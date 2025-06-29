package com.project.Expensedivider.transactions;

import com.project.Expensedivider.Groups.Group;
import com.project.Expensedivider.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,String> {
   List<Transaction> findByGroup(Group group);
   List<Transaction> findByFromuser(User user);
   @Query("SELECT t FROM Transaction t WHERE t.fromuser = :user AND EXTRACT(MONTH FROM t.createdAt) = :month AND EXTRACT(YEAR FROM t.createdAt) = :year")
   List<Transaction> findByFromuserAndMonth(@Param("user") User user,
                                            @Param("month") int month,
                                            @Param("year") int year);
}
