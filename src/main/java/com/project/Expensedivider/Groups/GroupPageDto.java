package com.project.Expensedivider.Groups;

import com.project.Expensedivider.expense.UserExpensedto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupPageDto {
    private String id;
    private String name;
    private String profileImage;
    private String category;
    private  int membercount;
    private BigDecimal amount;
}
