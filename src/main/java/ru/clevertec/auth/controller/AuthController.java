package ru.clevertec.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.clevertec.auth.entity.dto.auth.JwtRequest;
import ru.clevertec.auth.entity.dto.auth.JwtResponse;
import ru.clevertec.auth.entity.dto.user.UserRequest;
import ru.clevertec.auth.entity.dto.user.UserResponse;
import ru.clevertec.auth.entity.dto.validation.OnCreate;
import ru.clevertec.exceptionhandlerstarter.entity.IncorrectData;

@Validated
@RequestMapping("/auth")
@Tag(name = "Auth service", description = "Security related operations")
public interface AuthController {

    @Operation(
            summary = "Authenticate user",
            tags = {"Auth"},
            description = "User authentication. Returns JwtResponse.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Authentication successful"),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid credentials",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class))),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Authentication failed",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class))),
                    @ApiResponse(
                            responseCode = "500",
                            description = "General application error",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class)))
            })
    @PostMapping("/login")
    JwtResponse login(@Validated @RequestBody final JwtRequest loginRequest);

    @Operation(
            summary = "Register new user",
            tags = {"Auth"},
            description = "User registration. Returns user data.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "Registration successful"),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request data",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class))),
                    @ApiResponse(
                            responseCode = "409",
                            description = "User already exists",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class))),
                    @ApiResponse(
                            responseCode = "500",
                            description = "General application error",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class)))
            })
    @PostMapping("/register")
    UserResponse register(@Validated(OnCreate.class) @RequestBody final UserRequest userDto);

    @Operation(
            summary = "Refresh JWT token",
            tags = {"Auth"},
            description = "Token refresh. Returns new JWT token.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid refresh token",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class))),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Refresh token expired",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class))),
                    @ApiResponse(
                            responseCode = "500",
                            description = "General application error",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class)))
            })
    @PostMapping("/refresh")
    JwtResponse refresh(@RequestBody final String refreshToken);

    @Operation(
            summary = "Validate JWT token",
            tags = {"Auth"},
            description = "Token validation. Returns validation status.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Token is valid"),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid token",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class))),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Token expired",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class))),
                    @ApiResponse(
                            responseCode = "500",
                            description = "General application error",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class)))
            })
    @PostMapping("/validate")
    ResponseEntity<JwtResponse> validate(@RequestBody final String token);
}
