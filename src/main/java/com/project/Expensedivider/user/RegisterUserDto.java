package com.project.Expensedivider.user;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserDto {
    private String userid;
    private String email;
    private String password;
    private String name;
    private String phonenumber;
    private String profileImage;
}
