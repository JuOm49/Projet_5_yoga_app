package com.openclassrooms.starterjwt.security.services;

import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

public class UserDetailsImplTest {

    private UserDetailsImpl userDetails;
    private final Long testId = 1L;
    private final String testUsername = "test@example.com";
    private final String testFirstName = "John";
    private final String testLastName = "Doe";
    private final Boolean testAdmin = false;
    private final String testPassword = "password123";

    @BeforeEach
    void setUp() {
        userDetails = UserDetailsImpl.builder()
            .id(testId)
            .username(testUsername)
            .firstName(testFirstName)
            .lastName(testLastName)
            .admin(testAdmin)
            .password(testPassword)
            .build();
    }

    @Test
    void constructor_shouldCreateUserDetailsWithAllFields() {
        // ASSERT
        assertThat(userDetails.getId()).isEqualTo(testId);
        assertThat(userDetails.getUsername()).isEqualTo(testUsername);
        assertThat(userDetails.getFirstName()).isEqualTo(testFirstName);
        assertThat(userDetails.getLastName()).isEqualTo(testLastName);
        assertThat(userDetails.getAdmin()).isEqualTo(testAdmin);
        assertThat(userDetails.getPassword()).isEqualTo(testPassword);
    }

    @Test
    void getAuthorities_shouldReturnEmptyCollection() {
        // ACT
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        // ASSERT
        assertThat(authorities).isNotNull();
        assertThat(authorities).isEmpty();
    }

    @Test
    void isAccountNonExpired_shouldReturnTrue() {
        // ACT & ASSERT
        assertThat(userDetails.isAccountNonExpired()).isTrue();
    }

    @Test
    void isAccountNonLocked_shouldReturnTrue() {
        // ACT & ASSERT
        assertThat(userDetails.isAccountNonLocked()).isTrue();
    }

    @Test
    void isCredentialsNonExpired_shouldReturnTrue() {
        // ACT & ASSERT
        assertThat(userDetails.isCredentialsNonExpired()).isTrue();
    }

    @Test
    void isEnabled_shouldReturnTrue() {
        // ACT & ASSERT
        assertThat(userDetails.isEnabled()).isTrue();
    }

    @Test
    void equals_shouldReturnTrue_whenSameObject() {
        // ACT & ASSERT
        assertThat(userDetails.equals(userDetails)).isTrue();
    }

    @Test
    void equals_shouldReturnTrue_whenSameId() {
        // ARRANGE
        UserDetailsImpl otherUser = UserDetailsImpl.builder()
            .id(testId) // Même ID
            .username("different@example.com")
            .firstName("Jane")
            .lastName("Smith")
            .admin(true)
            .password("differentPassword")
            .build();

        // ACT & ASSERT
        assertThat(userDetails.equals(otherUser)).isTrue();
    }

    @Test
    void equals_shouldReturnFalse_whenDifferentId() {
        // ARRANGE
        UserDetailsImpl otherUser = UserDetailsImpl.builder()
            .id(2L)
            .username(testUsername)
            .firstName(testFirstName)
            .lastName(testLastName)
            .admin(testAdmin)
            .password(testPassword)
            .build();

        // ACT & ASSERT
        assertThat(userDetails.equals(otherUser)).isFalse();
    }

    @Test
    void equals_shouldReturnFalse_whenNull() {
        // ACT & ASSERT
        assertThat(userDetails.equals(null)).isFalse();
    }

    @Test
    void equals_shouldReturnFalse_whenDifferentClass() {
        // ARRANGE
        String differentObject = "Not a UserDetailsImpl";

        // ACT & ASSERT
        assertThat(userDetails.equals(differentObject)).isFalse();
    }

    @Test
    void equals_shouldReturnFalse_whenIdIsNull() {
        // ARRANGE
        UserDetailsImpl userWithNullId = UserDetailsImpl.builder()
            .id(null)
            .username(testUsername)
            .firstName(testFirstName)
            .lastName(testLastName)
            .admin(testAdmin)
            .password(testPassword)
            .build();

        // ACT & ASSERT
        assertThat(userDetails.equals(userWithNullId)).isFalse();
    }

    @Test
    void equals_shouldReturnTrue_whenBothIdsAreNull() {
        // ARRANGE
        UserDetailsImpl user1 = UserDetailsImpl.builder()
            .id(null)
            .username("user1@example.com")
            .build();

        UserDetailsImpl user2 = UserDetailsImpl.builder()
            .id(null)
            .username("user2@example.com")
            .build();

        // ACT & ASSERT
        assertThat(user1.equals(user2)).isTrue();
    }

    @Test
    void builder_shouldCreateUserDetailsWithAdminTrue() {
        // ARRANGE & ACT
        UserDetailsImpl adminUser = UserDetailsImpl.builder()
            .id(2L)
            .username("admin@example.com")
            .firstName("Admin")
            .lastName("User")
            .admin(true)
            .password("adminPassword")
            .build();

        // ASSERT
        assertThat(adminUser.getAdmin()).isTrue();
        assertThat(adminUser.getId()).isEqualTo(2L);
        assertThat(adminUser.getUsername()).isEqualTo("admin@example.com");
    }

    @Test
    void builder_shouldCreateUserDetailsWithPartialData() {
        // ARRANGE & ACT
        UserDetailsImpl partialUser = UserDetailsImpl.builder()
            .id(3L)
            .username("partial@example.com")
            .build();

        // ASSERT
        assertThat(partialUser.getId()).isEqualTo(3L);
        assertThat(partialUser.getUsername()).isEqualTo("partial@example.com");
        assertThat(partialUser.getFirstName()).isNull();
        assertThat(partialUser.getLastName()).isNull();
        assertThat(partialUser.getAdmin()).isNull();
        assertThat(partialUser.getPassword()).isNull();
    }
}
