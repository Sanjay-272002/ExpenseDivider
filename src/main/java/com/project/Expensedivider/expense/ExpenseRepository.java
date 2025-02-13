package com.project.Expensedivider.expense;

import com.project.Expensedivider.Groups.Group;
import com.project.Expensedivider.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense,String> {
    List<Expense> findByUser(User user);
    List<Expense> findByGroup(Group group);
    Expense findByGroupAndUser(Group group,User user);

}
