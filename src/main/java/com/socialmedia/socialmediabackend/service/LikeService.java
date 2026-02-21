package com.socialmedia.socialmediabackend.service;

import com.socialmedia.socialmediabackend.Like;
import com.socialmedia.socialmediabackend.Post;
import com.socialmedia.socialmediabackend.User;
import com.socialmedia.socialmediabackend.dto.LikeResponseDTO;
import com.socialmedia.socialmediabackend.repository.LikeRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostService postService;
    private final UserService userService;

    public LikeService(LikeRepository likeRepository, PostService postService, UserService userService) {
        this.likeRepository = likeRepository;
        this.postService = postService;
        this.userService = userService;
    }

    @Transactional
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @CacheEvict(value = "popularPosts", allEntries = true)
    public LikeResponseDTO likePost(Long postId, String email) {
        User user = userService.getVerifiedUserOrThrow(email);
        Post post = postService.findPostById(postId);

        if (likeRepository.existsByPostIdAndUserId(postId, user.getId())) {
            throw new IllegalStateException("You already liked this post");
        }

        Like like = new Like();
        like.setPost(post);
        like.setUser(user);
        likeRepository.save(like);

        long count = likeRepository.countByPostId(postId);
        return new LikeResponseDTO(postId, user.getId(), count, "Post liked successfully");
    }
}
