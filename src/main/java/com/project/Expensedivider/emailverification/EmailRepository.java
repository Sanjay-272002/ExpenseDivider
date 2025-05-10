package com.project.Expensedivider.emailverification;

import com.project.Expensedivider.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailRepository extends JpaRepository<Email,Long> {

     void deleteByUser(User user);

    Email findByToken(String token);
}
