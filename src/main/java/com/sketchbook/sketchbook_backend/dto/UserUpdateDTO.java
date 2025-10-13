package com.sketchbook.sketchbook_backend.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateDTO {

    @Size(min = 3, max = 20, message = "Username must be between 3 and 50 characters")
    private String username;

    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String email;

    @Size(min = 8,max = 50, message = "Password must be at least 8 characters long")
    private String newPassword;

    private String oldPassword;

    @Size(max = 255, message = "Avatar URL is too long")
    private String avatarUrl;
}
