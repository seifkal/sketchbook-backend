package com.sketchbook.sketchbook_backend.service;

import com.sketchbook.sketchbook_backend.entity.Post;
import com.sketchbook.sketchbook_backend.entity.User;
import com.sketchbook.sketchbook_backend.repository.PostRepository;
import com.sketchbook.sketchbook_backend.util.ImageGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final ImageGenerator imageGenerator;

    @Autowired
    public PostService(PostRepository postRepository, ImageGenerator imageGenerator) {
        this.postRepository = postRepository;
        this.imageGenerator = imageGenerator;
    }

    public Post createPostForUser(String title, String pixelDataJSON, String userEmail, UserService userService) {
        User user = userService.getUserByEmail(userEmail);

        Post post = new Post();
        post.setTitle(title);
        post.setPixelData(pixelDataJSON);
        post.setUser(user);
        post.setCreatedAt(Instant.now());

        try{
            String url = imageGenerator.generateImage(pixelDataJSON);
            post.setImageUrl(url);
        } catch (IOException e){
            throw new RuntimeException("Failed to generate image");
        }

        return postRepository.save(post);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Post> getAllPostsForUsername(String username, UserService userService) {
        User user = userService.getUserByUsername(username);
        return postRepository.findAllByUserOrderByCreatedAtDesc(user);
    }

    public List<Post> getAllPostsForUserId(UUID userId, UserService userService) {
        User user = userService.getUserById(userId);
        return postRepository.findAllByUserOrderByCreatedAtDesc(user);
    }

    public Post getPostbyId(UUID postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
    }

}
