package com.project.Expensedivider.Groups;

import com.project.Expensedivider.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GroupRepository  extends JpaRepository<Group,String> {
    Optional<Group> findByRoomcode(String roomcode);

    @Query("SELECT g FROM Group g JOIN g.user u WHERE u.id = :userId")
    List<Group> findGroupsByUserId(String userId);

}
