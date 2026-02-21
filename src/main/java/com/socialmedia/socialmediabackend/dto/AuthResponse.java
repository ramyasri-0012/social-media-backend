package com.socialmedia.socialmediabackend.dto;

public record AuthResponse(String message, String token, UserResponseDTO user) {
}
