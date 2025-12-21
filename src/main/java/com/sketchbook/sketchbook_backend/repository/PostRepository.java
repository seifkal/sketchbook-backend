package com.sketchbook.sketchbook_backend.repository;

import com.sketchbook.sketchbook_backend.entity.Post;
import com.sketchbook.sketchbook_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {

    List<Post> findAllByUserOrderByCreatedAtDesc(User author); // Find all posts by author

    List<Post> findAllByOrderByCreatedAtDesc(); // Find all posts for feed, newest first

    List<Post> findAllByUserInOrderByCreatedAtDesc(Set<User> users); // Find all posts by followed users

    @Modifying
    @Query("UPDATE Post p SET p.likeCount = p.likeCount + 1 WHERE p.id = :postId")
    void incrementLikeCount(@Param("postId") UUID postId);

    @Modifying
    @Query("UPDATE Post p SET p.likeCount = p.likeCount - 1 WHERE p.id = :postId")
    void decrementLikeCount(@Param("postId") UUID postId);

    @Modifying
    @Query("UPDATE Post p SET p.commentCount = p.commentCount + 1 WHERE p.id = :postId")
    void incrementCommentCount(@Param("postId") UUID postId);
}
