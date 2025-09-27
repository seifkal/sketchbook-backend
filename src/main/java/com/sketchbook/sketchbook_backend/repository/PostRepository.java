package com.sketchbook.sketchbook_backend.repository;

import com.sketchbook.sketchbook_backend.entity.Post;
import com.sketchbook.sketchbook_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {

    List<Post> findAllByAuthorOrderByCreatedAtDesc(User author); // Find all posts by author

    List<Post> findAllByOrderByCreatedAtDesc(); // Find all posts for feed, newest first
}
