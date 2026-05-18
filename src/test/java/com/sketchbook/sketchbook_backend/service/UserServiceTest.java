package com.sketchbook.sketchbook_backend.service;

import com.sketchbook.sketchbook_backend.dto.UserUpdateDTO;
import com.sketchbook.sketchbook_backend.entity.User;
import com.sketchbook.sketchbook_backend.entity.UserRole;
import com.sketchbook.sketchbook_backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void registerUser_rejectsDuplicateUsername() {
        when(userRepository.existsByUsername("alice")).thenReturn(true);

        assertThatThrownBy(() -> userService.registerUser(
                "alice",
                "alice@example.com",
                "password",
                "password",
                "variant",
                List.of("#000000"),
                passwordEncoder))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Username already taken");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_rejectsDuplicateEmail() {
        when(userRepository.existsByUsername("alice")).thenReturn(false);
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.registerUser(
                "alice",
                "alice@example.com",
                "password",
                "password",
                "variant",
                List.of("#000000"),
                passwordEncoder))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Email already registered");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_rejectsMismatchedPasswords() {
        when(userRepository.existsByUsername("alice")).thenReturn(false);
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);

        assertThatThrownBy(() -> userService.registerUser(
                "alice",
                "alice@example.com",
                "password",
                "different",
                "variant",
                List.of("#000000"),
                passwordEncoder))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Passwords do not match");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_savesEncodedPassword() {
        when(userRepository.existsByUsername("alice")).thenReturn(false);
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("hashed-password");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(userCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        User saved = userService.registerUser(
                "alice",
                "alice@example.com",
                "password",
                "password",
                "variant",
                List.of("#000000", "#FFFFFF"),
                passwordEncoder);

        User captured = userCaptor.getValue();
        assertThat(saved).isSameAs(captured);
        assertThat(captured.getUsername()).isEqualTo("alice");
        assertThat(captured.getEmail()).isEqualTo("alice@example.com");
        assertThat(captured.getAvatarVariant()).isEqualTo("variant");
        assertThat(captured.getAvatarColors()).containsExactly("#000000", "#FFFFFF");
        assertThat(captured.getRole()).isEqualTo(UserRole.USER);
        assertThat(captured.getPasswordHash()).isEqualTo("hashed-password");
    }

    @Test
    void updateUser_updatesUsernameAndEmail() {
        UUID userId = UUID.randomUUID();
        User user = baseUser(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("newname")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);

        UserUpdateDTO request = new UserUpdateDTO();
        request.setUsername("newname");
        request.setEmail("new@example.com");

        User updated = userService.updateUser(userId, request, passwordEncoder);

        assertThat(updated.getUsername()).isEqualTo("newname");
        assertThat(updated.getEmail()).isEqualTo("new@example.com");
    }

    @Test
    void updateUser_requiresOldPasswordForChange() {
        UUID userId = UUID.randomUUID();
        User user = baseUser(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserUpdateDTO request = new UserUpdateDTO();
        request.setNewPassword("newpass");

        assertThatThrownBy(() -> userService.updateUser(userId, request, passwordEncoder))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Old password is required to change password");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_rejectsIncorrectOldPassword() {
        UUID userId = UUID.randomUUID();
        User user = baseUser(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old", user.getPasswordHash())).thenReturn(false);

        UserUpdateDTO request = new UserUpdateDTO();
        request.setNewPassword("newpass");
        request.setOldPassword("old");

        assertThatThrownBy(() -> userService.updateUser(userId, request, passwordEncoder))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Old password is incorrect");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_rejectsSamePassword() {
        UUID userId = UUID.randomUUID();
        User user = baseUser(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old", user.getPasswordHash())).thenReturn(true);
        when(passwordEncoder.matches("newpass", user.getPasswordHash())).thenReturn(true);

        UserUpdateDTO request = new UserUpdateDTO();
        request.setNewPassword("newpass");
        request.setOldPassword("old");

        assertThatThrownBy(() -> userService.updateUser(userId, request, passwordEncoder))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("New password cannot be the same as old password");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_changesPasswordWhenValid() {
        UUID userId = UUID.randomUUID();
        User user = baseUser(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old", user.getPasswordHash())).thenReturn(true);
        when(passwordEncoder.matches("newpass", user.getPasswordHash())).thenReturn(false);
        when(passwordEncoder.encode("newpass")).thenReturn("new-hash");
        when(userRepository.save(user)).thenReturn(user);

        UserUpdateDTO request = new UserUpdateDTO();
        request.setNewPassword("newpass");
        request.setOldPassword("old");

        User updated = userService.updateUser(userId, request, passwordEncoder);

        assertThat(updated.getPasswordHash()).isEqualTo("new-hash");
    }

    private User baseUser(UUID id) {
        User user = new User();
        user.setId(id);
        user.setUsername("oldname");
        user.setEmail("old@example.com");
        user.setPasswordHash("old-hash");
        return user;
    }
}
