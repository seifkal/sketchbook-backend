package com.sketchbook.sketchbook_backend.repository;

import com.sketchbook.sketchbook_backend.entity.Like;
import com.sketchbook.sketchbook_backend.entity.Post;
import com.sketchbook.sketchbook_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LikeRepository extends JpaRepository<Like, UUID> {

    long countByPost(Post post); // Count likes for a post

    Optional<Like> findByPostAndUser(Post post, User user);

    boolean existsByPostIdAndUserId(UUID postId, UUID userId);

    List<Like> findAllByUserOrderByCreatedAtDesc(User user);
}
