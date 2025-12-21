package com.sketchbook.sketchbook_backend.service;

import com.sketchbook.sketchbook_backend.dto.CommentDTO;
import com.sketchbook.sketchbook_backend.entity.Comment;
import com.sketchbook.sketchbook_backend.entity.Post;
import com.sketchbook.sketchbook_backend.entity.User;
import com.sketchbook.sketchbook_backend.repository.CommentRepository;
import com.sketchbook.sketchbook_backend.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public Comment createComment(Post post, User user, String text) {
        Comment comment = new Comment();
        comment.setUser(user);
        comment.setPost(post);
        comment.setText(text);
        Comment savedComment = commentRepository.save(comment);

        postRepository.incrementCommentCount(post.getId());

        return savedComment;
    }

    public List<CommentDTO> getComments(Post post) {
        return commentRepository.findAllByPostOrderByCreatedAtDesc(post)
                .stream()
                .map(c -> new CommentDTO(
                        c.getId(),
                        c.getUser().getId(),
                        c.getUser().getUsername(),
                        c.getPost().getId(),
                        c.getUser().getAvatarVariant(),
                        c.getUser().getAvatarColors(),
                        c.getText(),
                        c.getCreatedAt()))
                .collect(Collectors.toList());
    }

    public long countComments(Post post) {
        return commentRepository.countByPost(post);
    }

}
