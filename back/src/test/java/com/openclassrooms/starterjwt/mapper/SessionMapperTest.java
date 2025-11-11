package com.openclassrooms.starterjwt.mapper;

import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.TeacherService;
import com.openclassrooms.starterjwt.services.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class SessionMapperTest {

    @InjectMocks
    SessionMapperImpl sessionMapper;

    @Mock
    TeacherService teacherService;
    @Mock
    UserService userService;



    public SessionMapperTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void toEntity_shouldMapDtoToEntity() {
        SessionDto sessionDto = new SessionDto();
        sessionDto.setId(1L);
        sessionDto.setName("Yoga Session");
        sessionDto.setDescription("description of yoga session");
        sessionDto.setCreatedAt(now());
        sessionDto.setUpdatedAt(now());
        sessionDto.setTeacher_id(1L);
        sessionDto.setUsers(Arrays.asList(2L, 3L));

        Teacher teacher = new Teacher();
        teacher.setId(1L);
        teacher.setFirstName("Marion");
        teacher.setLastName("Dupont");
        teacher.setCreatedAt(now());

        when(teacherService.findById(1L)).thenReturn(teacher);

        User user = new User();
        user.setId(2L);
        User user2 = new User();
        user2.setId(3L);
        when(userService.findById(2L)).thenReturn(user);
        when(userService.findById(3L)).thenReturn(user2);

        Session entity = sessionMapper.toEntity(sessionDto);

        assertThat(entity.getDescription()).isEqualTo("description of yoga session");
        assertThat(entity.getTeacher().getId()).isEqualTo(1L);
        assertThat(entity.getUsers()).extracting(User::getId).containsExactlyInAnyOrder(2L, 3L);
    }

    @Test
    void toDto_shouldMapEntityToDto() {
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        teacher.setFirstName("Marion");
        teacher.setLastName("Dupont");
        teacher.setCreatedAt(now());

        User user = new User();
        user.setId(2L);
        User user2 = new User();
        user2.setId(3L);

        Session session = new Session();
        session.setId(1L);
        session.setName("Yoga Session");
        session.setDescription("description of yoga session");
        session.setTeacher(teacher);
        session.setUsers(Arrays.asList(user, user2));
        session.setCreatedAt(now());
        session.setUpdatedAt(now());

        SessionDto dto = sessionMapper.toDto(session);

        assertThat(dto.getDescription()).isEqualTo("description of yoga session");
        assertThat(dto.getTeacher_id()).isEqualTo(1L);
        assertThat(dto.getUsers()).containsExactlyInAnyOrder(2L, 3L);
    }
}