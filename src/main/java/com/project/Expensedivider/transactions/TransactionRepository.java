package com.project.Expensedivider.transactions;

import com.project.Expensedivider.Groups.Group;
import com.project.Expensedivider.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,String> {
   List<Transaction> findByGroup(Group group);
   List<Transaction> findByFromuser(User user);
}
