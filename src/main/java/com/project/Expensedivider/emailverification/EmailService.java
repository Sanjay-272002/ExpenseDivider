package com.project.Expensedivider.emailverification;

import com.project.Expensedivider.user.User;
import com.project.Expensedivider.user.UserException;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;

public interface EmailService {

    void generateemailToken(User user) throws MessagingException;

    void sendEmail(User user ,String Token) throws MessagingException;
    public ResponseEntity<String> verifyEmail(String token);
    public ResponseEntity<String> resendEmail(String email) throws MessagingException, UserException;
}
