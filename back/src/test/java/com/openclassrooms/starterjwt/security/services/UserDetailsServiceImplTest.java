package com.openclassrooms.starterjwt.security.services;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceImplTest {

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private UserRepository userRepository;

    private User testUser;
    private final String testEmail = "test@example.com";
    private final String testPassword = "password123";
    private final String testFirstName = "John";
    private final String testLastName = "Doe";
    private final Long testId = 1L;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(testId);
        testUser.setEmail(testEmail);
        testUser.setPassword(testPassword);
        testUser.setFirstName(testFirstName);
        testUser.setLastName(testLastName);
        testUser.setAdmin(false);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void loadUserByUsername_shouldReturnUserDetails_whenUserExists() {
        // ARRANGE
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));

        // ACT
        UserDetails userDetails = userDetailsService.loadUserByUsername(testEmail);

        // ASSERT
        assertThat(userDetails).isNotNull();
        assertThat(userDetails).isInstanceOf(UserDetailsImpl.class);

        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) userDetails;
        assertThat(userDetailsImpl.getId()).isEqualTo(testId);
        assertThat(userDetailsImpl.getUsername()).isEqualTo(testEmail);
        assertThat(userDetailsImpl.getPassword()).isEqualTo(testPassword);
        assertThat(userDetailsImpl.getFirstName()).isEqualTo(testFirstName);
        assertThat(userDetailsImpl.getLastName()).isEqualTo(testLastName);

        verify(userRepository).findByEmail(testEmail);
    }

    @Test
    void loadUserByUsername_shouldThrowUsernameNotFoundException_whenUserDoesNotExist() {
        // ARRANGE
        String nonExistentEmail = "nonexistent@example.com";
        when(userRepository.findByEmail(nonExistentEmail)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(nonExistentEmail))
            .isInstanceOf(UsernameNotFoundException.class)
            .hasMessageContaining("User Not Found with email: " + nonExistentEmail);

        verify(userRepository).findByEmail(nonExistentEmail);
    }

    @Test
    void loadUserByUsername_shouldReturnUserDetails_whenUserIsAdmin() {
        // ARRANGE
        testUser.setAdmin(true);
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));

        // ACT
        UserDetails userDetails = userDetailsService.loadUserByUsername(testEmail);

        // ASSERT
        assertThat(userDetails).isNotNull();
        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) userDetails;

        assertThat(userDetailsImpl.getAdmin()).isNull();
        assertThat(userDetailsImpl.getUsername()).isEqualTo(testEmail);
        assertThat(userDetailsImpl.getId()).isEqualTo(testId);

        verify(userRepository).findByEmail(testEmail);
    }

    @Test
    void loadUserByUsername_shouldHandleEmptyFields_whenUserHasEmptyData() {
        // ARRANGE
        User userWithEmptyFields = new User();
        userWithEmptyFields.setId(2L);
        userWithEmptyFields.setEmail("empty@hotmail.com");
        userWithEmptyFields.setPassword("password");
        userWithEmptyFields.setFirstName("");
        userWithEmptyFields.setLastName("");
        userWithEmptyFields.setAdmin(false);

        when(userRepository.findByEmail("empty@hotmail.com")).thenReturn(Optional.of(userWithEmptyFields));

        // ACT
        UserDetails userDetails = userDetailsService.loadUserByUsername("empty@hotmail.com");

        // ASSERT
        assertThat(userDetails).isNotNull();
        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) userDetails;
        assertThat(userDetailsImpl.getId()).isEqualTo(2L);
        assertThat(userDetailsImpl.getUsername()).isEqualTo("empty@hotmail.com");
        assertThat(userDetailsImpl.getPassword()).isEqualTo("password");
        assertThat(userDetailsImpl.getFirstName()).isEmpty();
        assertThat(userDetailsImpl.getLastName()).isEmpty();
        assertThat(userDetailsImpl.getAdmin()).isNull();

        verify(userRepository).findByEmail("empty@hotmail.com");
    }

    @Test
    void loadUserByUsername_shouldNotCallRepositoryTwice_whenCalledOnce() {
        // ARRANGE
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));

        // ACT
        userDetailsService.loadUserByUsername(testEmail);

        // ASSERT
        verify(userRepository, times(1)).findByEmail(testEmail);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void loadUserByUsername_shouldWorkWithDifferentEmailFormats() {
        // ARRANGE
        String uppercaseEmail = "TEST@FREE.COM";
        User uppercaseUser = new User();
        uppercaseUser.setId(3L);
        uppercaseUser.setEmail(uppercaseEmail);
        uppercaseUser.setPassword("password");
        uppercaseUser.setFirstName("Test");
        uppercaseUser.setLastName("User");

        when(userRepository.findByEmail(uppercaseEmail)).thenReturn(Optional.of(uppercaseUser));

        // ACT
        UserDetails userDetails = userDetailsService.loadUserByUsername(uppercaseEmail);

        // ASSERT
        assertThat(userDetails.getUsername()).isEqualTo(uppercaseEmail);
        verify(userRepository).findByEmail(uppercaseEmail);
    }

    @Test
    void loadUserByUsername_shouldPreserveAllUserProperties() {
        // ARRANGE
        User complexUser = new User();
        complexUser.setId(999L);
        complexUser.setEmail("complex_user@orange.com");
        complexUser.setPassword("complexPassword123!");
        complexUser.setFirstName("Baptiste");
        complexUser.setLastName("Fatin");
        complexUser.setAdmin(true);
        complexUser.setCreatedAt(LocalDateTime.of(2023, 1, 1, 12, 0));
        complexUser.setUpdatedAt(LocalDateTime.of(2023, 12, 31, 23, 59));

        when(userRepository.findByEmail("complex_user@orange.com")).thenReturn(Optional.of(complexUser));

        // ACT
        UserDetails userDetails = userDetailsService.loadUserByUsername("complex_user@orange.com");

        // ASSERT
        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) userDetails;
        assertThat(userDetailsImpl.getId()).isEqualTo(999L);
        assertThat(userDetailsImpl.getUsername()).isEqualTo("complex_user@orange.com");
        assertThat(userDetailsImpl.getPassword()).isEqualTo("complexPassword123!");
        assertThat(userDetailsImpl.getFirstName()).isEqualTo("Baptiste");
        assertThat(userDetailsImpl.getLastName()).isEqualTo("Fatin");
        assertThat(userDetailsImpl.getAdmin()).isNull();

        verify(userRepository).findByEmail("complex_user@orange.com");
    }

    @Test
    void loadUserByUsername_shouldThrowException_whenRepositoryThrowsException() {
        // ARRANGE
        when(userRepository.findByEmail(testEmail)).thenThrow(new RuntimeException("Database error"));

        // ACT & ASSERT
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(testEmail))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");

        verify(userRepository).findByEmail(testEmail);
    }
}
