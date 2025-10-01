package com.sketchbook.sketchbook_backend.controller;

import com.sketchbook.sketchbook_backend.dto.PostDTO;
import com.sketchbook.sketchbook_backend.dto.PostRequestDTO;
import com.sketchbook.sketchbook_backend.entity.Post;
import com.sketchbook.sketchbook_backend.service.PostService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<PostDTO> createPost(@Valid @RequestBody PostRequestDTO request) {
        Post post = postService.createPost(
                request.getAuthorId(),
                request.getTitle(),
                request.getPixelData());
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

    @GetMapping("/{userId}")
    public ResponseEntity<List<PostDTO>> getAllPostsForAuthor(@PathVariable UUID userId){
        List<PostDTO> posts = postService.getAllPostsForAuthor(userId)
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
