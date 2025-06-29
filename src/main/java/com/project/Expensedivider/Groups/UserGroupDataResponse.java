package com.project.Expensedivider.Groups;

import com.project.Expensedivider.expense.UserExpensedto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserGroupDataResponse {
    private List<GroupPageDto> groupPageDtoList;
    private UserExpensedto userExpenseDto;
}
