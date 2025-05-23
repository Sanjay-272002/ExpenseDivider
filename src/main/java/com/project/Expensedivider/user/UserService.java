package com.project.Expensedivider.user;

import com.project.Expensedivider.Groups.Group;
import com.project.Expensedivider.Groups.GroupException;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;


public interface UserService {


     void register(RegisterUserDto request) throws UserException, MessagingException;

    LoginResponse authenticate(LoginUserDto request,HttpServletResponse response) throws UserException;
    List<User> returnUserList() throws UserException;
    void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;
     void revokeAllUserTokens(User user) ;
     void saveUserToken(User user, String jwtToken) ;
     String getAuthenticatedUserId();
     List<Group> listgroups() throws GroupException;
     void setJwtCookie(HttpServletResponse response, String token,boolean islogout);
     void setRefreshTokenCookie(HttpServletResponse response, String refreshToken,boolean islogout);
    RegisterUserDto getProfile(String id) throws UserException;

    void updateProfile(RegisterUserDto request) throws UserException;

    JwtResponseDto handleOauthAuthentication(String name,String email,HttpServletResponse response);

    void exchangeTokens(Map<String, String> tokens, HttpServletResponse response);
}
