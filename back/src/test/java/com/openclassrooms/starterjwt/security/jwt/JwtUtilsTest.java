package com.openclassrooms.starterjwt.security.jwt;

import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JwtUtilsTest {

    @InjectMocks
    private JwtUtils jwtUtils;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetailsImpl userDetails;

    private String testSecret = "testSecretKey";
    private int testExpirationMs = 3600000; // 1 hour

    @BeforeEach
    void setUp() {
        // Injection of test values into private fields using ReflectionTestUtils
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", testSecret);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", testExpirationMs);
    }

    @Test
    void generateJwtToken_shouldReturnValidToken_whenAuthenticationProvided() {
        // ARRANGE
        String username = "test@gmail.com";
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(username);

        // ACT
        String token = jwtUtils.generateJwtToken(authentication);

        // ASSERT
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();

        // Verify that the token contains the correct username
        String extractedUsername = Jwts.parser()
            .setSigningKey(testSecret)
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
        assertThat(extractedUsername).isEqualTo(username);
    }

    @Test
    void getUserNameFromJwtToken_shouldReturnUsername_whenValidTokenProvided() {
        // ARRANGE
        String username = "test@orange.com";
        String token = Jwts.builder()
            .setSubject(username)
            .setIssuedAt(new Date())
            .setExpiration(new Date((new Date()).getTime() + testExpirationMs))
            .signWith(SignatureAlgorithm.HS512, testSecret)
            .compact();

        // ACT
        String extractedUsername = jwtUtils.getUserNameFromJwtToken(token);

        // ASSERT
        assertThat(extractedUsername).isEqualTo(username);
    }

    @Test
    void validateJwtToken_shouldReturnTrue_whenValidTokenProvided() {
        // ARRANGE
        String validToken = Jwts.builder()
            .setSubject("test@free.com")
            .setIssuedAt(new Date())
            .setExpiration(new Date((new Date()).getTime() + testExpirationMs))
            .signWith(SignatureAlgorithm.HS512, testSecret)
            .compact();

        // ACT
        boolean isValid = jwtUtils.validateJwtToken(validToken);

        // ASSERT
        assertThat(isValid).isTrue();
    }

    @Test
    void validateJwtToken_shouldReturnFalse_whenTokenHasInvalidSignature() {
        // ARRANGE
        String tokenWithWrongSignature = Jwts.builder()
            .setSubject("test@outlook.com")
            .setIssuedAt(new Date())
            .setExpiration(new Date((new Date()).getTime() + testExpirationMs))
            .signWith(SignatureAlgorithm.HS512, "wrongSecret")
            .compact();

        // ACT
        boolean isValid = jwtUtils.validateJwtToken(tokenWithWrongSignature);

        // ASSERT
        assertThat(isValid).isFalse();
    }

    @Test
    void validateJwtToken_shouldReturnFalse_whenTokenIsExpired() {
        // ARRANGE
        String expiredToken = Jwts.builder()
            .setSubject("test@hotmail.com")
            .setIssuedAt(new Date(System.currentTimeMillis() - 2 * testExpirationMs))
            .setExpiration(new Date(System.currentTimeMillis() - testExpirationMs)) // Expiré
            .signWith(SignatureAlgorithm.HS512, testSecret)
            .compact();

        // ACT
        boolean isValid = jwtUtils.validateJwtToken(expiredToken);

        // ASSERT
        assertThat(isValid).isFalse();
    }

    @Test
    void validateJwtToken_shouldReturnFalse_whenTokenIsMalformed() {
        // ARRANGE
        String malformedToken = "this.is.not.a.valid.jwt.token";

        // ACT
        boolean isValid = jwtUtils.validateJwtToken(malformedToken);

        // ASSERT
        assertThat(isValid).isFalse();
    }

    @Test
    void validateJwtToken_shouldReturnFalse_whenTokenIsNull() {
        // ARRANGE
        String nullToken = null;

        // ACT
        boolean isValid = jwtUtils.validateJwtToken(nullToken);

        // ASSERT
        assertThat(isValid).isFalse();
    }

    @Test
    void validateJwtToken_shouldReturnFalse_whenTokenIsEmpty() {
        // ARRANGE
        String emptyToken = "";

        // ACT
        boolean isValid = jwtUtils.validateJwtToken(emptyToken);

        // ASSERT
        assertThat(isValid).isFalse();
    }

    @Test
    void generateJwtToken_shouldCreateTokenWithCorrectExpiration() {
        // ARRANGE
        String username = "test@wanadoo.com";
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(username);

        long beforeGeneration = System.currentTimeMillis();

        // ACT
        String token = jwtUtils.generateJwtToken(authentication);

        // ASSERT
        Date expiration = Jwts.parser()
            .setSigningKey(testSecret)
            .parseClaimsJws(token)
            .getBody()
            .getExpiration();

        long expectedExpirationTime = beforeGeneration + testExpirationMs;
        long actualExpirationTime = expiration.getTime();

        // Timing tolerance of 1 second
        assertThat(actualExpirationTime).isBetween(
            expectedExpirationTime - 1000,
            expectedExpirationTime + 1000
        );
    }

    @Test
    void generateJwtToken_shouldCreateTokenWithIssuedAtDate() {
        // ARRANGE
        String username = "test@yahoo.com";
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(username);

        long beforeGeneration = System.currentTimeMillis();

        // ACT
        String token = jwtUtils.generateJwtToken(authentication);

        // ASSERT
        Date issuedAt = Jwts.parser()
            .setSigningKey(testSecret)
            .parseClaimsJws(token)
            .getBody()
            .getIssuedAt();

        long actualIssuedAt = issuedAt.getTime();

        // Verify issuedAt is between beforeGeneration and now + 1 second
        assertThat(actualIssuedAt).isBetween(
            beforeGeneration - 1000,
            System.currentTimeMillis() + 1000
        );
    }
}
