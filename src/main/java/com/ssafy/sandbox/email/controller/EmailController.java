package com.ssafy.sandbox.email.controller;

import com.ssafy.sandbox.email.dto.EmailDTO;
import com.ssafy.sandbox.email.dto.EmailVerificationDTO;
import com.ssafy.sandbox.email.dto.EmailVerificationResponse;
import com.ssafy.sandbox.email.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class EmailController {
    private final EmailService emailService;

    @PostMapping
    public ResponseEntity<Map<String, Boolean>> sendVerificationEmail(@RequestBody EmailDTO emailDto) {
        boolean isOk = emailService.sendVerificationEmail(emailDto.getEmail());
        log.info("Email verification email sent: {}", isOk);
        return ResponseEntity.ok(Collections.singletonMap("isOk", isOk));
    }

    @PostMapping("/authentication")
    public EmailVerificationResponse verifyEmail(@RequestBody EmailVerificationDTO verificationDto) {
        boolean isSuccess = emailService.verifyCode(verificationDto);

        return new EmailVerificationResponse(isSuccess);
    }
}