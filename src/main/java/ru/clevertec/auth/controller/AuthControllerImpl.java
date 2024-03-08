package ru.clevertec.auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import ru.clevertec.auth.entity.dto.user.UserRequest;
import ru.clevertec.auth.entity.dto.user.UserResponse;
import ru.clevertec.auth.service.AuthService;
import ru.clevertec.auth.service.UserViewService;
import ru.clevertec.auth.entity.dto.auth.JwtRequest;
import ru.clevertec.auth.entity.dto.auth.JwtResponse;

@Validated
@RestController
@RequiredArgsConstructor
public class AuthControllerImpl implements AuthController{

    private final AuthService authService;
    private final UserViewService userService;

    public JwtResponse login(JwtRequest loginRequest) {
        return authService.login(loginRequest);
    }

    public UserResponse register(UserRequest userDto) {
        return userService.createWithRoleSubscriber(userDto);
    }

    public JwtResponse refresh(String refreshToken) {
        return authService.refresh(refreshToken);
    }

    public ResponseEntity<JwtResponse> validate(String token) {
        return ResponseEntity
                .status(200)
                .body(authService.validate(token));
    }
}
