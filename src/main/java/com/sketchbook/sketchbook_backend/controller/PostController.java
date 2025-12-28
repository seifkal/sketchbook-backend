package com.sketchbook.sketchbook_backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sketchbook.sketchbook_backend.dto.CommentDTO;
import com.sketchbook.sketchbook_backend.dto.CommentRequestDTO;
import com.sketchbook.sketchbook_backend.dto.PostDTO;
import com.sketchbook.sketchbook_backend.dto.PostRequestDTO;
import com.sketchbook.sketchbook_backend.entity.Comment;
import com.sketchbook.sketchbook_backend.entity.Post;
import com.sketchbook.sketchbook_backend.entity.User;
import com.sketchbook.sketchbook_backend.service.CommentService;
import com.sketchbook.sketchbook_backend.service.LikeService;
import com.sketchbook.sketchbook_backend.service.PostService;
import com.sketchbook.sketchbook_backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final UserService userService;
    private final LikeService likeService;
    private final CommentService commentService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping
    public ResponseEntity<PostDTO> createPost(@Valid @RequestBody PostRequestDTO postRequest,
            Authentication authentication) {

        UUID userId = UUID.fromString(authentication.getName());
        try {
            String pixelDataJson = objectMapper.writeValueAsString(postRequest.getPixelData());
            Post post = postService.createPostForUser(postRequest.getTitle(), pixelDataJson, userId);

            return ResponseEntity.ok(toDTO(post, userService.getUserById(userId)));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse pixel data");
        }
    }

    @GetMapping
    public ResponseEntity<Page<PostDTO>> getAllPosts(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            Authentication authentication) {

        Pageable pageable = PageRequest.of(page, size);

        User currentUser = userService.getUserById(UUID.fromString(authentication.getName()));

        Page<Post> posts = postService.getAllPosts(pageable);

        return ResponseEntity.ok(posts.map(post -> toDTO(post, currentUser)));

    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable UUID postId, Authentication authentication) {

        Post post = postService.getPostbyId(postId);

        return ResponseEntity.ok(toDTO(post, userService.getUserById(UUID.fromString(authentication.getName()))));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<Page<PostDTO>> getPostsByUsername(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            Authentication authentication) {

        Pageable pageable = PageRequest.of(page, size);

        User currentUser = userService.getUserById(UUID.fromString(authentication.getName()));

        Page<Post> posts = postService.getAllPostsForUsername(username, userService, pageable);

        return ResponseEntity.ok(posts.map(post -> toDTO(post, currentUser)));
    }

    @GetMapping("/id/{userId}")
    public ResponseEntity<Page<PostDTO>> getPostsById(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            Authentication authentication) {

        Pageable pageable = PageRequest.of(page, size);

        User currentUser = userService.getUserById(UUID.fromString(authentication.getName()));

        Page<Post> posts = postService.getAllPostsForUserId(userId, pageable);

        return ResponseEntity.ok(posts.map(post -> toDTO(post, currentUser)));
    }

    @GetMapping("/me")
    public ResponseEntity<Page<PostDTO>> getMyPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            Authentication authentication) {

        Pageable pageable = PageRequest.of(page, size);

        UUID userId = UUID.fromString(authentication.getName());
        User user = userService.getUserById(userId);

        Page<Post> posts = postService.getAllPostsForUserId(userId, pageable);

        return ResponseEntity.ok(posts.map(post -> toDTO(post, user)));
    }

    @GetMapping("likes/{userId}")
    public ResponseEntity<Page<PostDTO>> getLikedPosts(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            Authentication authentication) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Post> posts = postService.getAllPostsLikedByUser(userId, pageable);

        User currentUser = userService.getUserById(UUID.fromString(authentication.getName()));
        return ResponseEntity.ok(posts.map(post -> toDTO(post, currentUser)));
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<Map<String, Object>> toggleLike(
            @PathVariable UUID postId,
            Authentication authentication) {

        UUID userId = UUID.fromString(authentication.getName());
        User user = userService.getUserById(userId);
        Post post = postService.getPostbyId(postId);

        boolean liked = likeService.toggleLike(post, user);

        Map<String, Object> response = new HashMap<>();
        response.put("liked", liked);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{postId}/isLiked")
    public ResponseEntity<Map<String, Boolean>> isLiked(
            @PathVariable UUID postId,
            Authentication authentication) {

        User user = userService.getUserById(UUID.fromString(authentication.getName()));
        Post post = postService.getPostbyId(postId);

        boolean isLiked = likeService.isLiked(post, user);

        return ResponseEntity.ok(Map.of("isLiked", isLiked));
    }

    @PostMapping("/{postId}/comment")
    public ResponseEntity<CommentDTO> commentPost(@PathVariable UUID postId,
            @Valid @RequestBody CommentRequestDTO request, Authentication authentication) {
        Post post = postService.getPostbyId(postId);
        User user = userService.getUserById(UUID.fromString(authentication.getName()));

        Comment comment = commentService.createComment(post, user, request.getContent());

        return ResponseEntity.status(HttpStatus.CREATED).body(toCommentDTO(comment));
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<CommentDTO>> getCommentsForPost(@PathVariable UUID postId) {
        Post post = postService.getPostbyId(postId);

        return ResponseEntity.ok(commentService.getComments(post));
    }

    @GetMapping("following/me")
    public ResponseEntity<Page<PostDTO>> getFollowingPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            Authentication authentication) {
        Pageable pageable = PageRequest.of(page, size);

        UUID userId = UUID.fromString(authentication.getName());
        User user = userService.getUserById(userId);

        Page<Post> posts = postService.getAllPostsByFollowing(userId, pageable);

        return ResponseEntity.ok(posts.map(post -> toDTO(post, user)));
    }

    public PostDTO toDTO(Post post, User currentUser) {
        boolean isLiked = false;

        if (currentUser != null) {
            isLiked = likeService.isLiked(post, currentUser);
        }

        return new PostDTO(
                post.getId(),
                post.getTitle(),
                post.getImageUrl(),
                post.getUser().getId(),
                post.getUser().getUsername(),
                post.getCreatedAt(),
                post.getUser().getAvatarVariant(),
                post.getUser().getAvatarColors(),
                post.getLikeCount(),
                isLiked,
                post.getCommentCount());
    }

    public CommentDTO toCommentDTO(Comment comment) {
        return new CommentDTO(
                comment.getId(),
                comment.getUser().getId(),
                comment.getUser().getUsername(),
                comment.getPost().getId(),
                comment.getUser().getAvatarVariant(),
                comment.getUser().getAvatarColors(),
                comment.getText(),
                comment.getCreatedAt());
    }

}
