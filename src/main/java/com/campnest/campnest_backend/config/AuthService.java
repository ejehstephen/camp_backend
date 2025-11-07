package com.campnest.campnest_backend.config;

import com.campnest.campnest_backend.model.EmailVerificationCode;
import com.campnest.campnest_backend.model.User;
import com.campnest.campnest_backend.repository.EmailVerificationCodeRepository;
import com.campnest.campnest_backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final EmailVerificationCodeRepository codeRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, EmailVerificationCodeRepository codeRepository, EmailService emailService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.codeRepository = codeRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    // ✅ Register new user and send OTP
    public User registerUser(String email, String password, String name, String school,
                               Integer age, String gender) {

        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already in use");
        }

        // 🔹 Save full user data
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setName(name);
        user.setSchool(school);
        user.setAge(age);
        user.setGender(gender);

//        user.setPhoneNumber(phoneNumber);
//        user.setProfileImage(profileImage);
        user.setEnabled(true);

        userRepository.save(user);

        // 🔹 Send OTP for email verification
//        sendOtpToUser(user);
        return user;
    }

    // ✅ Resend OTP using UUID
    public void resendOtp(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        sendOtpToUser(user);
    }

    // ✅ Verify OTP using UUID
    public String verifyOtp(UUID userId, String code) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        EmailVerificationCode verification = codeRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("No verification code found"));

        if (verification.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Verification code expired");
        }

        if (!verification.getCode().equals(code)) {
            throw new RuntimeException("Invalid verification code");
        }

        user.setEnabled(true);
        userRepository.save(user);
        codeRepository.delete(verification);

        return "Email verified successfully.";
    }

    // 🔄 Helper: Send new OTP
    public void sendOtpToUser(User user) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(10);

        // Remove old OTP if exists
        codeRepository.findByUser(user).ifPresent(codeRepository::delete);

        // Save new OTP
        EmailVerificationCode verification = new EmailVerificationCode();
        verification.setUser(user);
        verification.setCode(otp);
        verification.setExpiryDate(expiry);
        codeRepository.save(verification);

        // Send to email
        emailService.sendOtp(user.getEmail(), otp);
    }

    public User getUserByUuid(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public boolean passwordMatches(String raw, String encoded) {
        return passwordEncoder.matches(raw, encoded);
    }
}
