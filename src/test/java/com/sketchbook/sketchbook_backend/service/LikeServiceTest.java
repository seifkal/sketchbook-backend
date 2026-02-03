package com.sketchbook.sketchbook_backend.service;

import com.sketchbook.sketchbook_backend.entity.Like;
import com.sketchbook.sketchbook_backend.entity.Post;
import com.sketchbook.sketchbook_backend.entity.User;
import com.sketchbook.sketchbook_backend.repository.LikeRepository;
import com.sketchbook.sketchbook_backend.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private LikeService likeService;

    @Test
    void toggleLike_removesExistingLike() {
        Post post = new Post();
        post.setId(UUID.randomUUID());
        User user = new User();
        user.setId(UUID.randomUUID());
        Like like = new Like();
        like.setPost(post);
        like.setUser(user);

        when(likeRepository.findByPostAndUser(post, user)).thenReturn(Optional.of(like));

        boolean liked = likeService.toggleLike(post, user);

        assertThat(liked).isFalse();
        verify(likeRepository).delete(like);
        verify(postRepository).decrementLikeCount(post.getId());
    }

    @Test
    void toggleLike_addsLikeWhenMissing() {
        Post post = new Post();
        post.setId(UUID.randomUUID());
        User user = new User();
        user.setId(UUID.randomUUID());

        when(likeRepository.findByPostAndUser(post, user)).thenReturn(Optional.empty());

        boolean liked = likeService.toggleLike(post, user);

        assertThat(liked).isTrue();
        verify(likeRepository).save(any(Like.class));
        verify(postRepository).incrementLikeCount(post.getId());
    }

    @Test
    void countLikes_delegatesToRepository() {
        Post post = new Post();
        when(likeRepository.countByPost(post)).thenReturn(12L);

        assertThat(likeService.countLikes(post)).isEqualTo(12L);
    }

    @Test
    void isLiked_checksRepository() {
        Post post = new Post();
        User user = new User();
        when(likeRepository.findByPostAndUser(post, user)).thenReturn(Optional.of(new Like()));

        assertThat(likeService.isLiked(post, user)).isTrue();
    }
}
