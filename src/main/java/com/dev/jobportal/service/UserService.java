package com.dev.jobportal.service;

import com.dev.jobportal.exception.UserNotFoundException;
import com.dev.jobportal.model.Applicant;
import com.dev.jobportal.model.User;
import com.dev.jobportal.model.dto.LoginRequestDto;
import com.dev.jobportal.model.dto.RegistrationRequestDto;
import com.dev.jobportal.repository.ApplicantRepository;
import com.dev.jobportal.repository.UserRepository;
import com.dev.jobportal.util.Constant;
import com.dev.jobportal.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ApplicantRepository applicantRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public ResponseEntity<?> recruiterRegistration(RegistrationRequestDto user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            log.warn("User already exists");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already exists, try login");
        }

        User savedUser = saveUserDetails(user, Constant.ROLE_RECRUITER);
        String token = jwtUtil.generateToken(savedUser);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("token", token, "message", "Recruiter registered successfully"));
    }

    public ResponseEntity<?> employeeRegistration(RegistrationRequestDto user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            log.warn("User already exists");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already exists, try login");
        }

        User savedUser = saveUserDetails(user, Constant.ROLE_EMPLOYEE);
        saveApplicantProfile(savedUser);
        String token = jwtUtil.generateToken(savedUser);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("token", token, "message", "Employee registered successfully"));
    }

    public ResponseEntity<?> login(LoginRequestDto credentials) {
        String email = credentials.getEmail();
        String password = credentials.getPassword();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found, please register!"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password");
        }
        log.info("Generating a token");
        String token = jwtUtil.generateToken(user);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("token", token, "message", "Login successful"));
    }

    private User saveUserDetails(RegistrationRequestDto user, String role){
        log.debug("Setting user details with ROLE: {}", role);
        User userEntity = new User();
        userEntity.setEmail(user.getEmail());
        userEntity.setUsername(user.getUsername());
        userEntity.setPassword(passwordEncoder.encode(user.getPassword()));
        userEntity.setRole(role);
        userEntity.setCreatedOn(LocalDateTime.now());
        return userRepository.save(userEntity);
    }

    private void saveApplicantProfile(User user){
        log.debug("Creating an applicant record");
        Applicant applicant = new Applicant();
        applicant.setUser(user);
        applicant.setCreatedOn(LocalDateTime.now());
        applicantRepository.save(applicant);
    }
}
