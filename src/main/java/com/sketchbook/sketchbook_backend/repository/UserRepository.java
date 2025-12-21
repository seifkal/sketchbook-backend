package com.sketchbook.sketchbook_backend.repository;

import com.sketchbook.sketchbook_backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Page<User> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT u FROM User u LEFT JOIN u.followers f GROUP BY u ORDER BY COUNT(f) DESC")
    Page<User> findAllByOrderByFollowersCountDesc(Pageable pageable);

    @Modifying
    @Query("UPDATE User u SET u.followerCount = u.followerCount + 1 WHERE u.id = :userId")
    void incrementFollowerCount(@Param("userId") UUID userId);

    @Modifying
    @Query("UPDATE User u SET u.followerCount = u.followerCount - 1 WHERE u.id = :userId")
    void decrementFollowerCount(@Param("userId") UUID userId);

    @Modifying
    @Query("UPDATE User u SET u.followingCount = u.followingCount + 1 WHERE u.id = :userId")
    void incrementFollowingCount(@Param("userId") UUID userId);

    @Modifying
    @Query("UPDATE User u SET u.followingCount = u.followingCount - 1 WHERE u.id = :userId")
    void decrementFollowingCount(@Param("userId") UUID userId);
}
