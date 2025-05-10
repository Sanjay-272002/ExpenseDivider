package com.project.Expensedivider.emailverification;

import com.project.Expensedivider.user.UserException;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class EmailController {
    private final EmailService emailService;
    @PostMapping("/verifyemail")
    public ResponseEntity<String> verifyEmail(@RequestBody Map<String, String> request ){
        System.out.println("email controller"+" "+request.get("token"));
        return  this.emailService.verifyEmail(request.get("token"));
    }

    @PostMapping("/resendverification")
    public ResponseEntity<String> resendemail(@RequestBody Map<String, String> request ) throws MessagingException, UserException {
        return  this.emailService.resendEmail(request.get("email"));
    }

}
