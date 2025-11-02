package com.sketchbook.sketchbook_backend.service;

import com.sketchbook.sketchbook_backend.dto.CommentDTO;
import com.sketchbook.sketchbook_backend.entity.Comment;
import com.sketchbook.sketchbook_backend.entity.Post;
import com.sketchbook.sketchbook_backend.entity.User;
import com.sketchbook.sketchbook_backend.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    public Comment createComment(Post post, User user, String text) {
        Comment comment = new Comment();
        comment.setUser(user);
        comment.setPost(post);
        comment.setText(text);
        return commentRepository.save(comment);
    }


    public List<CommentDTO> getComments(Post post) {
        return commentRepository.findAllByPostOrderByCreatedAtDesc(post)
                .stream()
                .map(c -> new CommentDTO(
                        c.getId(),
                        c.getUser().getId(),
                        c.getUser().getUsername(),
                        c.getPost().getId(),
                        c.getText(),
                        c.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    public long countComments(Post post) {
        return commentRepository.countByPost(post);
    }

}
