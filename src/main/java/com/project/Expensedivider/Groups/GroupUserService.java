package com.project.Expensedivider.Groups;

import com.project.Expensedivider.expense.ExpenseException;
import com.project.Expensedivider.transactions.TransactionException;
import com.project.Expensedivider.user.UserException;

public interface GroupUserService {

    UserGroupDataResponse getusergroupdata() throws ExpenseException, UserException;

    GroupSpecificResponseDto getuserspecificgroupdata(String groupid) throws TransactionException, ExpenseException, GroupException, UserException;
}
