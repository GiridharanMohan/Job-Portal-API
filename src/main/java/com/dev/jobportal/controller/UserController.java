package com.dev.jobportal.controller;

import com.dev.jobportal.model.User;
import com.dev.jobportal.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/recruit/register")
    public ResponseEntity<String> recruiterRegistration(@RequestBody @Valid User user){
        return userService.recruiterRegistration(user);
    }

    @PostMapping("/employee/register")
    public ResponseEntity<String> employeeRegistration(@RequestBody @Valid User user){
        return userService.employeeRegistration(user);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> credentials){
        return userService.login(credentials);
    }

}
