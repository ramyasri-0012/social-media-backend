package com.socialmedia.socialmediabackend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String verificationBaseUrl;
    private final String fromEmail;

    public EmailService(
            JavaMailSender mailSender,
            @Value("${app.verification.base-url:http://localhost:8080}") String verificationBaseUrl,
            @Value("${spring.mail.username}") String fromEmail
    ) {
        this.mailSender = mailSender;
        this.verificationBaseUrl = verificationBaseUrl;
        this.fromEmail = fromEmail;
    }

    public String buildVerificationUrl(String token) {
        return verificationBaseUrl + "/api/auth/verify?token=" + token;
    }

    public void sendVerificationEmail(String toEmail, String token) {
        String verificationUrl = buildVerificationUrl(token);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Verify your account");
        message.setText("Click this link to verify your account: " + verificationUrl);

        try {
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send verification email", e);
        }
    }
}
