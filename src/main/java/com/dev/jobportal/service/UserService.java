package com.dev.jobportal.service;

import com.dev.jobportal.model.User;
import com.dev.jobportal.repository.UserRepository;
import com.dev.jobportal.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public ResponseEntity<String> recruiterRegistration(User user){
        if(userRepository.findByEmail(user.getEmail()).isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already exists, try login");
        }
        user.setRole("RECRUITER");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return ResponseEntity.ok("Recruiter registered successfully");
    }

    public ResponseEntity<String> employeeRegistration(@Valid User user) {
        if(userRepository.findByEmail(user.getEmail()).isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already exists, try login");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("EMPLOYEE");
        userRepository.save(user);
        return ResponseEntity.ok("Employee registered successfully");
    }

    public ResponseEntity<String> login(Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        Optional<User> user = userRepository.findByEmail(email);

        if(user.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unregistered user, please register!");
        }

        if(!passwordEncoder.matches(password, user.get().getPassword())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("invalid password");
        }

        String token = jwtUtil.generateToken(email);
        return ResponseEntity.status(HttpStatus.OK).body(token);
    }
}
