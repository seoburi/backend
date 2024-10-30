package com.ssafy.sandbox.email.service;


import com.ssafy.sandbox.email.repository.EmailVerificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@EnableScheduling
@Service
@RequiredArgsConstructor
public class EmailScheduler {
    private final EmailVerificationRepository emailVerificationRepository;

    @Scheduled(fixedRate = 10000)
    @Transactional
    public void cleanupExpiredEmails() {
        emailVerificationRepository.deleteByExpiryDateLessThan(LocalDateTime.now());
        log.info("Cleaning up expired emails");
    }
}
