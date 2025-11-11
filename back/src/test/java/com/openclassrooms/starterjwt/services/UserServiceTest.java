package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPassword("password");
        user.setAdmin(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void delete_shouldCallRepositoryDeleteById() {
        // ARRANGE
        Long id = 1L;

        // ACT
        userService.delete(id);

        // ASSERT
        verify(userRepository).deleteById(id);
    }

    @Test
    void delete_shouldCallRepositoryDeleteById_withDifferentId() {
        // ARRANGE
        Long id = 999L;

        // ACT
        userService.delete(id);

        // ASSERT
        verify(userRepository).deleteById(id);
    }

    @Test
    void findById_shouldReturnUser_whenExists() {
        // ARRANGE
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // ACT
        User result = userService.findById(1L);

        // ASSERT
        assertThat(result).isEqualTo(user);
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("test@test.com");
        assertThat(result.getFirstName()).isEqualTo("Test");
        assertThat(result.getLastName()).isEqualTo("User");
        assertThat(result.isAdmin()).isFalse();
        verify(userRepository).findById(1L);
    }

    @Test
    void findById_shouldReturnNull_whenNotExists() {
        // ARRANGE
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // ACT
        User result = userService.findById(1L);

        // ASSERT
        assertThat(result).isNull();
        verify(userRepository).findById(1L);
    }

    @Test
    void findById_shouldReturnNull_whenIdIsNull() {
        // ARRANGE
        when(userRepository.findById(null)).thenReturn(Optional.empty());

        // ACT
        User result = userService.findById(null);

        // ASSERT
        assertThat(result).isNull();
        verify(userRepository).findById(null);
    }
}
