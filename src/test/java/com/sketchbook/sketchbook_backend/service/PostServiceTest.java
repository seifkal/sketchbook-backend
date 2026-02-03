package com.sketchbook.sketchbook_backend.service;

import com.sketchbook.sketchbook_backend.entity.Like;
import com.sketchbook.sketchbook_backend.entity.Post;
import com.sketchbook.sketchbook_backend.entity.User;
import com.sketchbook.sketchbook_backend.repository.LikeRepository;
import com.sketchbook.sketchbook_backend.repository.PostRepository;
import com.sketchbook.sketchbook_backend.util.ImageGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private ImageGenerator imageGenerator;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private PostService postService;

    @Test
    void createPostForUser_setsImageUrlAndSaves() throws IOException {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setUsername("artist");

        when(userService.getUserById(userId)).thenReturn(user);
        when(imageGenerator.generateImage("pixels")).thenReturn("https://cdn.example.com/image.png");

        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
        when(postRepository.save(postCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        Post saved = postService.createPostForUser("My Art", "pixels", userId);

        Post captured = postCaptor.getValue();
        assertThat(saved).isSameAs(captured);
        assertThat(captured.getTitle()).isEqualTo("My Art");
        assertThat(captured.getPixelData()).isEqualTo("pixels");
        assertThat(captured.getUser()).isSameAs(user);
        assertThat(captured.getImageUrl()).isEqualTo("https://cdn.example.com/image.png");
        assertThat(captured.getCreatedAt()).isNotNull();
    }

    @Test
    void createPostForUser_throwsWhenImageFails() throws IOException {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);

        when(userService.getUserById(userId)).thenReturn(user);
        when(imageGenerator.generateImage("pixels")).thenThrow(new IOException("boom"));

        assertThatThrownBy(() -> postService.createPostForUser("My Art", "pixels", userId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Failed to generate image");

        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void deletePost_allowsOwner() {
        UUID userId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        User owner = new User();
        owner.setId(userId);
        Post post = new Post();
        post.setId(postId);
        post.setUser(owner);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        postService.deletePost(postId, userId);

        verify(postRepository).delete(post);
    }

    @Test
    void deletePost_rejectsNonOwner() {
        UUID userId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        User owner = new User();
        owner.setId(UUID.randomUUID());
        Post post = new Post();
        post.setId(postId);
        post.setUser(owner);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        assertThatThrownBy(() -> postService.deletePost(postId, userId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("You do not have permission to delete this post");

        verify(postRepository, never()).delete(any(Post.class));
    }

    @Test
    void getAllPostsByFollowing_returnsEmptyWhenNone() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);

        when(userService.getUserById(userId)).thenReturn(user);

        Page<Post> result = postService.getAllPostsByFollowing(userId, PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(0);
        verify(postRepository, never()).findAllByUserInOrderByCreatedAtDesc(any(), any());
    }

    @Test
    void getAllPostsByFollowing_queriesWhenFollowingExists() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        User followed = new User();
        followed.setId(UUID.randomUUID());
        user.getFollowing().add(followed);

        when(userService.getUserById(userId)).thenReturn(user);
        Page<Post> page = new PageImpl<>(List.of(new Post()));
        when(postRepository.findAllByUserInOrderByCreatedAtDesc(any(), any())).thenReturn(page);

        Page<Post> result = postService.getAllPostsByFollowing(userId, PageRequest.of(0, 10));

        assertThat(result).isSameAs(page);
        verify(postRepository).findAllByUserInOrderByCreatedAtDesc(PageRequest.of(0, 10), Set.of(followed));
    }

    @Test
    void getAllPostsLikedByUser_mapsLikesToPosts() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        Post post = new Post();
        post.setId(UUID.randomUUID());
        Like like = new Like();
        like.setPost(post);

        when(userService.getUserById(userId)).thenReturn(user);
        when(likeRepository.findAllByUserOrderByCreatedAtDesc(any(), any()))
                .thenReturn(new PageImpl<>(List.of(like)));

        Page<Post> result = postService.getAllPostsLikedByUser(userId, PageRequest.of(0, 5));

        assertThat(result.getContent()).containsExactly(post);
    }
}
