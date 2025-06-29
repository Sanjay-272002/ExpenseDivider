package com.project.Expensedivider.Groups;

import com.project.Expensedivider.General.ApiResponse;
import com.project.Expensedivider.expense.ExpenseException;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface GroupService {
    ResponseEntity<Map<String, Object>> createGroup(Groupdto request) throws GroupException, ExpenseException;

    ResponseEntity<ApiResponse> joinGroup(String roomcode) throws GroupException, ExpenseException;

    ResponseEntity<String> addPeople(List<String> userids,String groupid) throws GroupException, ExpenseException;

    ResponseEntity<String> removePeople(List<String> userids,String groupId) throws GroupException, ExpenseException;

    ResponseEntity<String> editgroup(String name,String groupId) throws GroupException;

    boolean checkUserPresence(String userId,Group group) throws GroupException;

    ResponseEntity<String> addUser(String groupId) throws GroupException, ExpenseException;

    String checkUser(String groupId) throws GroupException;
}
