package com.socialmedia.socialmediabackend.dto;

public record LikeResponseDTO(Long postId, Long userId, long likeCount, String message) {
}
