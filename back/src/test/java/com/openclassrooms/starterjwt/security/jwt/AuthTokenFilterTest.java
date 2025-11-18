package com.openclassrooms.starterjwt.security.jwt;

import com.openclassrooms.starterjwt.security.services.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthTokenFilterTest {

    @InjectMocks
    private AuthTokenFilter authTokenFilter;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private UserDetails userDetails;

    @Mock
    private SecurityContext securityContext;

    @BeforeEach
    void setUp() {
        reset(jwtUtils, userDetailsService, request, response, filterChain, userDetails, securityContext);
    }

    @Test
    void doFilterInternal_shouldSetAuthentication_whenValidJwtToken() throws ServletException, IOException {
        // ARRANGE
        String jwt = "valid.jwt.token";
        String username = "testuser@email.com";
        String authHeader = "Bearer " + jwt;

        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(jwtUtils.validateJwtToken(jwt)).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken(jwt)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // ACT
            authTokenFilter.doFilterInternal(request, response, filterChain);

            // ASSERT
            verify(request).getHeader("Authorization");
            verify(jwtUtils).validateJwtToken(jwt);
            verify(jwtUtils).getUserNameFromJwtToken(jwt);
            verify(userDetailsService).loadUserByUsername(username);
            verify(securityContext).setAuthentication(any(UsernamePasswordAuthenticationToken.class));
            verify(filterChain).doFilter(request, response);
        }
    }

    @Test
    void doFilterInternal_shouldNotSetAuthentication_whenNoAuthorizationHeader() throws ServletException, IOException {
        // ARRANGE
        when(request.getHeader("Authorization")).thenReturn(null);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // ACT
            authTokenFilter.doFilterInternal(request, response, filterChain);

            // ASSERT
            verify(request).getHeader("Authorization");
            verify(jwtUtils, never()).validateJwtToken(any());
            verify(userDetailsService, never()).loadUserByUsername(any());
            verify(securityContext, never()).setAuthentication(any());
            verify(filterChain).doFilter(request, response);
        }
    }

    @Test
    void doFilterInternal_shouldNotSetAuthentication_whenInvalidAuthorizationHeader() throws ServletException, IOException {
        // ARRANGE
        String invalidHeader = "InvalidHeader token";
        when(request.getHeader("Authorization")).thenReturn(invalidHeader);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // ACT
            authTokenFilter.doFilterInternal(request, response, filterChain);

            // ASSERT
            verify(request).getHeader("Authorization");
            verify(jwtUtils, never()).validateJwtToken(any());
            verify(userDetailsService, never()).loadUserByUsername(any());
            verify(securityContext, never()).setAuthentication(any());
            verify(filterChain).doFilter(request, response);
        }
    }

    @Test
    void doFilterInternal_shouldNotSetAuthentication_whenEmptyAuthorizationHeader() throws ServletException, IOException {
        // ARRANGE
        when(request.getHeader("Authorization")).thenReturn("");

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // ACT
            authTokenFilter.doFilterInternal(request, response, filterChain);

            // ASSERT
            verify(request).getHeader("Authorization");
            verify(jwtUtils, never()).validateJwtToken(any());
            verify(userDetailsService, never()).loadUserByUsername(any());
            verify(securityContext, never()).setAuthentication(any());
            verify(filterChain).doFilter(request, response);
        }
    }

    @Test
    void doFilterInternal_shouldNotSetAuthentication_whenInvalidJwtToken() throws ServletException, IOException {
        // ARRANGE
        String jwt = "invalid.jwt.token";
        String authHeader = "Bearer " + jwt;

        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(jwtUtils.validateJwtToken(jwt)).thenReturn(false);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // ACT
            authTokenFilter.doFilterInternal(request, response, filterChain);

            // ASSERT
            verify(request).getHeader("Authorization");
            verify(jwtUtils).validateJwtToken(jwt);
            verify(jwtUtils, never()).getUserNameFromJwtToken(any());
            verify(userDetailsService, never()).loadUserByUsername(any());
            verify(securityContext, never()).setAuthentication(any());
            verify(filterChain).doFilter(request, response);
        }
    }

    @Test
    void doFilterInternal_shouldContinueFilterChain_whenExceptionOccurs() throws ServletException, IOException {
        // ARRANGE
        String jwt = "valid.jwt.token";
        String authHeader = "Bearer " + jwt;

        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(jwtUtils.validateJwtToken(jwt)).thenThrow(new RuntimeException("JWT validation error"));

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // ACT
            authTokenFilter.doFilterInternal(request, response, filterChain);

            // ASSERT
            verify(request).getHeader("Authorization");
            verify(jwtUtils).validateJwtToken(jwt);
            verify(jwtUtils, never()).getUserNameFromJwtToken(any());
            verify(userDetailsService, never()).loadUserByUsername(any());
            verify(securityContext, never()).setAuthentication(any());
            verify(filterChain).doFilter(request, response);
        }
    }

    @Test
    void doFilterInternal_shouldNotSetAuthentication_whenBearerTokenIsEmpty() throws ServletException, IOException {
        // ARRANGE
        String authHeader = "Bearer ";
        String emptyToken = "";

        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(jwtUtils.validateJwtToken(emptyToken)).thenReturn(false);

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // ACT
            authTokenFilter.doFilterInternal(request, response, filterChain);

            // ASSERT
            verify(request).getHeader("Authorization");
            verify(jwtUtils).validateJwtToken(emptyToken);
            verify(jwtUtils, never()).getUserNameFromJwtToken(any());
            verify(userDetailsService, never()).loadUserByUsername(any());
            verify(securityContext, never()).setAuthentication(any());
            verify(filterChain).doFilter(request, response);
        }
    }

    @Test
    void doFilterInternal_shouldNotSetAuthentication_whenBearerTokenIsTooShort() throws ServletException, IOException {
        // ARRANGE
        String authHeader = "Bearer token";
        String extractedToken = "token";

        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(jwtUtils.validateJwtToken(extractedToken)).thenReturn(false); // invalid token

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            // ACT
            authTokenFilter.doFilterInternal(request, response, filterChain);

            // ASSERT
            verify(request).getHeader("Authorization");
            verify(jwtUtils).validateJwtToken(extractedToken);
            verify(jwtUtils, never()).getUserNameFromJwtToken(any());
            verify(userDetailsService, never()).loadUserByUsername(any());
            verify(securityContext, never()).setAuthentication(any());
            verify(filterChain).doFilter(request, response);
        }
    }
}
