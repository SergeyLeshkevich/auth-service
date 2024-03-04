package ru.clevertec.auth.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import ru.clevertec.auth.entity.user.User;
import ru.clevertec.auth.service.AuthService;
import ru.clevertec.auth.service.UserInnerService;
import ru.clevertec.auth.entity.dto.auth.JwtRequest;
import ru.clevertec.auth.entity.dto.auth.JwtResponse;
import ru.clevertec.auth.security.JwtTokenProvider;

import java.util.stream.Collectors;

/**
 * Service implementation for authentication operations.
 * This class handles the login, token refresh, and token validation processes.
 *
 * @author Sergey Leshkevich
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserInnerService userService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Authenticates a user and generates JWT tokens based on the provided login request.
     *
     * @param loginRequest the login request containing the username and password.
     * @return a JwtResponse containing the user's details and JWT tokens.
     */
    @Override
    public JwtResponse login(final JwtRequest loginRequest) {
        JwtResponse jwtResponse = new JwtResponse();

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(), loginRequest.getPassword())
            );
        User user = userService.getByUsername(loginRequest.getUsername());
        jwtResponse.setId(user.getId());
        jwtResponse.setUuid(user.getUuid());
        jwtResponse.setUsername(user.getUsername());
        jwtResponse.setRoles(user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()));
        jwtResponse.setAccessToken(jwtTokenProvider.createAccessToken(
                user.getId(), user.getUsername(), user.getRoles())
        );
        jwtResponse.setRefreshToken(jwtTokenProvider.createRefreshToken(
                user.getId(), user.getUsername())
        );
        return jwtResponse;
    }

    /**
     * Refreshes the JWT tokens using the provided refresh token.
     *
     * @param refreshToken the refresh token to use for generating new tokens.
     * @return a JwtResponse containing the new JWT tokens.
     */
    @Override
    public JwtResponse refresh(final String refreshToken) {
        return jwtTokenProvider.refreshUserTokens(refreshToken);
    }

    /**
     * Validates the provided JWT token and retrieves the associated user's details.
     *
     * @param token the JWT token to validate.
     * @return a JwtResponse containing the user's details if the token is valid.
     */
    @Override
    public JwtResponse validate(String token) {
        return jwtTokenProvider.getJwtResponse(token);
    }
}
