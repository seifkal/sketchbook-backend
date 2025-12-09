package com.sketchbook.sketchbook_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class PostDTO {
    private UUID id;
    private String title;
    private String imageUrl;
    private UUID authorId;
    private String authorUsername;
    private Instant createdAt;
    private String authorAvatarVariant;
    private List<String> authorAvatarColors;

    private long likeCount;
    private boolean isLiked;
    private Long commentCount;
}
