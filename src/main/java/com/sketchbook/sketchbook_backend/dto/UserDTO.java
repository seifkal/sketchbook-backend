package com.sketchbook.sketchbook_backend.dto;

import java.time.Instant;
import java.util.UUID;

public record UserDTO(
        UUID id,
        String Username,
        String email,
        String avatarUrl,
        Instant createdAt
){
}
