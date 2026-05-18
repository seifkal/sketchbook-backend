package com.sketchbook.sketchbook_backend.dto;

import com.sketchbook.sketchbook_backend.entity.UserRole;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record UserDTO(
        UUID id,
        String Username,
        UserRole role,
        String avatarVariant,
        List<String> avatarColors,
        Long followersCount,
        Long followingCount,
        boolean isFollowing,
        String description,
        Instant createdAt
){
}
