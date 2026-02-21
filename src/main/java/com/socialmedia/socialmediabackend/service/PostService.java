package com.socialmedia.socialmediabackend.service;

import com.socialmedia.socialmediabackend.Post;
import com.socialmedia.socialmediabackend.User;
import com.socialmedia.socialmediabackend.UserRole;
import com.socialmedia.socialmediabackend.dto.CreatePostRequestDTO;
import com.socialmedia.socialmediabackend.dto.PostResponseDTO;
import com.socialmedia.socialmediabackend.dto.external.AnalyticsRequest;
import com.socialmedia.socialmediabackend.exception.ResourceNotFoundException;
import com.socialmedia.socialmediabackend.repository.LikeRepository;
import com.socialmedia.socialmediabackend.repository.PostRepository;
import com.socialmedia.socialmediabackend.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final ExternalApiService externalApiService;

    public PostService(PostRepository postRepository,
                       UserService userService,
                       UserRepository userRepository,
                       LikeRepository likeRepository,
                       ExternalApiService externalApiService) {
        this.postRepository = postRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.likeRepository = likeRepository;
        this.externalApiService = externalApiService;
    }

    @Transactional
    @CacheEvict(value = "popularPosts", allEntries = true)
    public PostResponseDTO createPost(CreatePostRequestDTO request, String email) {
        if (request.content() == null || request.content().isBlank()) {
            throw new IllegalArgumentException("Post content is required");
        }

        User user = userService.getVerifiedUserOrThrow(email);

        Post post = new Post();
        post.setContent(request.content());
        post.setUser(user);

        Post savedPost = postRepository.save(post);
        PostResponseDTO response = toResponse(savedPost);

        try {
            externalApiService.sendPostAnalytics(new AnalyticsRequest(
                    response.id(),
                    response.userId(),
                    response.userEmail(),
                    response.createdAt()
            ));
        } catch (RuntimeException ignored) {
        }

        return response;
    }

    public Page<PostResponseDTO> getPosts(Long userId, Pageable pageable) {
        return postRepository.findByOptionalUserId(userId, pageable).map(this::toResponse);
    }

    @Cacheable("popularPosts")
    public Page<PostResponseDTO> getPopularPosts(Pageable pageable) {
        return postRepository.findPopularPosts(pageable).map(this::toResponse);
    }

    @Transactional
    @CacheEvict(value = "popularPosts", allEntries = true)
    public void deletePost(Long postId, String email) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean isOwner = post.getUser().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == UserRole.ROLE_ADMIN;

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("You can delete only your own posts unless you are an admin");
        }

        postRepository.delete(post);
    }

    public Post findPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
    }

    private PostResponseDTO toResponse(Post post) {
        long likeCount = likeRepository.countByPostId(post.getId());
        return new PostResponseDTO(
                post.getId(),
                post.getContent(),
                post.getCreatedAt(),
                post.getUser().getId(),
                post.getUser().getEmail(),
                likeCount
        );
    }
}
