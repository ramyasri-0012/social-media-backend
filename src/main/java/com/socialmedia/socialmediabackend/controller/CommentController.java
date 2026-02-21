package com.socialmedia.socialmediabackend.controller;

import com.socialmedia.socialmediabackend.dto.CommentResponseDTO;
import com.socialmedia.socialmediabackend.dto.CreateCommentRequestDTO;
import com.socialmedia.socialmediabackend.service.CommentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/posts/{postId}")
    public ResponseEntity<CommentResponseDTO> addComment(@PathVariable Long postId,
                                                         @RequestBody CreateCommentRequestDTO request,
                                                         Authentication authentication) {
        CommentResponseDTO response = commentService.addComment(postId, request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<Page<CommentResponseDTO>> getComments(@PathVariable Long postId, Pageable pageable) {
        return ResponseEntity.ok(commentService.getCommentsByPost(postId, pageable));
    }
}
