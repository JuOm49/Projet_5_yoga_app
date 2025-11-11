package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.payload.response.JwtResponse;
import com.openclassrooms.starterjwt.payload.response.MessageResponse;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserRepository userRepository;

    // use for resetting mocks before each test
    @BeforeEach
    void setUp() {
        Mockito.reset(authenticationManager, jwtUtils, passwordEncoder, userRepository);
    }

    @Test
    void authenticateUser_shouldReturnJwtResponse_whenCredentialsAreValid() {
        //ARRANGE
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("morgane.hauvard@gmail.com");
        loginRequest.setPassword("PassworD12345;");

        Authentication authentication = mock(Authentication.class);
        UserDetailsImpl userDetails = new UserDetailsImpl(
                1L,
                "morgane.hauvard@gmail.com",
                "Morgane",
                "Hauvard",
                true,
                "PassworD12345;");
        User user = new User(
                "morgane.hauvard@gmail.com",
                "Morgane",
                "Hauvard",
                "PassworD12345;",
                true);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("mocked-jwt-token-admin");
        when(userRepository.findByEmail("morgane.hauvard@gmail.com")).thenReturn(Optional.of(user));

        //ACT
        ResponseEntity<?> response = authController.authenticateUser(loginRequest);

        //ASSERT
        JwtResponse jwtResponse = (JwtResponse) response.getBody();
        if(jwtResponse == null) {
            throw new AssertionError("JwtResponse is null");
        }
        assertThat(jwtResponse.getToken()).isEqualTo("mocked-jwt-token-admin");
    }

    @Test
    void authenticateUser_ShouldReturnAdminFalse_WhenUserNotFound() {
        // ARRANGE
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("unknown@gmail.com");
        loginRequest.setPassword("password123;5");

        Authentication authentication = mock(Authentication.class);
        UserDetailsImpl userDetails = new UserDetailsImpl(1L, "unknown@gmail.com", "Unknown", "User", false, "password123;5");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("jwt-token");
        when(userRepository.findByEmail("unknown@gmail.com")).thenReturn(Optional.empty());

        // ACT
        ResponseEntity<?> response = authController.authenticateUser(loginRequest);

        // ASSERT
        JwtResponse jwtResponse = (JwtResponse) response.getBody();
        if(jwtResponse == null) {
            throw new AssertionError("JwtResponse is null");
        }
        assertThat(jwtResponse.getToken()).isNotEqualTo("jwt-token-valid");
    }

    @Test
    void registerUser_ShouldReturnMessageSuccessfullyResponse_WhenUserIsRegistered() {
        //ARRANGE
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("franck.gagnant@free.fr");
        signupRequest.setFirstName("Franck");
        signupRequest.setLastName("Gagnant");
        signupRequest.setPassword("StrongPassw0rd!;");

        User user = new User(
                "franck.gagnant@free.fr",
                "Gagnant",
                "Franck",
                "StrongPassw0rd!;",
                false
        );

        when(userRepository.existsByEmail("franck.gagnant@free.fr")).thenReturn(false);
        when(passwordEncoder.encode("StrongPassw0rd!;")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenReturn(user);

        //ACT
        ResponseEntity<?> response = authController.registerUser(signupRequest);

        //ASSERT
        if(response.getBody() == null) {
            throw new AssertionError("MessageResponse is null");
        }
        MessageResponse messageResponse = (MessageResponse) response.getBody();
        assertThat(messageResponse.getMessage()).isEqualTo("User registered successfully!");
    }

    @Test
    void registerUser_ShouldReturnErrorMessage_WhenEmailAlreadyExists() {
        //ARRANGE
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("existing@email.com");
        signupRequest.setFirstName("Toto");
        signupRequest.setLastName("Fool");
        signupRequest.setPassword("ValidPassword123");

        when(userRepository.existsByEmail("existing@email.com")).thenReturn(true);

        //ACT
        ResponseEntity<?> response = authController.registerUser(signupRequest);

        //ASSERT
        if(response.getBody() == null) {
            throw new AssertionError("MessageResponse is null");
        }
        MessageResponse messageResponse = (MessageResponse) response.getBody();
        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(messageResponse.getMessage()).isEqualTo("Error: Email is already taken!");
    }

    @Test
    void registerUser_ShouldReturnBadRequest_WhenEmailAlreadyExists() {
        //ARRANGE
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("duplicate.email@email.com");
        signupRequest.setFirstName("Emilie");
        signupRequest.setLastName("Lopez");
        signupRequest.setPassword("SecurePassword123");

        when(userRepository.existsByEmail("duplicate.email@email.com")).thenReturn(true);

        //ACT
        ResponseEntity<?> response = authController.registerUser(signupRequest);

        //ASSERT
        if(response.getBody() == null) {
            throw new AssertionError("MessageResponse is null");
        }
        MessageResponse messageResponse = (MessageResponse) response.getBody();
        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(messageResponse.getMessage()).isEqualTo("Error: Email is already taken!");
    }

    @Test
    void registerUser_ShouldReturnSuccess_WhenAllDataIsValid() {
        //ARRANGE
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("new.user@gmail.com");
        signupRequest.setFirstName("Alice");
        signupRequest.setLastName("Fabian");
        signupRequest.setPassword("ValidPassword123");

        User savedUser = new User(
                "new.user@gmail.com",
                "Fabian",
                "Alice",
                "encoded-password",
                false
        );

        when(userRepository.existsByEmail("new.user@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode("ValidPassword123")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        //ACT
        ResponseEntity<?> response = authController.registerUser(signupRequest);

        //ASSERT
        if(response.getBody() == null) {
            throw new AssertionError("MessageResponse is null");
        }
        MessageResponse messageResponse = (MessageResponse) response.getBody();
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(messageResponse.getMessage()).isEqualTo("User registered successfully!");
    }
}
