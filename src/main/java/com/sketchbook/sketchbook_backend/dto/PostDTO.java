package com.sketchbook.sketchbook_backend.dto;

import java.time.Instant;
import java.util.UUID;

public record PostDTO(
        UUID id,
        String title,
        String imageUrl,
        UUID authorId,
        String authorUsername,
        Instant createdAt
) {
}
