package com.socialmedia.socialmediabackend.service;

import com.socialmedia.socialmediabackend.User;
import com.socialmedia.socialmediabackend.VerificationStatus;
import com.socialmedia.socialmediabackend.dto.AuthResponse;
import com.socialmedia.socialmediabackend.dto.LoginRequestDTO;
import com.socialmedia.socialmediabackend.dto.RegisterRequestDTO;
import com.socialmedia.socialmediabackend.dto.UserResponseDTO;
import com.socialmedia.socialmediabackend.dto.external.ExternalNotificationRequest;
import com.socialmedia.socialmediabackend.repository.UserRepository;
import com.socialmedia.socialmediabackend.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final ExternalApiService externalApiService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       EmailService emailService,
                       JwtService jwtService,
                       ExternalApiService externalApiService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.jwtService = jwtService;
        this.externalApiService = externalApiService;
    }

    @Transactional
    public AuthResponse register(RegisterRequestDTO request) {
        validateCredentials(request.email(), request.password());

        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        String verificationToken = UUID.randomUUID().toString();
        LocalDateTime tokenExpiry = LocalDateTime.now().plusHours(24);

        User user = new User();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setVerificationStatus(VerificationStatus.PENDING);
        user.setVerificationToken(verificationToken);
        user.setTokenExpiry(tokenExpiry);

        User savedUser = userRepository.save(user);
        emailService.sendVerificationEmail(savedUser.getEmail(), verificationToken);
        externalApiService.notifyExternalSystem(new ExternalNotificationRequest("USER_REGISTERED", savedUser.getEmail(), savedUser.getId()));

        return new AuthResponse("Registration successful. Please verify your email.", null, toUserResponse(savedUser));
    }

    @Transactional
    public AuthResponse resendVerificationEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getVerificationStatus() == VerificationStatus.VERIFIED) {
            throw new IllegalStateException("Email is already verified");
        }

        String verificationToken = UUID.randomUUID().toString();
        LocalDateTime tokenExpiry = LocalDateTime.now().plusHours(24);

        user.setVerificationToken(verificationToken);
        user.setTokenExpiry(tokenExpiry);

        userRepository.save(user);
        emailService.sendVerificationEmail(user.getEmail(), verificationToken);
        return new AuthResponse("Verification email resent successfully.", null, toUserResponse(user));
    }

    @Transactional
    public String verifyEmail(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Verification token is required");
        }

        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid verification token"));

        if (user.getTokenExpiry() != null && LocalDateTime.now().isAfter(user.getTokenExpiry())) {
            throw new IllegalArgumentException("Verification token has expired");
        }

        user.setVerificationStatus(VerificationStatus.VERIFIED);
        user.setVerificationToken(null);
        user.setTokenExpiry(null);

        userRepository.save(user);
        return "Email verified successfully";
    }

    public AuthResponse login(LoginRequestDTO request) {
        validateCredentials(request.email(), request.password());

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        if (user.getVerificationStatus() != VerificationStatus.VERIFIED) {
            throw new IllegalStateException("Please verify your email before logging in");
        }

        String jwt = jwtService.generateToken(user.getEmail(), user.getRole().name());
        return new AuthResponse("Login successful", jwt, toUserResponse(user));
    }

    private void validateCredentials(String email, String password) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("Email and password are required");
        }
    }

    private UserResponseDTO toUserResponse(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getEmail(),
                user.getRole().name(),
                user.getVerificationStatus().name()
        );
    }
}
