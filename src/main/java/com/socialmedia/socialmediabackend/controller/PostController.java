package com.socialmedia.socialmediabackend.controller;

import com.socialmedia.socialmediabackend.dto.CreatePostRequestDTO;
import com.socialmedia.socialmediabackend.dto.PostResponseDTO;
import com.socialmedia.socialmediabackend.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<PostResponseDTO> createPost(@RequestBody CreatePostRequestDTO request, Authentication authentication) {
        PostResponseDTO response = postService.createPost(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<PostResponseDTO>> getPosts(
            @RequestParam(required = false) Long userId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(postService.getPosts(userId, pageable));
    }

    @GetMapping("/popular")
    public ResponseEntity<Page<PostResponseDTO>> getPopularPosts(Pageable pageable) {
        return ResponseEntity.ok(postService.getPopularPosts(pageable));
    }

    @DeleteMapping("/{postId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Map<String, String>> deletePost(@PathVariable Long postId, Authentication authentication) {
        postService.deletePost(postId, authentication.getName());
        return ResponseEntity.ok(Map.of("message", "Post deleted successfully"));
    }
}
