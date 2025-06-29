package com.project.Expensedivider.Groups;

import com.project.Expensedivider.expense.*;
import com.project.Expensedivider.transactions.DueDataDto;
import com.project.Expensedivider.transactions.ListTransactionDateDto;
import com.project.Expensedivider.transactions.TransactionException;
import com.project.Expensedivider.transactions.TransactionService;
import com.project.Expensedivider.user.User;
import com.project.Expensedivider.user.UserException;
import com.project.Expensedivider.user.UserRepository;
import com.project.Expensedivider.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupUserServiceImpl implements GroupUserService{

    private final UserService userService;
    private final ExpenseService expenseService;
    private final TransactionService transactionService;
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    @Override
    public UserGroupDataResponse getusergroupdata() throws ExpenseException, UserException {
        String userId = userService.getAuthenticatedUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException("User not found"));
        UserExpensedto userExpenseDto = expenseService.getUserExpense();

        List<Expense> expenseList = expenseRepository.findByUser(user);

        List<GroupPageDto> groupPageDtoList = new ArrayList<>();

        for (Expense expense : expenseList) {
            Group group = expense.getGroup();
            if (group == null) continue;

            byte[] imageBytes = group.getGroupImage();
            String base64Image = (imageBytes != null)
                    ? Base64.getEncoder().encodeToString(imageBytes)
                    : "";
            int memberCount = (group.getUser() != null) ? group.getUser().size() : 0;
            GroupPageDto groupPageDto = GroupPageDto.builder()
                    .id(group.getId())
                    .name(group.getName())
                    .profileImage(base64Image)
                    .category(group.getCategory() != null ? group.getCategory().getName() : null)
                    .amount(expense.getNetamount())
                    .membercount(memberCount)
                    .build();

            groupPageDtoList.add(groupPageDto);
        }

        return UserGroupDataResponse.builder().groupPageDtoList(groupPageDtoList).userExpenseDto(userExpenseDto).build();
    }

    @Override
    public GroupSpecificResponseDto getuserspecificgroupdata(String groupid) throws TransactionException, ExpenseException, GroupException, UserException {
        List<DueDataDto> duedata=this.transactionService.getDueList(groupid);
        List<ListTransactionDateDto> transactionData=this.transactionService.listgrouptransaction(groupid);
        List<UserExpensedto> individualExpense=this.expenseService.getindividualExpense(groupid);
        BigDecimal grouptotalExpense=this.expenseService.getTotalExpense(groupid);
        String groupName=this.groupRepository.findById(groupid).get().getName();
        String roomcode = this.groupRepository.findById(groupid).get().getRoomcode();
        UserExpensedto userexpense=this.expenseService.getUserExpense();
        var finaldata=GroupSpecificResponseDto.builder().groupName(groupName).dueData(duedata).transactionData(transactionData).individualExpense(individualExpense).totalexpenseamount(grouptotalExpense).userexpense(userexpense).roomCode(roomcode).build();
        return finaldata;
    }
}
