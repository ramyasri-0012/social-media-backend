package com.socialmedia.socialmediabackend.dto;

import java.time.LocalDateTime;

public record CommentResponseDTO(Long id, String content, LocalDateTime createdAt, Long postId, Long userId, String userEmail) {
}
