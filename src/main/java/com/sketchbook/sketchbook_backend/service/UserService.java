package com.sketchbook.sketchbook_backend.service;

import com.sketchbook.sketchbook_backend.dto.UserUpdateDTO;
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
    public User registerUser(String username, String email, String password, String confirmPassword ,PasswordEncoder passwordEncoder) {
        if (userRepository.existsByUsername(username))
            throw new RuntimeException("Username already taken");
        if (userRepository.existsByEmail(email))
            throw new RuntimeException("Email already registered");
        if(!password.equals(confirmPassword))
            throw new RuntimeException("Passwords do not match");
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
    public User updateUser(String loggedInEmail,
                           UserUpdateDTO request,
                           PasswordEncoder passwordEncoder) {

        User user = getUserByEmail(loggedInEmail);

        if (!user.getEmail().equals(loggedInEmail)) {
            throw new RuntimeException("You can only update your own profile");
        }

        if (request.getUsername() != null && !request.getUsername().isBlank()
                && !request.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new RuntimeException("Username already taken");
            }
            user.setUsername(request.getUsername());
        }

        if (request.getEmail() != null && !request.getEmail().isBlank()
                && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email already registered");
            }
            user.setEmail(request.getEmail());
        }

        String newPassword = request.getNewPassword();
        String oldPassword = request.getOldPassword();

        if (newPassword != null && !newPassword.isBlank()) {

            if (oldPassword == null || oldPassword.isBlank()) {
                throw new RuntimeException("Old password is required to change password");
            }

            if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
                throw new RuntimeException("Old password is incorrect");
            }

            if (passwordEncoder.matches(newPassword, user.getPasswordHash())) {
                throw new RuntimeException("New password cannot be the same as old password");
            }

            user.setPasswordHash(passwordEncoder.encode(newPassword));
        }

        return userRepository.save(user);
    }

}
