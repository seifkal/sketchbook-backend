package com.sketchbook.sketchbook_backend.service;

import com.sketchbook.sketchbook_backend.entity.User;
import com.sketchbook.sketchbook_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class FollowService {

    private final UserRepository userRepository;
    private final UserService userService;

    public void followUser(String followerEmail, UUID followingId) {
        User follower = userService.getUserByEmail(followerEmail);
        User following = userService.getUserById(followingId);

        if (follower.equals(following)) {
            throw new RuntimeException("You cannot follow yourself");
        }

        if (!follower.getFollowing().contains(following)) {
            follower.getFollowing().add(following);
            userRepository.save(follower);
        }
    }

    public void unfollowUser(String followerEmail, UUID followingId) {
        User follower = userService.getUserByEmail(followerEmail);
        User following = userService.getUserById(followingId);

        follower.getFollowing().remove(following);
        userRepository.save(follower);
    }

    public boolean isFollowing(String followerEmail, UUID followingId) {
        User follower = userService.getUserByEmail(followerEmail);
        User following = userService.getUserById(followingId);
        return follower.getFollowing().contains(following);
    }

    public int countFollowers(UUID userId) {
        User user = userService.getUserById(userId);
        return user.getFollowers().size();
    }

    public int countFollowing(UUID userId) {
        User user = userService.getUserById(userId);
        return user.getFollowing().size();
    }
}
