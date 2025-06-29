package com.project.Expensedivider.transactions;

import com.project.Expensedivider.General.ApiResponse;
import com.project.Expensedivider.expense.ExpenseException;
import com.project.Expensedivider.user.RegisterUserDto;
import com.project.Expensedivider.user.User;
import com.project.Expensedivider.user.UserException;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface TransactionService {
   Transaction addTransaction(PostTransactionDto postTransactionDto) throws TransactionException, ExpenseException;

   List<ListTransactionDateDto> listgrouptransaction(String groupId) throws TransactionException;
    RegisterUserDto convertToRegisterUserDto(User user) throws TransactionException;

    List<DueDataDto> getDueList(String groupId) throws TransactionException;

    ApiResponse getListUserTransactions(LocalDate date) throws TransactionException, UserException;
}
