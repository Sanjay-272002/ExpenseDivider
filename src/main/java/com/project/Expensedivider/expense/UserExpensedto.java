package com.project.Expensedivider.expense;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserExpensedto {
    private String user_name;
   private BigDecimal totalSpent ;
    private BigDecimal totalOwed;
   private  BigDecimal totalOwe;
   private  BigDecimal totalExpense;
}
