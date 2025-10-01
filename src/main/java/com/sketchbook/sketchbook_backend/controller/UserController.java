package com.sketchbook.sketchbook_backend.controller;

import com.sketchbook.sketchbook_backend.dto.UserDTO;
import com.sketchbook.sketchbook_backend.dto.UserRequestDTO;
import com.sketchbook.sketchbook_backend.entity.User;
import com.sketchbook.sketchbook_backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserRequestDTO request) {
        User createdUser = userService.createUser(
                request.getUsername(),
                request.getEmail(),
                request.getPassword());
        return ResponseEntity.ok(toDTO(createdUser));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserbyId(@PathVariable UUID id){
        User user = userService.getUserById(id);
        return ResponseEntity.ok(toDTO(user));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username){
        User user = userService.getUserByUsername(username);
        return ResponseEntity.ok(toDTO(user));
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers(){
        List<UserDTO> users = userService.getAllUsers()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
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
