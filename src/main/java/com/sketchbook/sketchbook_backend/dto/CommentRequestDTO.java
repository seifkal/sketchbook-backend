package com.sketchbook.sketchbook_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentRequestDTO {

    @NotBlank(message = "Content cannot be empty")
    @Size(max = 100, message = "Content cannot exceed 100 characters")
    private String content;

}
