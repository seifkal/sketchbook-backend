package com.sketchbook.sketchbook_backend.security;

import com.sketchbook.sketchbook_backend.entity.User;
import com.sketchbook.sketchbook_backend.entity.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import static org.assertj.core.api.Assertions.assertThat;

class CustomUserDetailsTest {

    @Test
    void getAuthorities_returnsRoleAuthority() {
        User user = new User();
        user.setRole(UserRole.ADMIN);

        CustomUserDetails userDetails = new CustomUserDetails(user);

        assertThat(userDetails.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_ADMIN");
    }

    @Test
    void getAuthorities_defaultsToUserWhenRoleIsMissing() {
        User user = new User();
        user.setRole(null);

        CustomUserDetails userDetails = new CustomUserDetails(user);

        assertThat(userDetails.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_USER");
    }
}
