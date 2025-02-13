package com.project.Expensedivider.transactions;

import com.project.Expensedivider.expense.ExpenseException;
import com.project.Expensedivider.user.RegisterUserDto;
import com.project.Expensedivider.user.User;
import com.project.Expensedivider.user.UserException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface TransactionService {
   Transaction addTransaction(PostTransactionDto postTransactionDto) throws TransactionException, ExpenseException;

   List<ListTransactionDto> listgrouptransaction(String groupId) throws TransactionException;
    RegisterUserDto convertToRegisterUserDto(User user) throws TransactionException;

    List<DueDataDto> getDueList(String groupId) throws TransactionException;

    List<ListTransactionDto> getListUserTransactions() throws TransactionException, UserException;
}
