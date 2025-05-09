package com.project.Expensedivider.user;

import com.project.Expensedivider.Groups.Group;
import com.project.Expensedivider.Groups.GroupException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/userdata/")
@RequiredArgsConstructor
public class DataUserController {
    private final UserService userService;

    @GetMapping("getgroups/")
    public ResponseEntity<List<Group>> getGroups() throws GroupException {
        return ResponseEntity.ok(this.userService.listgroups());
    }


    @GetMapping("getprofile")
    public ResponseEntity<RegisterUserDto> getProfile( @RequestParam("id")  String id) throws UserException {
        return ResponseEntity.ok(this.userService.getProfile(id));
    }
    @PutMapping("edit-profile")
    public ResponseEntity<String> updateProfile(@RequestBody RegisterUserDto request)  throws UserException{
        this.userService.updateProfile(request);
        return ResponseEntity.ok("Profile updated successfully!");
    }

    @PostMapping("exchange")
    public ResponseEntity<?> sendtokens(@RequestBody Map<String, String> tokens, HttpServletResponse response) throws UserException{
        userService.exchangeTokens(tokens,response);
        return ResponseEntity.ok().body(Map.of("success", true));
    }

}
