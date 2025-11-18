package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.mapper.SessionMapper;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.services.SessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import org.springframework.http.HttpStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SessionControllerTest {

    @InjectMocks
    SessionController sessionController;

    @Mock
    private SessionMapper sessionMapper;
    @Mock
    private SessionService sessionService;

    // use for resetting mocks before each test
    @BeforeEach
    void setUp() {
        Mockito.reset(sessionMapper, sessionService);
    }

    @Test
    void findById_shouldReturnSessionDto_whenSessionExists() {
        //ARRANGE
        String id = "1";
        Long sessionId = 1L;

        Session session = sessionTest();
        SessionDto sessionDto = sessionDtoTest();

        when(sessionService.getById(sessionId)).thenReturn(session);
        when(sessionMapper.toDto(session)).thenReturn(sessionDto);

        //ACT
        ResponseEntity<?> response = sessionController.findById(id);

        //ASSERT
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(SessionDto.class);

        SessionDto responseBody = (SessionDto) response.getBody();
        if(responseBody == null) {
            throw new AssertionError("Response body is null");
        }
        assertThat(responseBody.getId()).isEqualTo(1L);
        assertThat(responseBody.getName()).isEqualTo("Session yoga test");
        assertThat(responseBody.getDescription()).isEqualTo("Description session yoga test");
        assertThat(responseBody.getTeacher_id()).isEqualTo(4L);

        verify(sessionService).getById(sessionId);
        verify(sessionMapper).toDto(session);
    }

    @Test
    void findById_shouldReturnNotFound_whenSessionDoesNotExist() {
        //ARRANGE
        String id = "99";
        Long sessionId = 99L;

        when(sessionService.getById(sessionId)).thenReturn(null);

        //ACT
        ResponseEntity<?> response = sessionController.findById(id);

        //ASSERT
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void findById_shouldReturnBadRequest_whenIdIsNotNumeric() {
        //ARRANGE
        String id = "invalid";

        //ACT
        ResponseEntity<?> response = sessionController.findById(id);

        //ASSERT
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void findAll_shouldReturnListOfSessionsDto_whenSessionsExist() {
        //ARRANGE
        List<Session> sessions = Arrays.asList(sessionTest());
        List<SessionDto> sessionsDto = Arrays.asList(sessionDtoTest());

        when(sessionService.findAll()).thenReturn(sessions);
        when(sessionMapper.toDto(sessions)).thenReturn(sessionsDto);

        //ACT
        ResponseEntity<?> response = sessionController.findAll();

        //ASSERT
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(List.class);

        // @SuppressWarnings unchecked is used to suppress the warning about the unchecked cast from Object to List<SessionDto>
        @SuppressWarnings("unchecked")
        List<SessionDto> responseBody = (List<SessionDto>) response.getBody();
        assertThat(responseBody).hasSize(1);
        assertThat(responseBody.get(0).getId()).isEqualTo(1L);
        assertThat(responseBody.get(0).getName()).isEqualTo("Session yoga test");
        assertThat(responseBody.get(0).getDescription()).isEqualTo("Description session yoga test");

        verify(sessionService).findAll();
        verify(sessionMapper).toDto(sessions);
    }

    @Test
    void create_shouldReturnCreatedSessionDto_whenSessionDtoIsValid() {
        //ARRANGE
        SessionDto inputDto = sessionDtoTest();
        Session inputSession = sessionTest();
        Session createdSession = sessionTest();
        SessionDto outputDto = sessionDtoTest();

        when(sessionMapper.toEntity(inputDto)).thenReturn(inputSession);
        when(sessionService.create(inputSession)).thenReturn(createdSession);
        when(sessionMapper.toDto(createdSession)).thenReturn(outputDto);

        //ACT
        ResponseEntity<?> response = sessionController.create(inputDto);

        //ASSERT
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(SessionDto.class);

        SessionDto responseBody = (SessionDto) response.getBody();
        if(responseBody == null) {
            throw new AssertionError("Response body is null");
        }
        assertThat(responseBody.getId()).isEqualTo(1L);
        assertThat(responseBody.getName()).isEqualTo("Session yoga test");
        assertThat(responseBody.getDescription()).isEqualTo("Description session yoga test");
        assertThat(responseBody.getTeacher_id()).isEqualTo(4L);

        verify(sessionMapper).toEntity(inputDto);
        verify(sessionService).create(inputSession);
        verify(sessionMapper).toDto(createdSession);
    }

    @Test
    void update_shouldReturnUpdatedSessionDto_whenSessionDtoIsValid() {
        //ARRANGE
        String id = "1";
        Long sessionId = 1L;
        SessionDto inputDto = sessionDtoTest();
        Session inputSession = sessionTest();
        Session updatedSession = sessionTest();
        SessionDto outputDto = sessionDtoTest();

        when(sessionMapper.toEntity(inputDto)).thenReturn(inputSession);
        when(sessionService.update(sessionId, inputSession)).thenReturn(updatedSession);
        when(sessionMapper.toDto(updatedSession)).thenReturn(outputDto);

        //ACT
        ResponseEntity<?> response = sessionController.update(id, inputDto);

        //ASSERT
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(SessionDto.class);

        SessionDto responseBody = (SessionDto) response.getBody();
        if(responseBody == null) {
            throw new AssertionError("Response body is null");
        }
        assertThat(responseBody.getId()).isEqualTo(1L);
        assertThat(responseBody.getName()).isEqualTo("Session yoga test");
        assertThat(responseBody.getDescription()).isEqualTo("Description session yoga test");
        assertThat(responseBody.getTeacher_id()).isEqualTo(4L);

        verify(sessionMapper).toEntity(inputDto);
        verify(sessionService).update(sessionId, inputSession);
        verify(sessionMapper).toDto(updatedSession);
    }

    @Test
    void deleteSave_shouldReturnResponseEntityOk_whenSessionIsDeleted () {
        //ARRANGE
        String id = "1";
        Long sessionId = 1L;
        Session session = sessionTest();

        when(sessionService.getById(sessionId)).thenReturn(session);

        //ACT
        ResponseEntity<?> response = sessionController.save(id);

        //ASSERT
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        verify(sessionService).getById(sessionId);
        verify(sessionService).delete(sessionId);
    }

    @Test
    void participate_shouldReturnResponseEntityOk_whenUserParticipatesToSession () {
        //ARRANGE
        String sessionId = "1";
        String userId = "2";

        //ACT
        ResponseEntity<?> response = sessionController.participate(sessionId, userId);

        //ASSERT
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        verify(sessionService).participate(1L, 2L);
    }

    @Test
    void noLongerParticipate_shouldReturnResponseEntityOk_whenUserNoLongerParticipatesToSession () {
        //ARRANGE
        String sessionId = "1";
        String userId = "2";

        //ACT
        ResponseEntity<?> response = sessionController.noLongerParticipate(sessionId, userId);

        //ASSERT
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        verify(sessionService).noLongerParticipate(1L, 2L);
    }

    private SessionDto sessionDtoTest() {
        SessionDto sessionDto = new SessionDto();
        sessionDto.setId(1L);
        sessionDto.setName("Session yoga test");
        sessionDto.setDescription(("Description session yoga test"));
        sessionDto.setTeacher_id(4L);
        LocalDate localDate = LocalDate.of(2025, 12, 31);
        Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        sessionDto.setDate(date);
        sessionDto.setCreatedAt(LocalDateTime.parse("2025-11-15T10:00:00"));
        sessionDto.setUpdatedAt(LocalDateTime.parse("2025-11-15T10:00:00"));
        return sessionDto;
    }

    private Session sessionTest() {
        Session session = new Session();
        session.setId(1L);
        session.setName("Session yoga test");
        session.setDescription(("Description session yoga test"));
        Teacher teacher = new Teacher();
        teacher.setId(4L);
        teacher.setFirstName("Mario");
        teacher.setLastName("Castello");
        teacher.setCreatedAt(LocalDateTime.parse("2025-08-05T10:00:00"));
        teacher.setUpdatedAt(LocalDateTime.parse("2025-08-05T10:00:00"));
        session.setTeacher(teacher);
        LocalDate localDate = LocalDate.of(2025, 12, 31);
        Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        session.setDate(date);
        session.setCreatedAt(LocalDateTime.parse("2025-11-15T10:00:00"));
        session.setUpdatedAt(LocalDateTime.parse("2025-11-15T10:00:00"));
        return session;
    }

}
