package com.sketchbook.sketchbook_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class UserRequestDTO {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8,max = 50, message = "Password must be at least 8 characters long")
    private String password;

    @NotBlank(message = "Password confirmation is required")
    @Size(min = 8,max = 50, message = "Password must be at least 8 characters long")
    private String confirmPassword;

    @NotBlank(message = "Avatar variant is required")
    private String avatarVariant;

    @NotEmpty(message = "Avatar colors are required")
    private List<String> avatarColors;
}
