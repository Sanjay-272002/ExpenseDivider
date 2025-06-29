package com.project.Expensedivider.Groups;

import com.project.Expensedivider.General.ApiResponse;
import com.project.Expensedivider.expense.ExpenseException;
import com.project.Expensedivider.transactions.TransactionException;
import com.project.Expensedivider.user.RegisterUserDto;
import com.project.Expensedivider.user.User;
import com.project.Expensedivider.user.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/group")
public class GroupController {
    private final GroupService groupService;
    private final GroupUserService groupUserService;

    @PostMapping("/creategroup")
    public ResponseEntity<Map<String, Object>> creategroup(
            @RequestBody Groupdto request
    ) throws GroupException, ExpenseException {
        return groupService.createGroup(request);
    }

    @PatchMapping("/joingroup")
    public ResponseEntity<ApiResponse> joingroup (
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

   @GetMapping("/user/groupdata")
    public ResponseEntity<UserGroupDataResponse> globalgroupdata() throws ExpenseException, UserException {
        return ResponseEntity.ok(this.groupUserService.getusergroupdata());
   }

    @GetMapping("/user/groupspecificdata")
    public ResponseEntity<GroupSpecificResponseDto> groupspecificdata(@RequestParam String groupid) throws ExpenseException, UserException, TransactionException, GroupException {
        return ResponseEntity.ok(this.groupUserService.getuserspecificgroupdata(groupid));
    }




}
