package com.socialmedia.socialmediabackend.dto.external;

import java.time.LocalDateTime;

public record AnalyticsRequest(Long postId, Long userId, String userEmail, LocalDateTime createdAt) {
}
