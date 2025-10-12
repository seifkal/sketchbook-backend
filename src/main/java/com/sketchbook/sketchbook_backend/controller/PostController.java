package com.sketchbook.sketchbook_backend.controller;

import com.sketchbook.sketchbook_backend.dto.PostDTO;
import com.sketchbook.sketchbook_backend.dto.PostRequestDTO;
import com.sketchbook.sketchbook_backend.entity.Post;
import com.sketchbook.sketchbook_backend.service.PostService;
import com.sketchbook.sketchbook_backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<PostDTO> createPost(@Valid @RequestBody PostRequestDTO postRequest,
                                              Authentication authentication) {
        String userEmail = authentication.getName();
        Post post = postService.createPostForUser(postRequest.getTitle(), postRequest.getPixelData(), userEmail, userService);
        return ResponseEntity.ok(toDTO(post));
    }

    @GetMapping //Get all posts for feed , newest first
    public ResponseEntity<List<PostDTO>> getAllPosts(){
        List<PostDTO> posts = postService.getAllPosts()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{username}")
    public ResponseEntity<List<PostDTO>> getPostsByUsername(@PathVariable String username) {
        List<PostDTO> posts = postService.getAllPostsForUsername(username, userService)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(posts);
    }

    public PostDTO toDTO(Post post){
        return new PostDTO(
                post.getId(),
                post.getTitle(),
                post.getImageUrl(),
                post.getAuthor().getId(),
                post.getAuthor().getUsername(),
                post.getCreatedAt()
        );
    }
}
