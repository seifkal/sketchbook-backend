package com.sketchbook.sketchbook_backend.service;

import com.sketchbook.sketchbook_backend.entity.Post;
import com.sketchbook.sketchbook_backend.entity.User;
import com.sketchbook.sketchbook_backend.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PostService {

    private final PostRepository postRepository;

    @Autowired
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Post createPostForUser(String title, String pixelData, String userEmail, UserService userService) {
        User user = userService.getUserByEmail(userEmail);

        Post post = new Post();
        post.setTitle(title);
        post.setPixelData(pixelData);
        post.setUser(user);
        post.setCreatedAt(Instant.now());

        return postRepository.save(post);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Post> getAllPostsForUsername(String username, UserService userService) {
        User user = userService.getUserByUsername(username);
        return postRepository.findAllByUserOrderByCreatedAtDesc(user);
    }

    public Post getPostbyId(UUID postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
    }

}
