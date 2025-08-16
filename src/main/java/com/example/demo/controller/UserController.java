package com.example.demo.controller;

import com.example.demo.dto.UserDTO;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import com.example.demo.config.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*") // Allow all origins for CORS
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers().stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(UserDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // @PostMapping("/register")
    // public ResponseEntity<UserDTO> register(@RequestBody UserDTO userDTO) {
    //     User createdUser = userService.createUser(userDTO);
    //     return ResponseEntity.ok(UserDTO.fromEntity(createdUser));
    // }
    @PostMapping("/register")
public ResponseEntity<?> register(@RequestBody UserDTO userDTO) {
    if (userService.getUserByEmail(userDTO.getEmail()) != null) {
        return ResponseEntity
                .badRequest()
                .body(Map.of("message", "Email already exists"));
    }
    try {
        User createdUser = userService.createUser(userDTO);
        return ResponseEntity.ok(UserDTO.fromEntity(createdUser));
    } catch (Exception e) {
        return ResponseEntity
                .badRequest()
                .body(Map.of("message", "Registration failed: " + e.getMessage()));
    }
}


    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");
        User user = userService.login(email, password);

        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return ResponseEntity.ok(Map.of(
                "token", token,
                "role", user.getRole().name(),
                "email", user.getEmail()
        ));
    }
}
