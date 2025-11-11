package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TeacherServiceTest {

    @Mock
    private TeacherRepository teacherRepository;

    @InjectMocks
    private TeacherService teacherService;

    private Teacher teacher;

    @BeforeEach
    void setUp() {
        teacher = new Teacher();
        teacher.setId(1L);
        teacher.setFirstName("John");
        teacher.setLastName("Doe");
        teacher.setCreatedAt(LocalDateTime.now());
        teacher.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void findAll_shouldReturnAllTeachers() {
        // ARRANGE
        List<Teacher> teachers = Arrays.asList(teacher);
        when(teacherRepository.findAll()).thenReturn(teachers);

        // ACT
        List<Teacher> result = teacherService.findAll();

        // ASSERT
        assertThat(result).isEqualTo(teachers);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("John");
        verify(teacherRepository).findAll();
    }

    @Test
    void findAll_shouldReturnEmptyList_whenNoTeachers() {
        // ARRANGE
        when(teacherRepository.findAll()).thenReturn(Arrays.asList());

        // ACT
        List<Teacher> result = teacherService.findAll();

        // ASSERT
        assertThat(result).isEmpty();
        verify(teacherRepository).findAll();
    }

    @Test
    void findById_shouldReturnTeacher_whenExists() {
        // ARRANGE
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));

        // ACT
        Teacher result = teacherService.findById(1L);

        // ASSERT
        assertThat(result).isEqualTo(teacher);
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getLastName()).isEqualTo("Doe");
        verify(teacherRepository).findById(1L);
    }

    @Test
    void findById_shouldReturnNull_whenNotExists() {
        // ARRANGE
        when(teacherRepository.findById(1L)).thenReturn(Optional.empty());

        // ACT
        Teacher result = teacherService.findById(1L);

        // ASSERT
        assertThat(result).isNull();
        verify(teacherRepository).findById(1L);
    }
}
