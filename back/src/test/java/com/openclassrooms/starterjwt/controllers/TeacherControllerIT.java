package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.mapper.TeacherMapper;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.services.TeacherService;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "testUser", roles = {"USER"})
class TeacherControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TeacherService teacherService;

    @MockBean
    private TeacherMapper teacherMapper;

    @MockBean
    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        Mockito.when(jwtUtils.validateJwtToken(org.mockito.ArgumentMatchers.anyString())).thenReturn(true);
        Mockito.when(jwtUtils.getUserNameFromJwtToken(org.mockito.ArgumentMatchers.anyString())).thenReturn("testUser");
    }

    @Test
    @DisplayName("GET /api/teacher/{id} - valid id")
    void findById_shouldReturnTeacherDto_whenIdIsValid() throws Exception {
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        teacher.setLastName("Dupont");
        teacher.setFirstName("Jean");
        TeacherDto teacherDto = new TeacherDto();
        teacherDto.setId(1L);
        teacherDto.setLastName("Dupont");
        teacherDto.setFirstName("Jean");
        Mockito.when(teacherService.findById(1L)).thenReturn(teacher);
        Mockito.when(teacherMapper.toDto(teacher)).thenReturn(teacherDto);
        mockMvc.perform(get("/api/teacher/1").header("Authorization", "Bearer test.jwt.token"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.lastName").value("Dupont"))
                .andExpect(jsonPath("$.firstName").value("Jean"));
    }

    @Test
    @DisplayName("GET /api/teacher/{id} - non-existent id")
    void findById_shouldReturnNotFound_whenTeacherNotFound() throws Exception {
        Mockito.when(teacherService.findById(anyLong())).thenReturn(null);
        mockMvc.perform(get("/api/teacher/999").header("Authorization", "Bearer test.jwt.token"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/teacher/{id} - non-numeric id")
    void findById_shouldReturnBadRequest_whenIdIsNotNumber() throws Exception {
        mockMvc.perform(get("/api/teacher/abc").header("Authorization", "Bearer test.jwt.token"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/teacher - list of teachers")
    void findAll_shouldReturnListOfTeachers() throws Exception {
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        teacher.setLastName("Dupont");
        teacher.setFirstName("Jean");
        List<Teacher> teachers = Collections.singletonList(teacher);
        TeacherDto teacherDto = new TeacherDto();
        teacherDto.setId(1L);
        teacherDto.setLastName("Dupont");
        teacherDto.setFirstName("Jean");
        List<TeacherDto> teachersDto = List.of(teacherDto);
        Mockito.when(teacherService.findAll()).thenReturn(teachers);
        Mockito.when(teacherMapper.toDto(teachers)).thenReturn(teachersDto);
        mockMvc.perform(get("/api/teacher").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer test.jwt.token"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].lastName").value("Dupont"))
                .andExpect(jsonPath("$[0].firstName").value("Jean"));
    }
}
