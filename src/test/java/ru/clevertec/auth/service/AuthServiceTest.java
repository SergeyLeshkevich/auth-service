package ru.clevertec.auth.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import ru.clevertec.auth.entity.dto.auth.JwtRequest;
import ru.clevertec.auth.entity.dto.auth.JwtResponse;
import ru.clevertec.auth.entity.user.User;
import ru.clevertec.auth.security.JwtTokenProvider;
import ru.clevertec.auth.service.impl.AuthServiceImpl;
import ru.clevertec.auth.util.JwtRequestBuilderTest;
import ru.clevertec.auth.util.JwtResponseBuilderTest;
import ru.clevertec.auth.util.UserTestBuilderTest;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserInnerService userService;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @InjectMocks
    private AuthServiceImpl authService;


    @Test
    void shouldAuthenticateUserAndGenerateJwtTokens() {
        JwtRequest loginRequest = JwtRequestBuilderTest.aJwtRequest().build();
        User user = UserTestBuilderTest.anUser().build();
        Authentication authentication = new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());

        when(userService.getByUsername(anyString())).thenReturn(user);
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
        when(jwtTokenProvider.createAccessToken(anyLong(), anyString(), anySet())).thenReturn("access-token");
        when(jwtTokenProvider.createRefreshToken(anyLong(), anyString())).thenReturn("refresh-token");

        JwtResponse jwtResponse = authService.login(loginRequest);

        assertThat(jwtResponse.getUsername()).isEqualTo(loginRequest.getUsername());
        assertThat(jwtResponse.getAccessToken()).isNotBlank();
        assertThat(jwtResponse.getRefreshToken()).isNotBlank();
    }

    @Test
    void shouldThrowExceptionWhenAuthenticationFails() {
        JwtRequest loginRequest = JwtRequestBuilderTest.aJwtRequest().build();

        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Bad credentials");
    }

    @Test
    void shouldRefreshJwtTokens() {
        JwtResponse jwtResponse = JwtResponseBuilderTest.aJwtResponse().build();

        when(jwtTokenProvider.refreshUserTokens("valid-refresh-token")).thenReturn(jwtResponse);

        JwtResponse actual = authService.refresh("valid-refresh-token");

        assertThat(actual.getAccessToken()).isNotBlank();
        assertThat(actual.getRefreshToken()).isNotBlank();
        verify(jwtTokenProvider).refreshUserTokens("valid-refresh-token");
    }

    @Test
    void shouldValidateJwtToken() {
        JwtResponse jwtResponse = JwtResponseBuilderTest.aJwtResponse().build();

        when(jwtTokenProvider.getJwtResponse("access-token")).thenReturn(jwtResponse);

        JwtResponse result = authService.validate("access-token");

        assertThat(result.getUsername()).isEqualTo("Test userName");
        verify(jwtTokenProvider).getJwtResponse("access-token");
    }
}