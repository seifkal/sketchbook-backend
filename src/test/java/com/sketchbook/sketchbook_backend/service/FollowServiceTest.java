package com.sketchbook.sketchbook_backend.service;

import com.sketchbook.sketchbook_backend.entity.User;
import com.sketchbook.sketchbook_backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FollowServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private FollowService followService;

    @Test
    void followUser_rejectsSelfFollow() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);

        when(userService.getUserById(userId)).thenReturn(user);

        assertThatThrownBy(() -> followService.followUser(userId, userId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("You cannot follow yourself");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void followUser_addsWhenNotFollowing() {
        UUID followerId = UUID.randomUUID();
        UUID followingId = UUID.randomUUID();
        User follower = new User();
        follower.setId(followerId);
        User following = new User();
        following.setId(followingId);

        when(userService.getUserById(followerId)).thenReturn(follower);
        when(userService.getUserById(followingId)).thenReturn(following);

        followService.followUser(followerId, followingId);

        assertThat(follower.getFollowing()).contains(following);
        verify(userRepository).save(follower);
        verify(userRepository).incrementFollowingCount(followerId);
        verify(userRepository).incrementFollowerCount(followingId);
    }

    @Test
    void followUser_noopWhenAlreadyFollowing() {
        UUID followerId = UUID.randomUUID();
        UUID followingId = UUID.randomUUID();
        User follower = new User();
        follower.setId(followerId);
        User following = new User();
        following.setId(followingId);
        follower.getFollowing().add(following);

        when(userService.getUserById(followerId)).thenReturn(follower);
        when(userService.getUserById(followingId)).thenReturn(following);

        followService.followUser(followerId, followingId);

        verify(userRepository, never()).save(any(User.class));
        verify(userRepository, never()).incrementFollowingCount(any());
        verify(userRepository, never()).incrementFollowerCount(any());
    }

    @Test
    void unfollowUser_removesWhenPresent() {
        UUID followerId = UUID.randomUUID();
        UUID followingId = UUID.randomUUID();
        User follower = new User();
        follower.setId(followerId);
        User following = new User();
        following.setId(followingId);
        follower.getFollowing().add(following);

        when(userService.getUserById(followerId)).thenReturn(follower);
        when(userService.getUserById(followingId)).thenReturn(following);

        followService.unfollowUser(followerId, followingId);

        assertThat(follower.getFollowing()).doesNotContain(following);
        verify(userRepository).save(follower);
        verify(userRepository).decrementFollowingCount(followerId);
        verify(userRepository).decrementFollowerCount(followingId);
    }

    @Test
    void unfollowUser_noopWhenNotFollowing() {
        UUID followerId = UUID.randomUUID();
        UUID followingId = UUID.randomUUID();
        User follower = new User();
        follower.setId(followerId);
        User following = new User();
        following.setId(followingId);

        when(userService.getUserById(followerId)).thenReturn(follower);
        when(userService.getUserById(followingId)).thenReturn(following);

        followService.unfollowUser(followerId, followingId);

        verify(userRepository, never()).save(any(User.class));
        verify(userRepository, never()).decrementFollowingCount(any());
        verify(userRepository, never()).decrementFollowerCount(any());
    }

    @Test
    void isFollowing_checksSetMembership() {
        UUID followerId = UUID.randomUUID();
        UUID followingId = UUID.randomUUID();
        User follower = new User();
        follower.setId(followerId);
        User following = new User();
        following.setId(followingId);
        follower.getFollowing().add(following);

        when(userService.getUserById(followerId)).thenReturn(follower);
        when(userService.getUserById(followingId)).thenReturn(following);

        assertThat(followService.isFollowing(followerId, followingId)).isTrue();
    }

    @Test
    void countFollowersAndFollowing_returnsSizes() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        User follower = new User();
        follower.setId(UUID.randomUUID());
        User followingA = new User();
        followingA.setId(UUID.randomUUID());
        User followingB = new User();
        followingB.setId(UUID.randomUUID());
        user.getFollowers().add(follower);
        user.getFollowing().add(followingA);
        user.getFollowing().add(followingB);

        when(userService.getUserById(userId)).thenReturn(user);

        assertThat(followService.countFollowers(userId)).isEqualTo(1);
        assertThat(followService.countFollowing(userId)).isEqualTo(2);
    }
}
