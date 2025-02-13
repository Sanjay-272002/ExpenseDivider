package com.project.Expensedivider.token;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenController {
    @GetMapping("/get")
    public String getname(){
        return "response working";
    }
}
