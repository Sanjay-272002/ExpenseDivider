package com.project.Expensedivider.Groups;

import com.project.Expensedivider.expense.UserExpensedto;
import com.project.Expensedivider.transactions.DueDataDto;
import com.project.Expensedivider.transactions.ListTransactionDateDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupSpecificResponseDto {
   private String groupName;
   private String roomCode;
   private BigDecimal totalexpenseamount;
   private List<DueDataDto> dueData;
   private List<ListTransactionDateDto> transactionData;
   private List<UserExpensedto> individualExpense;
   private  UserExpensedto userexpense;
}
