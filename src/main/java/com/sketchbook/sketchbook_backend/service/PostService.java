package com.sketchbook.sketchbook_backend.service;

import com.sketchbook.sketchbook_backend.entity.Like;
import com.sketchbook.sketchbook_backend.entity.Post;
import com.sketchbook.sketchbook_backend.entity.User;
import com.sketchbook.sketchbook_backend.repository.LikeRepository;
import com.sketchbook.sketchbook_backend.repository.PostRepository;
import com.sketchbook.sketchbook_backend.util.ImageGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final ImageGenerator imageGenerator;
    private final LikeRepository likeRepository;
    private final UserService userService;

    @Autowired
    public PostService(PostRepository postRepository, ImageGenerator imageGenerator, LikeRepository likeRepository,
            UserService userService) {
        this.postRepository = postRepository;
        this.imageGenerator = imageGenerator;
        this.likeRepository = likeRepository;
        this.userService = userService;
    }

    public Post createPostForUser(String title, String pixelDataJSON, UUID userId) {
        User user = userService.getUserById(userId);

        Post post = new Post();
        post.setTitle(title);
        post.setPixelData(pixelDataJSON);
        post.setUser(user);
        post.setCreatedAt(Instant.now());

        try {
            String url = imageGenerator.generateImage(pixelDataJSON);
            post.setImageUrl(url);
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate image");
        }

        return postRepository.save(post);
    }

    public Page<Post> getAllPosts(Pageable pageable) {
        return postRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    public Page<Post> getAllPostsForUsername(String username, UserService userService, Pageable pageable) {
        User user = userService.getUserByUsername(username);
        return postRepository.findAllByUserOrderByCreatedAtDesc(pageable, user);
    }

    public Page<Post> getAllPostsForUserId(UUID userId, Pageable pageable) {
        User user = userService.getUserById(userId);
        return postRepository.findAllByUserOrderByCreatedAtDesc(pageable, user);
    }

    public Post getPostbyId(UUID postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
    }

    public Page<Post> getAllPostsLikedByUser(UUID userId, Pageable pageable) {
        User user = userService.getUserById(userId);
        return likeRepository.findAllByUserOrderByCreatedAtDesc(pageable, user)
                .map(Like::getPost);

    }

    public Page<Post> getAllPostsByFollowing(UUID userId, Pageable pageable) {
        User user = userService.getUserById(userId);

        Set<User> following = user.getFollowing();

        if (following.isEmpty()) {
            return Page.empty();
        }

        return postRepository.findAllByUserInOrderByCreatedAtDesc(pageable, following);
    }

    @Transactional
    public void deletePost(UUID postId, UUID userId) {
        Post post = getPostbyId(postId);

        if (post.getUser().getId().equals(userId)) {
            postRepository.delete(post);
        }
        else {
            throw new RuntimeException("You do not have permission to delete this post");
        }
    }
}
