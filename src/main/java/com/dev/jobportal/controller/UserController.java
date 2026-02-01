package com.dev.jobportal.controller;

import com.dev.jobportal.model.dto.LoginRequestDto;
import com.dev.jobportal.model.dto.RegistrationRequestDto;
import com.dev.jobportal.service.UserService;
import com.dev.jobportal.util.Constant;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/recruit/register")
    public ResponseEntity<?> recruiterRegistration(@RequestBody @Valid RegistrationRequestDto user) {
        log.info(Constant.SIGN_UP_DETAILS, Constant.ROLE_RECRUITER, user.getEmail());
        return userService.recruiterRegistration(user);
    }

    @PostMapping("/employee/register")
    public ResponseEntity<?> employeeRegistration(@RequestBody @Valid RegistrationRequestDto user) {
        log.info(Constant.SIGN_UP_DETAILS, Constant.ROLE_EMPLOYEE, user.getEmail());
        return userService.employeeRegistration(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequestDto credentials) {
        log.info("Processing login request. E-mail: {}", credentials.getEmail());
        return userService.login(credentials);
    }

}
