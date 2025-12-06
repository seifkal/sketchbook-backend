package com.sketchbook.sketchbook_backend.controller;

import com.sketchbook.sketchbook_backend.dto.UserDTO;
import com.sketchbook.sketchbook_backend.dto.UserRequestDTO;
import com.sketchbook.sketchbook_backend.dto.UserUpdateDTO;
import com.sketchbook.sketchbook_backend.entity.User;
import com.sketchbook.sketchbook_backend.service.FollowService;
import com.sketchbook.sketchbook_backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final FollowService followService;

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> getUserbyId(@PathVariable UUID id, Authentication authentication){
        User user = userService.getUserById(id);
        String loggedInEmail = authentication.getName();

        if(!user.getEmail().equals(loggedInEmail)){
            throw new RuntimeException("You can only view your own profile");
        }
        return ResponseEntity.ok(toDTO(user));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName());
        return ResponseEntity.ok(toDTO(user));
    }

    @PutMapping("/me")
    public ResponseEntity<UserDTO> updateUser(
            Authentication authentication,
            @Valid @RequestBody UserUpdateDTO request){
        User user = userService.updateUser(authentication.getName(), request, passwordEncoder);
        return ResponseEntity.status(HttpStatus.OK).body(toDTO(user));
    }

    private UserDTO toDTO(User user){

        long followerCount = followService.countFollowers(user.getId());

        long followingCount = followService.countFollowing(user.getId());

        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getAvatarVariant(),
                user.getAvatarColors(),
                followerCount,
                followingCount,
                user.getDescription(),
                user.getCreatedAt()
        );
    }


}
