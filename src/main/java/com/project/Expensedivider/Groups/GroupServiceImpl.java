package com.project.Expensedivider.Groups;

import com.project.Expensedivider.General.ApiResponse;
import com.project.Expensedivider.category.Categorrepository;
import com.project.Expensedivider.category.Category;
import com.project.Expensedivider.expense.ExpenseException;
import com.project.Expensedivider.expense.ExpenseService;
import com.project.Expensedivider.user.User;
import com.project.Expensedivider.user.UserRepository;
import com.project.Expensedivider.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.*;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final ExpenseService expenseService;
    private final Categorrepository categorrepository;
    private final SecureRandom secureRandom=new SecureRandom();
    @Override
    public ResponseEntity<Map<String, Object>> createGroup(Groupdto request) throws GroupException, ExpenseException {
        Set<String> uniqueUserIds = new HashSet<>(request.getFriends());
        List<User> usersList = new ArrayList<>(this.userRepository.findAllById(uniqueUserIds));
        String userId=this.userService.getAuthenticatedUserId();
        User hostuser=this.userRepository.findById(userId).orElseThrow(() -> new GroupException("HostUser not found"));
        if (usersList.size() != uniqueUserIds.size()) {
            throw new IllegalArgumentException("Some user IDs are invalid.");
        }
        System.out.println(usersList+" "+uniqueUserIds);
        usersList.add(hostuser);
        int roomCode = 1000 + secureRandom.nextInt(9999);
        Category category=categorrepository.findById(request.getCategory()).get();
        var group=Group.builder().name(request.getName()).user(usersList).roomcode(String.valueOf(roomCode)).hostuserId(userId).category(category).typeenum(request.getType()).build();
        Group groupObj=this.groupRepository.save(group);
        this.expenseService.createExpense(groupObj,usersList);
        Map<String, Object> response = new HashMap<>();
        response.put("groupId", groupObj.getId());
        response.put("code", groupObj.getRoomcode());
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ApiResponse> joinGroup(String roomcode) throws GroupException, ExpenseException {
        String userId=this.userService.getAuthenticatedUserId();
        User userdata = this.userRepository.findById(userId).orElseThrow(() -> new GroupException("User not found"));
        Group group=this.groupRepository.findByRoomcode(roomcode).orElseThrow(() -> new GroupException("Group not found"));
        group.getUser().add(userdata);
        Group groupObj=this.groupRepository.save(group);
        List<User> userDataList = new ArrayList<>();
        userDataList.add(userdata);
        this.expenseService.createExpense(groupObj,userDataList);
        var response=ApiResponse.builder().success(true).message("User Joined Group Successfully").data(group.getId()).build();
        return ResponseEntity.ok(response);
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
