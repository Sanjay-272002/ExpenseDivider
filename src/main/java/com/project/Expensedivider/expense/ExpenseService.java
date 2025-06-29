package com.project.Expensedivider.expense;

import com.project.Expensedivider.Groups.Group;
import com.project.Expensedivider.Groups.GroupException;
import com.project.Expensedivider.user.User;
import com.project.Expensedivider.user.UserException;

import java.math.BigDecimal;
import java.util.List;

public interface ExpenseService {

    public void createExpense(Group group, List<User> users) throws ExpenseException;
    public void removeExpense(Group group,  List<User> users) throws ExpenseException;
    public BigDecimal handleExpense(Group group, User user, List<User> users, BigDecimal amount) throws ExpenseException;
    BigDecimal getTotalExpense(String groupId) throws ExpenseException, GroupException;

    List<UserExpensedto> getindividualExpense(String groupId) throws ExpenseException,GroupException;

    UserExpensedto getUserExpense() throws ExpenseException, UserException;
}
