package com.socialmedia.socialmediabackend.service;

import com.socialmedia.socialmediabackend.Comment;
import com.socialmedia.socialmediabackend.Post;
import com.socialmedia.socialmediabackend.User;
import com.socialmedia.socialmediabackend.dto.CommentResponseDTO;
import com.socialmedia.socialmediabackend.dto.CreateCommentRequestDTO;
import com.socialmedia.socialmediabackend.repository.CommentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostService postService;
    private final UserService userService;

    public CommentService(CommentRepository commentRepository, PostService postService, UserService userService) {
        this.commentRepository = commentRepository;
        this.postService = postService;
        this.userService = userService;
    }

    @Transactional
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public CommentResponseDTO addComment(Long postId, CreateCommentRequestDTO request, String email) {
        if (request.content() == null || request.content().isBlank()) {
            throw new IllegalArgumentException("Comment content is required");
        }

        User user = userService.getVerifiedUserOrThrow(email);
        Post post = postService.findPostById(postId);

        Comment comment = new Comment();
        comment.setContent(request.content());
        comment.setPost(post);
        comment.setUser(user);

        return toResponse(commentRepository.save(comment));
    }

    public Page<CommentResponseDTO> getCommentsByPost(Long postId, Pageable pageable) {
        return commentRepository.findByPostId(postId, pageable).map(this::toResponse);
    }

    private CommentResponseDTO toResponse(Comment comment) {
        return new CommentResponseDTO(
                comment.getId(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getPost().getId(),
                comment.getUser().getId(),
                comment.getUser().getEmail()
        );
    }
}
