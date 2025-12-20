package com.sketchbook.sketchbook_backend.dto;

import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CommentDTO(
        UUID id,
        UUID userId,
        String username,
        UUID postId,
        String avatarVariant,
        List<String> avatarColors,
        String content,
        Instant createdAt
){}


