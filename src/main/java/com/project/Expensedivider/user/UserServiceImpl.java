package com.project.Expensedivider.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.Expensedivider.Groups.Group;
import com.project.Expensedivider.Groups.GroupException;
import com.project.Expensedivider.Groups.GroupRepository;
import com.project.Expensedivider.emailverification.Email;
import com.project.Expensedivider.emailverification.EmailService;
import com.project.Expensedivider.token.Token;
import com.project.Expensedivider.token.TokenRepository;
import com.project.Expensedivider.token.TokenType;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.Cookie;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements  UserService{

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final GroupRepository groupRepository;
    private final EmailService emailService;
//    private final AuthenticationManager authenticationManager;
    @Override
    public void register(RegisterUserDto request) throws UserException, MessagingException {
        Optional<User> emailcheck = this.userRepository.findByEmail(request.getEmail());
        User passwordCheck=this.userRepository.findByPassword(request.getPassword());
        if(emailcheck.isPresent())throw  new UserException("Email already exists.Please enter new email");
        if(passwordCheck!=null)throw  new UserException("Password is taken.Try new password");
        var user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()))
                .phonenumber(request.getPhonenumber())
                .build();
       User userdata = this.userRepository.save(user);
       emailService.generateemailToken(userdata);

    }

    @Override
    public LoginResponse authenticate(LoginUserDto request,HttpServletResponse response) throws UserException {

//        authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                        request.getEmail(),
//                        request.getPassword()
//                )
//        );
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserException("User not found"));
        if(!user.isEmailVerified()) throw new UserException("User's email yet to be verified");
        var jwtToken="";
        var refreshToken ="";
        if (!BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            throw new UserException("Invalid password");
        }
        jwtToken = jwtService.generateToken(user);
        refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);

        setJwtCookie(response, jwtToken,false); // Set the access token in a cookie
        setRefreshTokenCookie(response, refreshToken,false); // Set the refresh token in a cookie
        System.out.print("authenticate calls");
        return LoginResponse.builder()
                .accessToken("Bearer token in cookie")
                .refreshToken("Bearer refresh token in cookie")
                .build();
    }

   public  void setJwtCookie(HttpServletResponse response, String token,boolean islogout) {
       String cookieHeader = String.format(
               "accessToken=%s; Max-Age=%d; Path=/; HttpOnly; Secure; SameSite=None",
               token,
               islogout ? 0 : 3600
       );

       response.addHeader("Set-Cookie", cookieHeader);
    }

    public void setRefreshTokenCookie(HttpServletResponse response, String refreshToken,boolean islogout) {
        int maxAge = islogout ? 0 : (30 * 24 * 60 * 60); // 30 days

        String cookieHeader = String.format(
                "refreshToken=%s; Max-Age=%d; Path=/; HttpOnly; Secure; SameSite=None",
                refreshToken,
                maxAge
        );

        response.addHeader("Set-Cookie", cookieHeader);
    }

    @Override
    public List<User> returnUserList() throws UserException {
        List<User> usersList=this.userRepository.findAll();
        return usersList != null ? usersList : Collections.emptyList();
    }

    @Override
    public void saveUserToken(User user, String jwtToken)  {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    @Override
    public String getAuthenticatedUserId()  {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }
        User currentUser = (User) authentication.getPrincipal();
        System.out.println(currentUser);
        return currentUser.getId();
    }

    @Override
    public List<Group> listgroups() throws GroupException{
        String userId=this.getAuthenticatedUserId();
        List<Group> groupData = this.groupRepository.findGroupsByUserId(userId);
        return groupData != null ? groupData : Collections.emptyList();
    }
    @Transactional
    @Override
    public RegisterUserDto getProfile(String id) throws UserException{
        User user=this.userRepository.findById(id).orElseThrow(() -> new UserException("User does not Exist"));
        String base64Image="";
        if (user.getProfileImage() != null) {
             base64Image = Base64.getEncoder().encodeToString(user.getProfileImage());
        }

        return RegisterUserDto.builder().userid(user.getId()).name(user.getName()).email(user.getEmail()).phonenumber(user.getPhonenumber()).profileImage(base64Image).build();
    }
    @Transactional
    @Override
    public void updateProfile(RegisterUserDto request) throws UserException {
        String userId=this.getAuthenticatedUserId();
        User user=this.userRepository.findById(userId).orElseThrow(() -> new UserException("User does not Exist"));
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getPhonenumber() != null) {
            user.setPhonenumber(request.getPhonenumber());
        }
        if (request.getProfileImage() != null) {
            byte[] decodedImage = Base64.getDecoder().decode(request.getProfileImage());
            user.setProfileImage(decodedImage);
        }

        this.userRepository.save(user);
    }

    @Override
    public JwtResponseDto handleOauthAuthentication(String name,String email,HttpServletResponse response) {
        User user = userRepository.findByEmail(email)
                .orElseGet(() ->
                        this.userRepository.save(User.builder()
                                .name(name)
                                .email(email)
                                .emailVerified(true)
                                .build())
                );


        String jwtToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);

//        setJwtCookie(response, jwtToken,false); // Set the access token in a cookie
//        setRefreshTokenCookie(response, refreshToken,false);
        return new JwtResponseDto(jwtToken,refreshToken);
    }



    @Override
    public void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }
    @Override
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = this.userRepository.findByEmail(userEmail)
                    .orElseThrow();
            if (!jwtService.isTokenValid(refreshToken, user)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid or expired refresh token.");
                return;
            }

                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                setJwtCookie(response, accessToken,false); // Set the access token in a cooki
                var authResponse = LoginResponse.builder()
                        .accessToken("accesstoken regenerated")
                        .refreshToken("refresh token is same")
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);

        }
    }

    @Override
    public void exchangeTokens(Map<String, String> tokens, HttpServletResponse response) {

        String accessToken = tokens.get("accessToken");
        String refreshToken = tokens.get("refreshToken");
        setJwtCookie(response, accessToken,false); // Set the access token in a cookie
        setRefreshTokenCookie(response, refreshToken,false);
    }

}
