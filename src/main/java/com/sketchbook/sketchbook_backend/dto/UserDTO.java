package com.sketchbook.sketchbook_backend.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record UserDTO(
        UUID id,
        String Username,
        String avatarVariant,
        List<String> avatarColors,
        Long followersCount,
        Long followingCount,
        boolean isFollowing,
        String description,
        Instant createdAt
){
}
