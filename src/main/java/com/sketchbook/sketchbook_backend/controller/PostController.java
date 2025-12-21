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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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

        String userEmail = authentication.getName();
        try {
            String pixelDataJson = objectMapper.writeValueAsString(postRequest.getPixelData());
            Post post = postService.createPostForUser(postRequest.getTitle(), pixelDataJson, userEmail, userService);

            return ResponseEntity.ok(toDTO(post, userService.getUserByEmail(userEmail)));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse pixel data");
        }
    }

    @GetMapping
    public ResponseEntity<List<PostDTO>> getAllPosts(Authentication authentication) {
        User currentUser = userService.getUserByEmail(authentication.getName());

        List<PostDTO> posts = postService.getAllPosts()
                .stream()
                .map(post -> toDTO(post, currentUser))
                .collect(Collectors.toList());
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable UUID postId, Authentication authentication) {
        Post post = postService.getPostbyId(postId);
        return ResponseEntity.ok(toDTO(post, userService.getUserByEmail(authentication.getName())));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<List<PostDTO>> getPostsByUsername(
            @PathVariable String username,
            Authentication authentication) {

        User currentUser = userService.getUserByEmail(authentication.getName());

        List<PostDTO> posts = postService.getAllPostsForUsername(username, userService)
                .stream()
                .map(post -> toDTO(post, currentUser))
                .collect(Collectors.toList());
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/id/{userId}")
    public ResponseEntity<List<PostDTO>> getPostsById(
            @PathVariable UUID userId,
            Authentication authentication) {

        User currentUser = userService.getUserByEmail(authentication.getName());
        List<PostDTO> posts = postService.getAllPostsForUserId(userId, userService)
                .stream()
                .map(post -> toDTO(post, currentUser))
                .collect(Collectors.toList());
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/me")
    public ResponseEntity<List<PostDTO>> getMyPosts(Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName());
        UUID userId = user.getId();
        return getPostsById(userId, authentication);
    }

    @GetMapping("/likes/{userId}")
    public ResponseEntity<List<PostDTO>> getLikedPosts(
            @PathVariable UUID userId,
            Authentication authentication) {
        User currentUser = userService.getUserByEmail(authentication.getName());
        List<PostDTO> posts = postService.getAllPostsLikedByUser(userId, userService)
                .stream()
                .map(post -> toDTO(post, currentUser))
                .collect(Collectors.toList());
        return ResponseEntity.ok(posts);
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<Map<String, Object>> toggleLike(
            @PathVariable UUID postId,
            Authentication authentication) {

        String userEmail = authentication.getName();
        User user = userService.getUserByEmail(userEmail);
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

        User user = userService.getUserByEmail(authentication.getName());
        Post post = postService.getPostbyId(postId);

        boolean isLiked = likeService.isLiked(post, user);

        return ResponseEntity.ok(Map.of("isLiked", isLiked));
    }

    @PostMapping("/{postId}/comment")
    public ResponseEntity<CommentDTO> commentPost(@PathVariable UUID postId,
            @Valid @RequestBody CommentRequestDTO request, Authentication authentication) {
        Post post = postService.getPostbyId(postId);
        User user = userService.getUserByEmail(authentication.getName());

        Comment comment = commentService.createComment(post, user, request.getContent());

        return ResponseEntity.status(HttpStatus.CREATED).body(toCommentDTO(comment));
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<CommentDTO>> getCommentsForPost(@PathVariable UUID postId) {
        Post post = postService.getPostbyId(postId);

        return ResponseEntity.ok(commentService.getComments(post));
    }

    @GetMapping("/following/me")
    public ResponseEntity<List<PostDTO>> getFollowingPosts(Authentication authentication) {
        UUID userId = userService.getUserByEmail(authentication.getName()).getId();

        List<PostDTO> posts = postService.getAllPostsByFollowing(userId)
                .stream()
                .map(post -> toDTO(post, userService.getUserByEmail(authentication.getName())))
                .collect(Collectors.toList());
        return ResponseEntity.ok(posts);
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
