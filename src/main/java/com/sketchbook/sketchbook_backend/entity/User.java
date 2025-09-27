package com.sketchbook.sketchbook_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
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
    private String avatarUrl;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public User() {
        this.createdAt = Instant.now();
    }
}
