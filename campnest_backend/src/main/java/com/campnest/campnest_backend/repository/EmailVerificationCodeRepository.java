package com.campnest.campnest_backend.repository;


import com.campnest.campnest_backend.model.EmailVerificationCode;
import com.campnest.campnest_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EmailVerificationCodeRepository extends JpaRepository<EmailVerificationCode, UUID> {
    Optional<EmailVerificationCode> findByUser(User user);
    Optional<EmailVerificationCode> findByCode(String code);
}