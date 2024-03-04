package ru.clevertec.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import ru.clevertec.auth.entity.user.Role;
import ru.clevertec.auth.entity.user.User;
import ru.clevertec.auth.service.UserInnerService;
import ru.clevertec.auth.service.props.JwtProperties;
import ru.clevertec.auth.entity.dto.auth.JwtResponse;
import ru.clevertec.exceptionhandlerstarter.exception.AccessDeniedException;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service class for JWT token management.
 * This class provides methods to create and refresh JWT tokens, as well as validate them.
 *
 * @author Sergey Leshkevich
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;
    private final UserDetailsService userDetailsService;
    private final UserInnerService userService;
    private SecretKey key;

    /**
     * Initializes the service by setting up the secret key used for signing JWT tokens.
     */
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
    }

    /**
     * Creates an access token for a user with the given ID, username, and roles.
     *
     * @param userId   the ID of the user.
     * @param username the username of the user.
     * @param roles    the roles of the user.
     * @return a signed JWT access token.
     */
    public String createAccessToken(final Long userId, final String username, final Set<Role> roles) {
        Claims claims = Jwts.claims()
                .subject(username)
                .add("id", userId)
                .add("roles", resolveRoles(roles))
                .build();
        Instant validity = Instant.now()
                .plus(jwtProperties.getAccess(), ChronoUnit.HOURS);
        return Jwts.builder()
                .claims(claims)
                .expiration(Date.from(validity))
                .signWith(key)
                .compact();
    }

    /**
     * Resolves user roles into a list of strings.
     *
     * @param roles the set of user roles.
     * @return a list of role names.
     */
    private List<String> resolveRoles(final Set<Role> roles) {
        return roles.stream()
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    /**
     * Creates a refresh token for a user with the given ID and username.
     *
     * @param userId   the ID of the user.
     * @param username the username of the user.
     * @return a signed JWT refresh token.
     */
    public String createRefreshToken(final Long userId, final String username) {
        Claims claims = Jwts.claims()
                .subject(username)
                .add("id", userId)
                .build();
        Instant validity = Instant.now()
                .plus(jwtProperties.getRefresh(), ChronoUnit.DAYS);
        return Jwts.builder()
                .claims(claims)
                .expiration(Date.from(validity))
                .signWith(key)
                .compact();
    }

    /**
     * Refreshes the user's authentication tokens using the provided refresh token.
     *
     * @param refreshToken the refresh token to use for generating new tokens.
     * @return a JwtResponse containing new access and refresh tokens.
     */
    public JwtResponse refreshUserTokens(String refreshToken) {
        JwtResponse jwtResponse = new JwtResponse();
        if (!isValid(refreshToken)) {
            throw new AccessDeniedException("Token is not valid");
        }
        long userId = getId(refreshToken);
        User user = userService.getById(userId);
        jwtResponse.setUuid(user.getUuid());
        jwtResponse.setId(userId);
        jwtResponse.setUsername(user.getUsername());
        jwtResponse.setRoles(user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()));
        jwtResponse.setAccessToken(
                createAccessToken(userId, user.getUsername(), user.getRoles())
        );
        jwtResponse.setRefreshToken(
                createRefreshToken(userId, user.getUsername())
        );
        return jwtResponse;
    }

    /**
     * Retrieves a JwtResponse containing the user's details and tokens based on the provided JWT token.
     *
     * @param token the JWT token to parse.
     * @return a JwtResponse with user details and tokens.
     */
    public JwtResponse getJwtResponse(String token) {
        JwtResponse jwtResponse = new JwtResponse();
        if (!isValid(token)) {
            throw new AccessDeniedException("Token is not valid");
        }
        long userId = getId(token);
        User user = userService.getById(userId);
        jwtResponse.setId(userId);
        jwtResponse.setUuid(user.getUuid());
        jwtResponse.setUsername(user.getUsername());
        jwtResponse.setRoles(user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()));
        jwtResponse.setAccessToken(token);
        return jwtResponse;
    }

    /**
     * Validates the provided JWT token.
     *
     * @param token the JWT token to validate.
     * @return true if the token is valid, false otherwise.
     */
    public boolean isValid(final String token) {
       try {
           return Jwts
                   .parser()
                   .verifyWith(key)
                   .build()
                   .parseSignedClaims(token)
                   .getPayload()
                   .getExpiration()
                   .after(new Date());
       }catch (Exception e){
           return false;
       }
    }

    /**
     * Extracts the user ID from the provided JWT token.
     *
     * @param token the JWT token to parse.
     * @return the user ID extracted from the token.
     */
    private Long getId(final String token) {
        Jws<Claims> claimsJws = Jwts
                .parser()
                .verifyWith(key)
                .build().parseSignedClaims(token);
        return claimsJws.getPayload()
                .get("id", Long.class);
    }

    /**
     * Extracts the username from the provided JWT token.
     *
     * @param token the JWT token to parse.
     * @return the username extracted from the token.
     */
    private String getUsername(final String token) {
        return Jwts
                .parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * Retrieves the authentication object for the user identified by the JWT token.
     *
     * @param token the JWT token to parse.
     * @return an Authentication object containing the user's details and authorities.
     */
    public Authentication getAuthentication(final String token) {
        String username = getUsername(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                "",
                userDetails.getAuthorities()
        );
    }
}
