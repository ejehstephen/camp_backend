package com.campnest.campnest_backend.Controller;

import com.campnest.campnest_backend.dto.UserDTO;
import com.campnest.campnest_backend.Mapper.UserMapper;
import com.campnest.campnest_backend.model.User;
import com.campnest.campnest_backend.repository.UserRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final Cloudinary cloudinary;

    private final UserRepository userRepository;

    public UserController(Cloudinary cloudinary, UserRepository userRepository) {
        this.cloudinary = cloudinary;
        this.userRepository = userRepository;
    }

    // ✅ Helper: get logged-in user's UUID
    private UUID getCurrentUserId() {
        return UUID.fromString((String) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal());
    }

    // 🔹 Get all users (for admin/future use)
    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }

    // 🔹 Get user by ID (must be yourself)
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getId().equals(getCurrentUserId())) {
            return ResponseEntity.status(403).body("You can only view your own profile");
        }

        return ResponseEntity.ok(UserMapper.toDTO(user));
    }

    // 🔹 Update user (must be yourself)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable UUID id, @RequestBody UserDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getId().equals(getCurrentUserId())) {
            return ResponseEntity.status(403).body("You can only update your own profile");
        }

        if (dto.getName() != null) user.setName(dto.getName());
        if (dto.getSchool() != null) user.setSchool(dto.getSchool());
        if (dto.getAge() != null) user.setAge(dto.getAge());
        if (dto.getGender() != null) user.setGender(dto.getGender());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());

        User updated = userRepository.save(user);
        return ResponseEntity.ok(UserMapper.toDTO(updated));
    }

    // 🔹 Delete user (must be yourself)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getId().equals(getCurrentUserId())) {
            return ResponseEntity.status(403).body("You can only delete your own account");
        }

        userRepository.delete(user);
        return ResponseEntity.ok("User deleted successfully");
    }

    @PostMapping("/profile/upload")
    public ResponseEntity<String> uploadProfilePicture(@RequestParam("file") MultipartFile file) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = (UUID) authentication.getPrincipal(); // This assumes your authentication principal is the UUID

        if (userId == null) {
            return ResponseEntity.status(401).body("User not authenticated.");
        }

        try {
            // Upload file to Cloudinary
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            "resource_type", "image",
                            "folder", "profile-pictures/" + userId
                    ));
            String imageUrl = (String) uploadResult.get("secure_url");

            // Find the user and update their profile picture
            return userRepository.findById(userId).map(user -> {
                user.setProfileImage(imageUrl);
                userRepository.save(user); // Use save to update the existing user
                return ResponseEntity.ok(imageUrl);
            }).orElseGet(() -> ResponseEntity.notFound().build());

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("❌ Upload failed");
        }
    }
}
