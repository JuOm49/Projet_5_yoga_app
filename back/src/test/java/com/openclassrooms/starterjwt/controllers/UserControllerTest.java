package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.mapper.UserMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @InjectMocks
    UserController userController;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserService userService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        Mockito.reset(userMapper, userService, securityContext, authentication, userDetails);
    }

    @Test
    void findById_shouldReturnUserDto_whenUserExists() {
        // ARRANGE
        String id = "1";
        Long userId = 1L;
        User user = userTest();
        UserDto userDto = userDtoTest();

        when(userService.findById(userId)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        // ACT
        ResponseEntity<?> response = userController.findById(id);

        // ASSERT
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(UserDto.class);

        UserDto responseBody = (UserDto) response.getBody();
        if (responseBody == null) {
            throw new AssertionError("Response body is null");
        }
        assertThat(responseBody.getId()).isEqualTo(1L);
        assertThat(responseBody.getEmail()).isEqualTo("test@example.com");
        assertThat(responseBody.getFirstName()).isEqualTo("John");
        assertThat(responseBody.getLastName()).isEqualTo("Doe");

        verify(userService).findById(userId);
        verify(userMapper).toDto(user);
    }

    @Test
    void findById_shouldReturnNotFound_whenUserDoesNotExist() {
        // ARRANGE
        String id = "1";
        Long userId = 1L;

        when(userService.findById(userId)).thenReturn(null);

        // ACT
        ResponseEntity<?> response = userController.findById(id);

        // ASSERT
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();

        verify(userService).findById(userId);
    }

    @Test
    void findById_shouldReturnBadRequest_whenIdIsNotNumeric() {
        // ARRANGE
        String id = "invalid";

        // ACT
        ResponseEntity<?> response = userController.findById(id);

        // ASSERT
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void save_shouldReturnOk_whenUserExistsAndIsOwner() {
        // ARRANGE
        String id = "1";
        Long userId = 1L;
        User user = userTest();
        String userEmail = "test@example.com";

        when(userService.findById(userId)).thenReturn(user);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(userEmail);


        // ACT
        try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class) ) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            ResponseEntity<?> response = userController.save(id);

            // ASSERT
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

            verify(userService).findById(userId);
            verify(userService).delete(userId);
        }
    }

    @Test
    void save_shouldReturnNotFound_whenUserDoesNotExist() {
        // ARRANGE
        String id = "1";
        Long userId = 1L;

        when(userService.findById(userId)).thenReturn(null);

        // ACT
        ResponseEntity<?> response = userController.save(id);

        // ASSERT
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        verify(userService).findById(userId);
        verify(userService, never()).delete(any());
    }

    @Test
    void save_shouldReturnUnauthorized_whenUserIsNotOwner() {
        // ARRANGE
        String id = "1";
        Long userId = 1L;
        User user = userTest();
        String differentUserEmail = "different@example.com";

        when(userService.findById(userId)).thenReturn(user);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(differentUserEmail);


        // ACT
        try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class) ){
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            ResponseEntity<?> response = userController.save(id);

            // ASSERT
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

            verify(userService).findById(userId);
            verify(userService, never()).delete(any());
        }
    }

    @Test
    void save_shouldReturnBadRequest_whenIdIsNotNumeric() {
        // ARRANGE
        String id = "invalid";

        // ACT
        ResponseEntity<?> response = userController.save(id);

        // ASSERT
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        verify(userService, never()).findById(any());
        verify(userService, never()).delete(any());
    }

    private UserDto userDtoTest() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail("test@example.com");
        userDto.setFirstName("John");
        userDto.setLastName("Doe");
        userDto.setAdmin(false);
        userDto.setCreatedAt(LocalDateTime.parse("2025-08-05T10:00:00"));
        userDto.setUpdatedAt(LocalDateTime.parse("2025-08-05T10:00:00"));
        return userDto;
    }

    private User userTest() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setAdmin(false);
        user.setPassword("hashedPassword");
        user.setCreatedAt(LocalDateTime.parse("2025-08-05T10:00:00"));
        user.setUpdatedAt(LocalDateTime.parse("2025-08-05T10:00:00"));
        return user;
    }
}
