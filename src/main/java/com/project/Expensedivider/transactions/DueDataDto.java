package com.project.Expensedivider.transactions;

import com.project.Expensedivider.user.RegisterUserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DueDataDto {
   private RegisterUserDto fromuser;
    private RegisterUserDto touser;
    private BigDecimal amount;
}
