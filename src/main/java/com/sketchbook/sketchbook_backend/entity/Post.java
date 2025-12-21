package com.sketchbook.sketchbook_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.sql.SQLType;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
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
    private User user;

    @Column(nullable = false, length = 100)
    private String title;

    @Lob
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(nullable = false)
    private String pixelData; // raw pixel data in JSON format

    @Column(length = 255)
    private String imageUrl; // PNG URL for display

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    @ColumnDefault("0")
    private long likeCount = 0;

    @Column(nullable = false)
    @ColumnDefault("0")
    private long commentCount = 0;

    public Post() {
        this.createdAt = Instant.now();
    }

}
