package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.mapper.TeacherMapper;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.services.TeacherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TeacherControllerTest {

    @InjectMocks
    TeacherController teacherController;

    @Mock
    private TeacherMapper teacherMapper;

    @Mock
    private TeacherService teacherService;

    @BeforeEach
    void setUp() {
        Mockito.reset(teacherMapper, teacherService);
    }

    @Test
    void findById_shouldReturnTeacherDto_whenTeacherExists() {
        // ARRANGE
        String id = "1";
        Long teacherId = 1L;
        Teacher teacher = teacherTest();
        TeacherDto teacherDto = teacherDtoTest();

        when(teacherService.findById(teacherId)).thenReturn(teacher);
        when(teacherMapper.toDto(teacher)).thenReturn(teacherDto);

        // ACT
        ResponseEntity<?> response = teacherController.findById(id);

        // ASSERT
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(TeacherDto.class);

        TeacherDto responseBody = (TeacherDto) response.getBody();
        if (responseBody == null) {
            throw new AssertionError("Response body is null");
        }
        assertThat(responseBody.getId()).isEqualTo(1L);
        assertThat(responseBody.getFirstName()).isEqualTo("Mario");
        assertThat(responseBody.getLastName()).isEqualTo("Castello");

        verify(teacherService).findById(teacherId);
        verify(teacherMapper).toDto(teacher);
    }

    @Test
    void findById_shouldReturnNotFound_whenTeacherDoesNotExist() {
        // ARRANGE
        String id = "1";
        Long teacherId = 1L;

        when(teacherService.findById(teacherId)).thenReturn(null);

        // ACT
        ResponseEntity<?> response = teacherController.findById(id);

        // ASSERT
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();

        verify(teacherService).findById(teacherId);
    }

    @Test
    void findById_shouldReturnBadRequest_whenIdIsNotNumeric() {
        // ARRANGE
        String id = "invalid";

        // ACT
        ResponseEntity<?> response = teacherController.findById(id);

        // ASSERT
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void findAll_shouldReturnListOfTeachersDto_whenTeachersExist() {
        // ARRANGE
        List<Teacher> teachers = Arrays.asList(teacherTest());
        List<TeacherDto> teachersDto = Arrays.asList(teacherDtoTest());

        when(teacherService.findAll()).thenReturn(teachers);
        when(teacherMapper.toDto(teachers)).thenReturn(teachersDto);

        // ACT
        ResponseEntity<?> response = teacherController.findAll();

        // ASSERT
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(List.class);

        @SuppressWarnings("unchecked")
        List<TeacherDto> responseBody = (List<TeacherDto>) response.getBody();
        assertThat(responseBody).hasSize(1);
        assertThat(responseBody.get(0).getId()).isEqualTo(1L);
        assertThat(responseBody.get(0).getFirstName()).isEqualTo("Mario");
        assertThat(responseBody.get(0).getLastName()).isEqualTo("Castello");

        verify(teacherService).findAll();
        verify(teacherMapper).toDto(teachers);
    }

    private TeacherDto teacherDtoTest() {
        TeacherDto teacherDto = new TeacherDto();
        teacherDto.setId(1L);
        teacherDto.setFirstName("Mario");
        teacherDto.setLastName("Castello");
        teacherDto.setCreatedAt(LocalDateTime.parse("2025-08-05T10:00:00"));
        teacherDto.setUpdatedAt(LocalDateTime.parse("2025-08-05T10:00:00"));
        return teacherDto;
    }

    private Teacher teacherTest() {
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        teacher.setFirstName("Mario");
        teacher.setLastName("Castello");
        teacher.setCreatedAt(LocalDateTime.parse("2025-08-05T10:00:00"));
        teacher.setUpdatedAt(LocalDateTime.parse("2025-08-05T10:00:00"));
        return teacher;
    }
}