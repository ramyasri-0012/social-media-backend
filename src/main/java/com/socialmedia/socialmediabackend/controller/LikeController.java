package com.socialmedia.socialmediabackend.controller;

import com.socialmedia.socialmediabackend.dto.LikeResponseDTO;
import com.socialmedia.socialmediabackend.service.LikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/likes")
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping("/posts/{postId}")
    public ResponseEntity<LikeResponseDTO> likePost(@PathVariable Long postId, Authentication authentication) {
        return ResponseEntity.ok(likeService.likePost(postId, authentication.getName()));
    }
}
