package com.openclassrooms.starterjwt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.SessionService;
import com.openclassrooms.starterjwt.services.TeacherService;
import com.openclassrooms.starterjwt.services.UserService;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.openclassrooms.starterjwt.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser(username = "testuser", roles = {"USER"})
public class SessionControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private JwtUtils jwtUtils;

    private Session session;
    private Teacher usedTeacher;
    private User testUser;

    @BeforeEach
    void setUp() {
        // Mock JWT validation to avoid 401 errors
        when(jwtUtils.validateJwtToken(org.mockito.ArgumentMatchers.anyString())).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken(org.mockito.ArgumentMatchers.anyString())).thenReturn("testuser");

        List<Teacher> teachers = teacherService.findAll();
        if (teachers.isEmpty()) {
            usedTeacher = null;
            return;
        }
        usedTeacher = teachers.get(0);
        Session sessionEntity = Session.builder()
                .name("Yoga Test")
                .description("Test session")
                .date(new Date())
                .teacher(usedTeacher)
                .users(Collections.emptyList())
                .build();
        session = sessionService.create(sessionEntity);
        // Create a test user
        testUser = userService.findById(1L);
        if (testUser == null) {
            User newUser = new User();
            newUser.setEmail("testuser@email.com");
            newUser.setFirstName("Test");
            newUser.setLastName("User");
            newUser.setPassword("password");
            testUser = userRepository.save(newUser);
        }
    }

    @Test
    void findById_shouldReturnSession_whenIdExists() throws Exception {
        if (usedTeacher == null) return; // Ignore si pas de teacher
        mockMvc.perform(get("/api/session/" + session.getId())
                .header("Authorization", "Bearer test.jwt.token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(session.getId().intValue())))
                .andExpect(jsonPath("$.name", is(session.getName())));
    }

    @Test
    void findById_shouldReturnNotFound_whenIdDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/session/99999")
                .header("Authorization", "Bearer test.jwt.token"))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAll_shouldReturnListOfSessions() throws Exception {
        mockMvc.perform(get("/api/session")
                .header("Authorization", "Bearer test.jwt.token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    void create_shouldReturnCreatedSession() throws Exception {
        if (usedTeacher == null) return;
        SessionDto dto = new SessionDto();
        dto.setName("New session");
        dto.setDescription("Description of the new session");
        dto.setTeacher_id(usedTeacher.getId());
        dto.setDate(new Date());
        dto.setUsers(Collections.emptyList());
        mockMvc.perform(post("/api/session")
                .header("Authorization", "Bearer test.jwt.token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(dto.getName())));
    }

    @Test
    void update_shouldReturnUpdatedSession() throws Exception {
        if (usedTeacher == null) return;
        SessionDto dto = new SessionDto();
        dto.setName("udpated name's session");
        dto.setDescription("updated description of the session");
        dto.setTeacher_id(usedTeacher.getId());
        dto.setDate(new Date());
        dto.setUsers(Collections.emptyList());
        mockMvc.perform(put("/api/session/" + session.getId())
                .header("Authorization", "Bearer test.jwt.token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(dto.getName())));
    }

    @Test
    void delete_shouldRemoveSession() throws Exception {
        if (usedTeacher == null) return;
        mockMvc.perform(delete("/api/session/" + session.getId())
                .header("Authorization", "Bearer test.jwt.token"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/session/" + session.getId())
                .header("Authorization", "Bearer test.jwt.token"))
                .andExpect(status().isNotFound());
    }
}
