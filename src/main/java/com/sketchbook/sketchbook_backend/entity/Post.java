package com.sketchbook.sketchbook_backend.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "posts")
@Data
public class Post {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false)
    private String pixelData; // raw pixel data in JSON format


    @Column(length = 255)
    private String imageUrl; // PNG URL for display

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public Post() {
        this.createdAt = Instant.now();
    }
}
