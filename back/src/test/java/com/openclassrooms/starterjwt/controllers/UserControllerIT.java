package com.openclassrooms.starterjwt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.services.UserService;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser(username = "julien.toureau@hotmail.com", roles = {"USER"})
public class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private JwtUtils jwtUtils;

    private User savedUser;

    @BeforeEach
    void setUp() {
        // Mock JWT validation to avoid 401
        when(jwtUtils.validateJwtToken(anyString())).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken(anyString())).thenReturn("julien.toureau@hotmail.com");

        // Ensure repository clean for predictable tests
        userRepository.deleteAll();

        // create one user matching the mocked principal
        User u = new User();
        u.setEmail("julien.toureau@hotmail.com");
        u.setFirstName("Julien");
        u.setLastName("Toureau");
        u.setPassword("password1234;");
        u.setAdmin(false);
        savedUser = userRepository.save(u);
    }

    @Test
    void findById_shouldReturnUser_whenUserExists() throws Exception {
        mockMvc.perform(get("/api/user/" + savedUser.getId())
                .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value(savedUser.getEmail()));
    }

    @Test
    void findById_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/user/10009")
                .header("Authorization", "Bearer token"))
                .andExpect(status().isNotFound());
    }

    @Test
    void findById_shouldReturnBadRequest_whenIdIsInvalid() throws Exception {
        mockMvc.perform(get("/api/user/invalid")
                .header("Authorization", "Bearer token"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void delete_shouldReturnOk_whenUserExistsAndIsOwner() throws Exception {
        // delete the user
        mockMvc.perform(delete("/api/user/" + savedUser.getId())
                .header("Authorization", "Bearer token")
                .with(csrf()))
                .andExpect(status().isOk());

        // subsequent get should be not found
        mockMvc.perform(get("/api/user/" + savedUser.getId())
                .header("Authorization", "Bearer token"))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_shouldReturnUnauthorized_whenUserIsNotOwner() throws Exception {
        // create a different user
        User other = new User();
        other.setEmail("victor.danieu@email.com");
        other.setFirstName("Victor");
        other.setLastName("Danieu");
        other.setPassword("other_pwd-1234");
        other.setAdmin(false);
        User otherSaved = userRepository.save(other);

        mockMvc.perform(delete("/api/user/" + otherSaved.getId())
                .header("Authorization", "Bearer token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void delete_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
        mockMvc.perform(delete("/api/user/56699887")
                .header("Authorization", "Bearer token"))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_shouldReturnBadRequest_whenIdIsInvalid() throws Exception {
        mockMvc.perform(delete("/api/user/invalid")
                .header("Authorization", "Bearer token"))
                .andExpect(status().isBadRequest());
    }
}
