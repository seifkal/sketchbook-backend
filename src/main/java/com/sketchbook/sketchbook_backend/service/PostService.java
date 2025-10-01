package com.sketchbook.sketchbook_backend.service;

import com.sketchbook.sketchbook_backend.entity.Post;
import com.sketchbook.sketchbook_backend.entity.User;
import com.sketchbook.sketchbook_backend.repository.PostRepository;
import com.sketchbook.sketchbook_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Autowired
    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Post createPost(UUID userId, String title, String pixelData) {

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = new Post();
        post.setAuthor(author);
        post.setTitle(title);
        post.setPixelData(pixelData);

        return postRepository.save(post);

    }

    public List<Post> getAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Post> getAllPostsForAuthor(UUID userId) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return postRepository.findAllByAuthorOrderByCreatedAtDesc(author);
    }
}
