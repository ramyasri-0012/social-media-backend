package com.socialmedia.socialmediabackend.service;

import com.socialmedia.socialmediabackend.User;
import com.socialmedia.socialmediabackend.VerificationStatus;
import com.socialmedia.socialmediabackend.dto.UserResponseDTO;
import com.socialmedia.socialmediabackend.exception.ResourceNotFoundException;
import com.socialmedia.socialmediabackend.repository.UserRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Cacheable(value = "userProfiles", key = "#userId")
    public UserResponseDTO getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return toResponse(user);
    }

    public Page<UserResponseDTO> getUsers(String keyword, VerificationStatus status, Pageable pageable) {
        if (keyword != null && !keyword.isBlank()) {
            return userRepository.searchByEmail(keyword, pageable).map(this::toResponse);
        }
        if (status != null) {
            return userRepository.findByVerificationStatus(status, pageable).map(this::toResponse);
        }
        return userRepository.findAll(pageable).map(this::toResponse);
    }

    public User getVerifiedUserOrThrow(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getVerificationStatus() != VerificationStatus.VERIFIED) {
            throw new AccessDeniedException("Only verified users can perform this action");
        }

        return user;
    }

    private UserResponseDTO toResponse(User user) {
        return new UserResponseDTO(user.getId(), user.getEmail(), user.getRole().name(), user.getVerificationStatus().name());
    }
}
