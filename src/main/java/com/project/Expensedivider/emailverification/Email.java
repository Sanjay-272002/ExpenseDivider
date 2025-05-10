package com.project.Expensedivider.emailverification;

import com.project.Expensedivider.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Email {


        @Id
        @GeneratedValue
        private Long id;

        private String token;
        private LocalDateTime expiryDate;

        @ManyToOne
        @JoinColumn(name="user_id")
        private User user;
}
