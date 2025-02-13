package com.project.Expensedivider.Groups;

import com.project.Expensedivider.expense.ExpenseException;
import com.project.Expensedivider.user.RegisterUserDto;
import com.project.Expensedivider.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/group")
public class GroupController {
    private final GroupService groupService;

    @PostMapping("/creategroup")
    public ResponseEntity<Group> creategroup(
            @RequestBody Groupdto request
    ) throws GroupException, ExpenseException {
        return ResponseEntity.ok(groupService.createGroup(request));
    }

    @PatchMapping("/joingroup")
    public ResponseEntity<String> joingroup (
            @RequestParam("roomcode") String roomcode
    ) throws GroupException, ExpenseException {

        return this.groupService.joinGroup(roomcode);
    }
    @PatchMapping("/addPeople")
    public ResponseEntity<String> addPeople(
            @RequestBody List<String> userids,@RequestParam String groupid
    ) throws GroupException, ExpenseException {
        return this.groupService.addPeople(userids,groupid);
    }

    @DeleteMapping("/removePeople")
    public ResponseEntity<String> RemovePeople(
            @RequestBody List<String> userids,@RequestParam String groupId
    ) throws GroupException, ExpenseException {
        return groupService.removePeople(userids,groupId);
    }
    @PatchMapping("/editGroupName")
    public ResponseEntity<String> editgroupName(
           @RequestParam String name,@RequestParam String groupId
    ) throws GroupException {
        return groupService.editgroup(name,groupId);
    }

    @PatchMapping("/joinuser")
    public ResponseEntity<String> addUser(
            @RequestParam String groupId
    ) throws GroupException, ExpenseException {
        return groupService.addUser(groupId);
    }
    @PatchMapping("/checkuser")
    public ResponseEntity<String> checkUserAvailability(
            @RequestParam String groupId
    ) throws GroupException {
        return ResponseEntity.ok(groupService.checkUser(groupId));
    }





}
