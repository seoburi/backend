package com.ssafy.sandbox.email.service;

import com.ssafy.sandbox.email.dto.EmailVerification;
import com.ssafy.sandbox.email.dto.EmailVerificationDTO;
import com.ssafy.sandbox.email.repository.EmailVerificationRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender emailSender;
    private final EmailVerificationRepository emailVerificationRepository;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public boolean sendVerificationEmail(String toEmail) {
        try {
            // 이전 미인증 코드 삭제
            emailVerificationRepository.findByEmailAndVerifiedFalse(toEmail)
                    .ifPresent(emailVerificationRepository::delete);

            // 새로운 인증 코드 생성
            String verificationCode = generateVerificationCode();

            log.info("Sending verification : " + verificationCode);
            // 이메일 발송
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            log.info("Sending verification email to " + toEmail);
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("이메일 인증 코드");
            helper.setText("인증 코드: " + verificationCode);
            log.info("it's fine");
            emailSender.send(message);

            // DB에 저장
            EmailVerification verification = EmailVerification.builder()
                    .email(toEmail)
                    .verificationCode(verificationCode)
                    .expiryDate(LocalDateTime.now().plusMinutes(5))
                    .verified(false)
                    .build();

            emailVerificationRepository.save(verification);
            log.info("이메일 전송 성공: {}", toEmail);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            log.info("이메일 전송 실패: {}", e.getMessage());
            return false;
        }
    }

    public boolean verifyCode(EmailVerificationDTO verificationDto) {
        return emailVerificationRepository
                .findByEmailAndVerificationCodeAndVerifiedFalse(verificationDto.getEmail(), verificationDto.getAuthentication())
                .filter(verification -> verification.getExpiryDate().isAfter(LocalDateTime.now()))
                .map(verification -> {
                    verification.verify();
                    emailVerificationRepository.save(verification);
                    return true;
                })
                .orElse(false);
    }

    private String generateVerificationCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }
}