package com.campnest.campnest_backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtp(String to, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setFrom("no-reply@campnest.com"); // ✅ Customize sender name/email
            message.setSubject("CampNest Email Verification Code");
            message.setText(
                    "Hello!\n\n" +
                            "Your 6-digit verification code is: " + otp + "\n\n" +
                            "This code will expire in 2 minutes.\n\n" +
                            "If you didn’t request this, please ignore this email.\n\n" +
                            "— The CampNest Team"
            );

            mailSender.send(message);
            System.out.println("✅ OTP sent successfully to " + to);
        } catch (Exception e) {
            System.err.println("❌ Failed to send OTP to " + to + ": " + e.getMessage());
        }
    }
}
