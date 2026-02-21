package com.socialmedia.socialmediabackend.controller;

import com.socialmedia.socialmediabackend.VerificationStatus;
import com.socialmedia.socialmediabackend.dto.UserResponseDTO;
import com.socialmedia.socialmediabackend.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserProfile(id));
    }

    @GetMapping
    public ResponseEntity<Page<UserResponseDTO>> getUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) VerificationStatus status,
            Pageable pageable
    ) {
        return ResponseEntity.ok(userService.getUsers(keyword, status, pageable));
    }
}
