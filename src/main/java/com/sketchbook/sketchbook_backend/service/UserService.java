package com.sketchbook.sketchbook_backend.service;

import com.sketchbook.sketchbook_backend.entity.User;
import com.sketchbook.sketchbook_backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User registerUser(String username, String email, String password, PasswordEncoder passwordEncoder) {
        if (userRepository.existsByUsername(username))
            throw new RuntimeException("Username already taken");
        if (userRepository.existsByEmail(email))
            throw new RuntimeException("Email already registered");

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional(readOnly = true)
    public User getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public User updateAvatar(UUID id, String loggedInEmail, String newAvatarUrl) {
        User user = getUserById(id);
        if (!user.getEmail().equals(loggedInEmail))
            throw new RuntimeException("You can only update your own profile");

        user.setAvatarUrl(newAvatarUrl);
        return userRepository.save(user);
    }
}
