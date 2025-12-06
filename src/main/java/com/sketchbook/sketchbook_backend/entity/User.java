package com.sketchbook.sketchbook_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;


@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(length = 255)
    private String avatarVariant;

    @ElementCollection
    private List<String> avatarColors;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(length = 255)
    private String description;

    public User() {
        this.createdAt = Instant.now();
    }

    // Users this user follows
    @ManyToMany
    @JoinTable(
            name = "user_follows",
            joinColumns = @JoinColumn(name = "follower_id"),
            inverseJoinColumns = @JoinColumn(name = "following_id")
    )
    private Set<User> following = new HashSet<>();

    // Users who follow this user
    @ManyToMany(mappedBy = "following")
    private Set<User> followers = new HashSet<>();
}
