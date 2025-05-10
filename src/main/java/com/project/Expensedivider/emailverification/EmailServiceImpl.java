package com.project.Expensedivider.emailverification;

import com.project.Expensedivider.token.TokenRepository;
import com.project.Expensedivider.user.User;
import com.project.Expensedivider.user.UserException;
import com.project.Expensedivider.user.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
     private final EmailRepository emailRepository;
    private final JavaMailSender sender;
    private final UserRepository userRepository;
    @Value("${FRONTEND_HOST}")
    private String frontendHost;
    @Override
    public void generateemailToken(User user) throws MessagingException {
        String token = UUID.randomUUID().toString();
        var verificationEmail = Email.builder().token(token).expiryDate(LocalDateTime.now().plusMinutes(15)).user(user).build();
        emailRepository.save(verificationEmail);
        sendEmail(user, token);
    }
    @Override
    public void sendEmail(User user,String token) throws MessagingException {
        String verifyurl=String.format(
                "%s/authenticate/verify-email?token=%s",
                frontendHost, token
        );
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(user.getUsername());
        helper.setSubject("Confirm you E-Mail - EvenUp Registration");
        helper.setText("<html>" +
                        "<body>" +
                        "<h2>Dear "+ user.getName() + ",</h2>"
                        + "<br/> We're excited to have you get started in EvenUp. " +
                        "Please click on below link to confirm your account."
                        + "<br/> "  + verifyurl +" " +
                        "<br/> Regards,<br/>" +
                        "Evenup" +
                        "</body>" +
                        "</html>"
                , true);

        sender.send(message);
    }
    @Override
    public ResponseEntity<String> verifyEmail(String token) {
        System.out.print("verifyemail service entered");
        System.out.println(token);
        Email evToken = emailRepository.findByToken(token);
        if (evToken == null) return ResponseEntity.status(404).body("Invalid token");
        if (evToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(400).body("Token expired");
        }
        User user = evToken.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);
        emailRepository.delete(evToken);
        return ResponseEntity.ok("Email verified");
    }

     @Override
    public ResponseEntity<String> resendEmail(String email) throws MessagingException, UserException {
        Optional<User> user = userRepository.findByEmail(email);
        if(!user.isPresent()) throw new UserException("user not found");
        if (user.isPresent() &&  user.get().isEmailVerified()) return ResponseEntity.badRequest().body("User already verified");// clear old
        generateemailToken(user.get());
        return ResponseEntity.ok("Verification email sent");
    }

}
