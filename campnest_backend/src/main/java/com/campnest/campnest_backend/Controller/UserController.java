package com.campnest.campnest_backend.Controller;

import com.campnest.campnest_backend.dto.UserDTO;
import com.campnest.campnest_backend.Mapper.UserMapper;
import com.campnest.campnest_backend.model.User;
import com.campnest.campnest_backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 🔒 Helper: get logged-in email from JWT
    private String getCurrentUserEmail() {
        return (String) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }

    // Create user (probably not needed since registration is in /auth, but keeping for now)
    @PostMapping
    public UserDTO createUser(@RequestBody UserDTO dto) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setName(dto.getName());
        user.setSchool(dto.getSchool());
        user.setAge(dto.getAge());
        user.setGender(dto.getGender());

        user.setPasswordHash("temporary"); // placeholder

        User saved = userRepository.save(user);
        return UserMapper.toDTO(saved);
    }

    // Get all users (⚠️ Only admin should see this in future)
    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }

    // Get user by ID (must be yourself)
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getEmail().equals(getCurrentUserEmail())) {
            return ResponseEntity.status(403).body("You can only view your own profile");
        }

        return ResponseEntity.ok(UserMapper.toDTO(user));
    }

    // Update user by ID (must be yourself)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable UUID id, @RequestBody UserDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getEmail().equals(getCurrentUserEmail())) {
            return ResponseEntity.status(403).body("You can only update your own profile");
        }

        // Update fields if provided
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getName() != null) user.setName(dto.getName());
        if (dto.getSchool() != null) user.setSchool(dto.getSchool());
        if (dto.getAge() != null) user.setAge(dto.getAge());
        if (dto.getGender() != null) user.setGender(dto.getGender());

        User updated = userRepository.save(user);
        return ResponseEntity.ok(UserMapper.toDTO(updated));
    }

    // Delete user by ID (must be yourself)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getEmail().equals(getCurrentUserEmail())) {
            return ResponseEntity.status(403).body("You can only delete your own account");
        }

        userRepository.delete(user);
        return ResponseEntity.ok("User deleted successfully");
    }
}
