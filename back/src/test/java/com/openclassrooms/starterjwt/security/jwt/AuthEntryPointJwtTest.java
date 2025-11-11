package com.openclassrooms.starterjwt.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.atLeastOnce;

@ExtendWith(MockitoExtension.class)
public class AuthEntryPointJwtTest {

    @InjectMocks
    private AuthEntryPointJwt authEntryPointJwt;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private AuthenticationException authException;

    @Mock
    private ServletOutputStream outputStream;

    @BeforeEach
    void setUp() {
        reset(request, response, authException, outputStream);
    }

    @Test
    void commence_shouldSetUnauthorizedResponseAndWriteJsonError() throws IOException, ServletException {
        // ARRANGE
        String errorMessage = "Authentication failed";
        String servletPath = "/api/test";

        when(authException.getMessage()).thenReturn(errorMessage);
        when(request.getServletPath()).thenReturn(servletPath);
        when(response.getOutputStream()).thenReturn(outputStream);

        // ACT
        authEntryPointJwt.commence(request, response, authException);

        // ASSERT
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(authException, times(2)).getMessage(); // called 2 times  : logger + JSON body
        verify(request).getServletPath();
        verify(response).getOutputStream();
    }

    @Test
    void commence_shouldHandleNullErrorMessage() throws IOException, ServletException {
        // ARRANGE
        String servletPath = "/api/test";

        when(authException.getMessage()).thenReturn(null);
        when(request.getServletPath()).thenReturn(servletPath);
        when(response.getOutputStream()).thenReturn(outputStream);

        // ACT
        authEntryPointJwt.commence(request, response, authException);

        // ASSERT
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(authException, times(2)).getMessage(); // called 2 times : logger + JSON body
        verify(request).getServletPath();
        verify(response).getOutputStream();
    }

    @Test
    void commence_shouldHandleEmptyServletPath() throws IOException, ServletException {
        // ARRANGE
        String errorMessage = "Authentication failed";

        when(authException.getMessage()).thenReturn(errorMessage);
        when(request.getServletPath()).thenReturn("");
        when(response.getOutputStream()).thenReturn(outputStream);

        // ACT
        authEntryPointJwt.commence(request, response, authException);

        // ASSERT
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(authException, times(2)).getMessage(); // Called 2 times : logger + JSON body
        verify(request).getServletPath();
        verify(response).getOutputStream();
    }

    @Test
    void commence_shouldHandleIOException() throws IOException, ServletException {
        // ARRANGE
        String errorMessage = "Authentication failed";
        String servletPath = "/api/test";

        when(authException.getMessage()).thenReturn(errorMessage);
        when(request.getServletPath()).thenReturn(servletPath);
        when(response.getOutputStream()).thenThrow(new IOException("Output stream error"));

        // ACT & ASSERT
        try {
            authEntryPointJwt.commence(request, response, authException);
        } catch (IOException e) {
            // Expected behavior - IOException should be propagated
            verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
            verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            verify(response).getOutputStream();
        }
    }
}
