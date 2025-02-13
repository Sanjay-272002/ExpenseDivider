package com.project.Expensedivider.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface  UserRepository extends JpaRepository<User,String> {
    Optional<User> findByEmail(String email);
    User findByPassword(String password);
    User findByEmailAndPassword(String email, String password);
}
