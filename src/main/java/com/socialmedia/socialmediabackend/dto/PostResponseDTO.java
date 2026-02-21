package com.socialmedia.socialmediabackend.dto;

import java.time.LocalDateTime;

public record PostResponseDTO(Long id, String content, LocalDateTime createdAt, Long userId, String userEmail, long likeCount) {
}
