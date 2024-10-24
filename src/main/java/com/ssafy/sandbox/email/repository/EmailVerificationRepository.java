package com.ssafy.sandbox.email.repository;

import com.ssafy.sandbox.email.dto.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {
    Optional<EmailVerification> findByEmailAndVerifiedFalse(String email);
    Optional<EmailVerification> findByEmailAndVerificationCodeAndVerifiedFalse(String email, String verificationCode);

    @Modifying
    void deleteByExpiryDateLessThan(LocalDateTime expiryDate);
}