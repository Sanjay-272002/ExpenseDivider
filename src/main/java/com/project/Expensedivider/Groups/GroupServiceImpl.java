package com.project.Expensedivider.Groups;

import com.project.Expensedivider.expense.ExpenseException;
import com.project.Expensedivider.expense.ExpenseService;
import com.project.Expensedivider.user.User;
import com.project.Expensedivider.user.UserException;
import com.project.Expensedivider.user.UserRepository;
import com.project.Expensedivider.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final ExpenseService expenseService;
    private final SecureRandom secureRandom=new SecureRandom();
    @Override
    public Group createGroup(Groupdto request) throws GroupException, ExpenseException {
        Set<String> uniqueUserIds = new HashSet<>(request.getUserids());
        List<User> usersList = this.userRepository.findAllById(uniqueUserIds);
        User hostuser=this.userRepository.findById(request.getHostuserId()).orElseThrow(() -> new GroupException("HostUser not found"));
        if (usersList.size() != uniqueUserIds.size()) {
            throw new IllegalArgumentException("Some user IDs are invalid.");
        }
        usersList.add(hostuser);
        int roomCode = 1000 + secureRandom.nextInt(9999);
        var group=Group.builder().name(request.getName()).user(usersList).roomcode(String.valueOf(roomCode)).hostuserId(request.getHostuserId()).build();
        Group groupObj=this.groupRepository.save(group);
        this.expenseService.createExpense(groupObj,usersList);
        return groupObj;
    }

    @Override
    public ResponseEntity<String> joinGroup(String roomcode) throws GroupException, ExpenseException {
        String userId=this.userService.getAuthenticatedUserId();
        User userdata = this.userRepository.findById(userId).orElseThrow(() -> new GroupException("User not found"));
        Group group=this.groupRepository.findByRoomcode(roomcode).orElseThrow(() -> new GroupException("Group not found"));
        group.getUser().add(userdata);
        Group groupObj=this.groupRepository.save(group);
        List<User> userDataList = new ArrayList<>();
        userDataList.add(userdata);
        this.expenseService.createExpense(groupObj,userDataList);
        return ResponseEntity.ok("Joined Group successfully");
    }

    @Override
    public ResponseEntity<String> addPeople(List<String> userids,String groupId) throws GroupException, ExpenseException {
        String userId=this.userService.getAuthenticatedUserId();
        Group group=this.groupRepository.findById(groupId).orElseThrow(() -> new GroupException("group not found"));
        this.checkUserPresence(userId,group);
        List<User> usersdata = userRepository.findAllById(userids);
        group.getUser().addAll(usersdata);
        Group groupObj=this.groupRepository.save(group);
        this.expenseService.createExpense(groupObj,usersdata);
        return ResponseEntity.ok("Added People to the group Successfully");

    }

    @Override
    public ResponseEntity<String> removePeople(List<String> userids,String groupId) throws GroupException, ExpenseException {
        String userId=this.userService.getAuthenticatedUserId();
        Group group=this.groupRepository.findById(groupId).orElseThrow(() -> new GroupException("User not found"));
        if(!(group.getHostuserId().equals(userId))){
            throw new GroupException("Authenticated user is not authorised to do remove");
        }
        List<User> usersdata = userRepository.findAllById(userids);
        group.getUser().removeAll(usersdata);
        Group groupObj=this.groupRepository.save(group);
        this.expenseService.removeExpense(groupObj,usersdata);
        return ResponseEntity.ok("Removed People From the group Successfully");
    }

    @Override
    public ResponseEntity<String> editgroup(String name,String groupId) throws GroupException{
        String userId=this.userService.getAuthenticatedUserId();
        Group group=this.groupRepository.findById(groupId).orElseThrow(() -> new GroupException("User not found"));
        this.checkUserPresence(userId,group);
        group.setName(name);
        this.groupRepository.save(group);
        return ResponseEntity.ok("Edited name Successfully");
    }

    @Override
    public boolean checkUserPresence(String userId,Group group) throws GroupException{
        List<User> users=group.getUser();
        boolean isUserInGroup = users.stream()
                .anyMatch(user -> user.getId().equals(userId));
        if (!isUserInGroup) {
            throw new GroupException("Authenticated user is not a member of this group");
        }
        return true;
    }

    @Override
    public ResponseEntity<String> addUser(String groupId) throws GroupException, ExpenseException {
        String userId=this.userService.getAuthenticatedUserId();
        Group group=this.groupRepository.findById(groupId).orElseThrow(() -> new GroupException("Group not found"));
       // this.checkUserPresence(userId,group);
        User userdata = userRepository.findById(userId).orElseThrow(() -> new GroupException("User not found"));;
        group.getUser().add(userdata);
        Group groupObj=this.groupRepository.save(group);
        List<User> userDataList = new ArrayList<>();
        userDataList.add(userdata);
        this.expenseService.createExpense(groupObj,userDataList);
        return ResponseEntity.ok("You are now added to the group Successfully");
    }

    @Override
    public String checkUser(String groupId) throws GroupException {
        String userId=this.userService.getAuthenticatedUserId();
        Group group=this.groupRepository.findById(groupId).orElseThrow(() -> new GroupException("Group not found"));
       boolean isUserPresent= this.checkUserPresence(userId,group);
        return isUserPresent?"User is present":"User is not in the group";
    }
}
