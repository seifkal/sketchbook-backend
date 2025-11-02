package com.sketchbook.sketchbook_backend.dto;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

public record CommentDTO(
        UUID id,
        UUID userId,
        String username,
        UUID postId,
        String content,
        Instant createdAt
){}


