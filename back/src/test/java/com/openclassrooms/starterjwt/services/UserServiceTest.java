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

import static org.junit.jupiter.api.Assertions.*;
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
        user = User.builder()
                .id(1L)
                .email("aurelie.darand@test.com")
                .firstName("Aurélie")
                .lastName("Darand")
                .password("password123")
                .admin(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void delete_shouldCallRepositoryDeleteById() {
        // ARRANGE
        Long userId = 1L;

        // ACT
        userService.delete(userId);

        // ASSERT
        verify(userRepository).deleteById(userId);
    }

    @Test
    void delete_shouldCallRepositoryDeleteById_whenIdIsNull() {
        // ACT
        userService.delete(null);

        // ASSERT
        verify(userRepository).deleteById(null);
    }

    @Test
    void delete_shouldCallRepositoryDeleteById_whenIdIsZero() {
        // ARRANGE
        Long userId = 0L;

        // ACT
        userService.delete(userId);

        // ASSERT
        verify(userRepository).deleteById(userId);
    }

    @Test
    void delete_shouldCallRepositoryDeleteById_whenIdIsNegative() {
        // ARRANGE
        Long userId = -1L;

        // ACT
        userService.delete(userId);

        // ASSERT
        verify(userRepository).deleteById(userId);
    }

    @Test
    void findById_shouldReturnUser_whenUserExists() {
        // ARRANGE
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // ACT
        User result = userService.findById(1L);

        // ASSERT
        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getFirstName(), result.getFirstName());
        assertEquals(user.getLastName(), result.getLastName());
        assertEquals(user.isAdmin(), result.isAdmin());
        verify(userRepository).findById(1L);
    }

    @Test
    void findById_shouldReturnNull_whenUserDoesNotExist() {
        // ARRANGE
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // ACT
        User result = userService.findById(1L);

        // ASSERT
        assertNull(result);
        verify(userRepository).findById(1L);
    }

    @Test
    void findById_shouldReturnNull_whenIdIsNull() {
        // ACT
        User result = userService.findById(null);

        // ASSERT
        assertNull(result);
        verify(userRepository).findById(null);
    }

    @Test
    void findById_shouldHandleNegativeId() {
        // ARRANGE
        when(userRepository.findById(-1L)).thenReturn(Optional.empty());

        // ACT
        User result = userService.findById(-1L);

        // ASSERT
        assertNull(result);
        verify(userRepository).findById(-1L);
    }

    @Test
    void findById_shouldHandleZeroId() {
        // ARRANGE
        when(userRepository.findById(0L)).thenReturn(Optional.empty());

        // ACT
        User result = userService.findById(0L);

        // ASSERT
        assertNull(result);
        verify(userRepository).findById(0L);
    }

    @Test
    void findById_shouldReturnAdminUser_whenUserIsAdmin() {
        // ARRANGE
        User adminUser = User.builder()
                .id(2L)
                .email("admin@test.com")
                .firstName("Admin")
                .lastName("User")
                .password("adminpass")
                .admin(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(userRepository.findById(2L)).thenReturn(Optional.of(adminUser));

        // ACT
        User result = userService.findById(2L);

        // ASSERT
        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("admin@test.com", result.getEmail());
        assertTrue(result.isAdmin());
        verify(userRepository).findById(2L);
    }
}
