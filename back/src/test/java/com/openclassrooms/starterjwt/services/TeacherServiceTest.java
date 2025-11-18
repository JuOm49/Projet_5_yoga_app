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

import static org.junit.jupiter.api.Assertions.*;
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
        teacher = Teacher.builder()
                .id(1L)
                .firstName("Jacques")
                .lastName("Dupont")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void findAll_shouldReturnAllTeachers() {
        // ARRANGE
        Teacher teacher2 = Teacher.builder()
                .id(2L)
                .firstName("Danise")
                .lastName("Fouceaud")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        List<Teacher> teachers = Arrays.asList(teacher, teacher2);
        when(teacherRepository.findAll()).thenReturn(teachers);

        // ACT
        List<Teacher> result = teacherService.findAll();

        // ASSERT
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(teacher.getId(), result.get(0).getId());
        assertEquals(teacher2.getId(), result.get(1).getId());
        verify(teacherRepository).findAll();
    }

    @Test
    void findAll_shouldReturnEmptyList_whenNoTeachers() {
        // ARRANGE
        when(teacherRepository.findAll()).thenReturn(Arrays.asList());

        // ACT
        List<Teacher> result = teacherService.findAll();

        // ASSERT
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(teacherRepository).findAll();
    }

    @Test
    void findById_shouldReturnTeacher_whenTeacherExists() {
        // ARRANGE
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));

        // ACT
        Teacher result = teacherService.findById(1L);

        // ASSERT
        assertNotNull(result);
        assertEquals(teacher.getId(), result.getId());
        assertEquals(teacher.getFirstName(), result.getFirstName());
        assertEquals(teacher.getLastName(), result.getLastName());
        verify(teacherRepository).findById(1L);
    }

    @Test
    void findById_shouldReturnNull_whenTeacherDoesNotExist() {
        // ARRANGE
        when(teacherRepository.findById(1L)).thenReturn(Optional.empty());

        // ACT
        Teacher result = teacherService.findById(1L);

        // ASSERT
        assertNull(result);
        verify(teacherRepository).findById(1L);
    }

    @Test
    void findById_shouldReturnNull_whenIdIsNull() {
        // ACT
        Teacher result = teacherService.findById(null);

        // ASSERT
        assertNull(result);
        verify(teacherRepository).findById(null);
    }

    @Test
    void findById_shouldHandleNegativeId() {
        // ARRANGE
        when(teacherRepository.findById(-1L)).thenReturn(Optional.empty());

        // ACT
        Teacher result = teacherService.findById(-1L);

        // ASSERT
        assertNull(result);
        verify(teacherRepository).findById(-1L);
    }

    @Test
    void findById_shouldHandleZeroId() {
        // ARRANGE
        when(teacherRepository.findById(0L)).thenReturn(Optional.empty());

        // ACT
        Teacher result = teacherService.findById(0L);

        // ASSERT
        assertNull(result);
        verify(teacherRepository).findById(0L);
    }
}
