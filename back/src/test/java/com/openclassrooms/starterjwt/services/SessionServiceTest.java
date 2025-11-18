package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SessionService sessionService;

    private Session session;
    private User user;

    @BeforeEach
    void setUp() {
        session = new Session();
        session.setId(1L);
        session.setName("Test Session");
        session.setDescription("Test Description");
        session.setUsers(new ArrayList<>());

        user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPassword("password");
        user.setAdmin(false);
    }

    @Test
    void create_shouldReturnSavedSession() {
        // ARRANGE
        when(sessionRepository.save(session)).thenReturn(session);

        // ACT
        Session result = sessionService.create(session);

        // ASSERT
        assertThat(result).isEqualTo(session);
        verify(sessionRepository).save(session);
    }

    @Test
    void delete_shouldCallRepositoryDeleteById() {
        // ARRANGE
        Long id = 1L;

        // ACT
        sessionService.delete(id);

        // ASSERT
        verify(sessionRepository).deleteById(id);
    }

    @Test
    void findAll_shouldReturnAllSessions() {
        // ARRANGE
        List<Session> sessions = Arrays.asList(session);
        when(sessionRepository.findAll()).thenReturn(sessions);

        // ACT
        List<Session> result = sessionService.findAll();

        // ASSERT
        assertThat(result).isEqualTo(sessions);
        verify(sessionRepository).findAll();
    }

    @Test
    void getById_shouldReturnSession_whenExists() {
        // ARRANGE
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        // ACT
        Session result = sessionService.getById(1L);

        // ASSERT
        assertThat(result).isEqualTo(session);
        verify(sessionRepository).findById(1L);
    }

    @Test
    void getById_shouldReturnNull_whenNotExists() {
        // ARRANGE
        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());

        // ACT
        Session result = sessionService.getById(1L);

        // ASSERT
        assertThat(result).isNull();
        verify(sessionRepository).findById(1L);
    }

    @Test
    void update_shouldSetIdAndSave() {
        // ARRANGE
        Long id = 2L;
        when(sessionRepository.save(session)).thenReturn(session);

        // ACT
        Session result = sessionService.update(id, session);

        // ASSERT
        assertThat(session.getId()).isEqualTo(id);
        assertThat(result).isEqualTo(session);
        verify(sessionRepository).save(session);
    }

    @Test
    void participate_shouldAddUserToSession() {
        // ARRANGE
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(sessionRepository.save(session)).thenReturn(session);

        // ACT
        sessionService.participate(1L, 1L);

        // ASSERT
        assertThat(session.getUsers()).contains(user);
        verify(sessionRepository).save(session);
    }

    @Test
    void participate_shouldThrowNotFoundException_whenSessionNotFound() {
        // ARRANGE
        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // ACT & ASSERT
        assertThrows(NotFoundException.class, () -> sessionService.participate(1L, 1L));
    }

    @Test
    void participate_shouldThrowNotFoundException_whenUserNotFound() {
        // ARRANGE
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(NotFoundException.class, () -> sessionService.participate(1L, 1L));
    }

    @Test
    void participate_shouldThrowBadRequestException_whenUserAlreadyParticipates() {
        // ARRANGE
        session.getUsers().add(user);
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // ACT & ASSERT
        assertThrows(BadRequestException.class, () -> sessionService.participate(1L, 1L));
    }

    @Test
    void noLongerParticipate_shouldRemoveUserFromSession() {
        // ARRANGE
        session.getUsers().add(user);
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(sessionRepository.save(any(Session.class))).thenReturn(session);

        // ACT
        sessionService.noLongerParticipate(1L, 1L);

        // ASSERT
        assertThat(session.getUsers()).doesNotContain(user);
        verify(sessionRepository).save(any(Session.class));
    }

    @Test
    void noLongerParticipate_shouldThrowNotFoundException_whenSessionNotFound() {
        // ARRANGE
        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(NotFoundException.class, () -> sessionService.noLongerParticipate(1L, 1L));
    }

    @Test
    void noLongerParticipate_shouldThrowBadRequestException_whenUserNotParticipating() {
        // ARRANGE
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        // ACT & ASSERT
        assertThrows(BadRequestException.class, () -> sessionService.noLongerParticipate(1L, 1L));
    }
}
