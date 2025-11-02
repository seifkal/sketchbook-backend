package com.sketchbook.sketchbook_backend.repository;

import com.sketchbook.sketchbook_backend.entity.Comment;
import com.sketchbook.sketchbook_backend.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {

    List<Comment> findAllByPostOrderByCreatedAtDesc(Post post);

    long countByPost(Post post);

}
