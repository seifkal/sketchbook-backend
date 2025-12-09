package com.sketchbook.sketchbook_backend.controller;

import com.sketchbook.sketchbook_backend.dto.AuthDTO;
import com.sketchbook.sketchbook_backend.dto.AuthRequestDTO;
import com.sketchbook.sketchbook_backend.dto.UserRequestDTO;
import com.sketchbook.sketchbook_backend.entity.User;
import com.sketchbook.sketchbook_backend.security.JwtService;
import com.sketchbook.sketchbook_backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<AuthDTO> register(@Valid @RequestBody UserRequestDTO request) {
        User user = userService.registerUser(request.getUsername(), request.getEmail(), request.getPassword(), request.getConfirmPassword(), request.getAvatarVariant(), request.getAvatarColors(),passwordEncoder);
        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(new AuthDTO(token));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthDTO> login(@Valid @RequestBody AuthRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userService.getUserByEmail(request.getEmail());
        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(new AuthDTO(token));
    }

}
