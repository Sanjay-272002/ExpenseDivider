package com.project.Expensedivider.Groups;

import com.project.Expensedivider.expense.ExpenseException;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.List;

public interface GroupService {
    Group createGroup(Groupdto request) throws GroupException, ExpenseException;

    ResponseEntity<String> joinGroup(String roomcode) throws GroupException, ExpenseException;

    ResponseEntity<String> addPeople(List<String> userids,String groupid) throws GroupException, ExpenseException;

    ResponseEntity<String> removePeople(List<String> userids,String groupId) throws GroupException, ExpenseException;

    ResponseEntity<String> editgroup(String name,String groupId) throws GroupException;

    boolean checkUserPresence(String userId,Group group) throws GroupException;

    ResponseEntity<String> addUser(String groupId) throws GroupException, ExpenseException;

    String checkUser(String groupId) throws GroupException;
}
