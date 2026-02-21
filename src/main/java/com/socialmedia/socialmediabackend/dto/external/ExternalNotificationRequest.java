package com.socialmedia.socialmediabackend.dto.external;

public record ExternalNotificationRequest(String eventType, String email, Long userId) {
}
