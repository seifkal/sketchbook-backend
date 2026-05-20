package com.sketchbook.sketchbook_backend.security;

import com.sketchbook.sketchbook_backend.entity.User;
import com.sketchbook.sketchbook_backend.entity.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import static org.assertj.core.api.Assertions.assertThat;

class CustomUserDetailsTest {

    @Test
    void getAuthorities_returnsUserRoleAuthority() {
        User user = new User();
        user.setRole(UserRole.USER);

        CustomUserDetails userDetails = new CustomUserDetails(user);

        assertThat(userDetails.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_USER");
    }

    @Test
    void getAuthorities_returnsGuestRoleAuthority() {
        User user = new User();
        user.setRole(UserRole.GUEST);

        CustomUserDetails userDetails = new CustomUserDetails(user);

        assertThat(userDetails.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_GUEST");
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

    @Test
    void getAuthorities_doesNotReturnAdminAuthorityYet() {
        User user = new User();
        user.setRole(UserRole.ADMIN);

        CustomUserDetails userDetails = new CustomUserDetails(user);

        assertThat(userDetails.getAuthorities()).isEmpty();
    }
}
