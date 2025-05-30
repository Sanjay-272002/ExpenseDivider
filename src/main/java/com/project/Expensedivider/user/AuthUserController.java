package com.project.Expensedivider.user;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor //creates constructor for final and not null references
public class AuthUserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register ( @RequestBody RegisterUserDto request ) throws UserException, MessagingException {
        userService.register(request);
        return ResponseEntity.ok("User Registered Successfully");
    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate( @RequestBody LoginUserDto request,HttpServletResponse response) throws UserException{

        return ResponseEntity.ok(userService.authenticate(request,response));
    }

    @PostMapping("/refresh-token")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        userService.refreshToken(request, response);
    }
    @GetMapping("/getdata")
    public List<User> getuserdata() throws UserException{
        return userService.returnUserList();
   }


}
