package com.sketchbook.sketchbook_backend.controller;

import com.sketchbook.sketchbook_backend.dto.UserDTO;
import com.sketchbook.sketchbook_backend.dto.UserRequestDTO;
import com.sketchbook.sketchbook_backend.entity.User;
import com.sketchbook.sketchbook_backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> getUserbyId(@PathVariable UUID id){
        User user = userService.getUserById(id);
        return ResponseEntity.ok(toDTO(user));
    }

    @GetMapping("/username/{username}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username){
        User user = userService.getUserByUsername(username);
        return ResponseEntity.ok(toDTO(user));
    }

    private UserDTO toDTO(User user){
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getAvatarUrl(),
                user.getCreatedAt()
        );
    }


}
