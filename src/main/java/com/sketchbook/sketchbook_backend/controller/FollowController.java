package com.sketchbook.sketchbook_backend.controller;

import com.sketchbook.sketchbook_backend.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/follow")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @PostMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> followUser(@PathVariable UUID userId, Authentication authentication) {
        UUID followerId = UUID.fromString(authentication.getName());
        followService.followUser(followerId, userId);
        return ResponseEntity.ok(Map.of("message", "Followed successfully"));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> unfollowUser(@PathVariable UUID userId, Authentication authentication) {
        UUID followerId = UUID.fromString(authentication.getName());
        followService.unfollowUser(followerId, userId);
        return ResponseEntity.ok(Map.of("message", "Unfollowed successfully"));
    }

    @GetMapping("/{userId}/status")
    public ResponseEntity<Map<String, Object>> isFollowing(@PathVariable UUID userId, Authentication authentication) {
        UUID followerId = UUID.fromString(authentication.getName());
        boolean following = followService.isFollowing(followerId, userId);
        return ResponseEntity.ok(Map.of("following", following));
    }

    @GetMapping("/{userId}/stats")
    public ResponseEntity<Map<String, Integer>> getFollowStats(@PathVariable UUID userId) {
        int followers = followService.countFollowers(userId);
        int following = followService.countFollowing(userId);
        return ResponseEntity.ok(Map.of("followers", followers, "following", following));
    }
}
