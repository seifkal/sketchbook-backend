package com.sketchbook.sketchbook_backend.service;

import com.sketchbook.sketchbook_backend.dto.CommentDTO;
import com.sketchbook.sketchbook_backend.entity.Comment;
import com.sketchbook.sketchbook_backend.entity.Post;
import com.sketchbook.sketchbook_backend.entity.User;
import com.sketchbook.sketchbook_backend.repository.CommentRepository;
import com.sketchbook.sketchbook_backend.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private CommentService commentService;

    @Test
    void createComment_savesAndIncrementsCount() {
        Post post = new Post();
        post.setId(UUID.randomUUID());
        User user = new User();
        user.setId(UUID.randomUUID());

        Comment saved = new Comment();
        saved.setPost(post);
        saved.setUser(user);
        saved.setText("Nice art!");

        when(commentRepository.save(any(Comment.class))).thenReturn(saved);

        Comment result = commentService.createComment(post, user, "Nice art!");

        assertThat(result).isSameAs(saved);
        verify(postRepository).incrementCommentCount(post.getId());
    }

    @Test
    void getComments_mapsToDto() {
        Post post = new Post();
        post.setId(UUID.randomUUID());
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("artist");
        user.setAvatarVariant("variant");
        user.setAvatarColors(List.of("#111111", "#222222"));

        Comment comment = new Comment();
        comment.setId(UUID.randomUUID());
        comment.setPost(post);
        comment.setUser(user);
        comment.setText("Great work");
        comment.setCreatedAt(Instant.parse("2024-01-01T00:00:00Z"));

        when(commentRepository.findAllByPostOrderByCreatedAtDesc(post)).thenReturn(List.of(comment));

        List<CommentDTO> dtos = commentService.getComments(post);

        assertThat(dtos).hasSize(1);
        CommentDTO dto = dtos.get(0);
        assertThat(dto.id()).isEqualTo(comment.getId());
        assertThat(dto.userId()).isEqualTo(user.getId());
        assertThat(dto.username()).isEqualTo("artist");
        assertThat(dto.postId()).isEqualTo(post.getId());
        assertThat(dto.avatarVariant()).isEqualTo("variant");
        assertThat(dto.avatarColors()).containsExactly("#111111", "#222222");
        assertThat(dto.content()).isEqualTo("Great work");
        assertThat(dto.createdAt()).isEqualTo(Instant.parse("2024-01-01T00:00:00Z"));
    }

    @Test
    void countComments_delegatesToRepository() {
        Post post = new Post();
        when(commentRepository.countByPost(post)).thenReturn(5L);

        assertThat(commentService.countComments(post)).isEqualTo(5L);
    }
}
