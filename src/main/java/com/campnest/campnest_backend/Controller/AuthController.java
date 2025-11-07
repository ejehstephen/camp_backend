package com.campnest.campnest_backend.Controller;

import com.campnest.campnest_backend.config.AuthService;
import com.campnest.campnest_backend.dto.*;
import com.campnest.campnest_backend.model.User;
import com.campnest.campnest_backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*") // Allow Flutter access
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService, UserRepository userRepository, JwtUtil jwtUtil) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    // ✅ REGISTER — sends OTP via email
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        User user = authService.registerUser(
                request.getEmail(),
                request.getPassword(),
                request.getName(),
                request.getSchool(),
                request.getAge(),
                request.getGender()
        );

        String token = jwtUtil.generateToken(user); // generate token if needed
        String message = "Registration successful. Check your email for a 6-digit code.";

        AuthResponseWithUser response = new AuthResponseWithUser(message, token, user);
        return ResponseEntity.ok(response);
    }


    // ✅ VERIFY EMAIL OTP
    @PostMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestBody VerifyRequest request) {
        try {
            String message = authService.verifyOtp(request.getUuid(), request.getCode());

            // Generate JWT token after successful verification
            User user = authService.getUserByUuid(request.getUuid());
            String token = jwtUtil.generateToken(user);

            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    // ✅ RESEND OTP
    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOtp(@RequestBody VerifyRequest request) {
        try {
            authService.resendOtp(request.getUuid());
            return ResponseEntity.ok("OTP resent successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    // ✅ LOGIN — only if verified
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isEnabled()) {
            return ResponseEntity.status(403).body("Please verify your email before logging in.");
        }

        if (!authService.passwordMatches(request.getPassword(), user.getPasswordHash())) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user);
        return ResponseEntity.ok(new AuthResponse(token));
    }
}
