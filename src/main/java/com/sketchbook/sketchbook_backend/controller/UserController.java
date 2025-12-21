package com.sketchbook.sketchbook_backend.controller;

import com.sketchbook.sketchbook_backend.dto.UserDTO;
import com.sketchbook.sketchbook_backend.dto.UserUpdateDTO;
import com.sketchbook.sketchbook_backend.entity.User;
import com.sketchbook.sketchbook_backend.repository.UserRepository;
import com.sketchbook.sketchbook_backend.service.FollowService;
import com.sketchbook.sketchbook_backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
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
    private final UserRepository userRepository;

    @GetMapping
    public PagedModel<UserDTO> getAllUsers(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "3") int size,
            @RequestParam(value = "sort", defaultValue = "recent") String sort,
            Authentication authentication) {

        Pageable pageable = PageRequest.of(page,size);

        Page<User> usersPage;

        if("popular".equals(sort)){
            usersPage = userRepository.findAllByOrderByFollowersCountDesc(pageable);
        } else {
            usersPage = userRepository.findAllByOrderByCreatedAtDesc(pageable);
        }

        User currentUser = userService.getUserByUsername(authentication.getName());

        Page<UserDTO> users = usersPage.map(user -> toDTO(user, currentUser));

        return new PagedModel<>(users);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> getUserbyId(@PathVariable UUID id, Authentication authentication){
        User user = userService.getUserById(id);
        String loggedInEmail = authentication.getName();

        return ResponseEntity.ok(toDTO(user, userService.getUserByEmail(loggedInEmail)));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName());
        return ResponseEntity.ok(toDTO(user, user));
    }

    @PutMapping("/me")
    public ResponseEntity<UserDTO> updateUser(
            Authentication authentication,
            @Valid @RequestBody UserUpdateDTO request){
        User user = userService.updateUser(authentication.getName(), request, passwordEncoder);
        return ResponseEntity.status(HttpStatus.OK).body(toDTO(user, userService.getUserByEmail(authentication.getName())));
    }

    private UserDTO toDTO(User user, User currentUser){

        long followerCount = followService.countFollowers(user.getId());

        long followingCount = followService.countFollowing(user.getId());

        boolean isFollowing = false;

        if(currentUser != null) {
            isFollowing = followService.isFollowing(currentUser, user.getId());
        }

        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getAvatarVariant(),
                user.getAvatarColors(),
                followerCount,
                followingCount,
                isFollowing,
                user.getDescription(),
                user.getCreatedAt()
        );
    }


}
