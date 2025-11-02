package com.sketchbook.sketchbook_backend.dto;

import java.util.UUID;

public record LikeDTO (
        UUID id,
        UUID userI,
        String username,
        UUID postID
){

}
