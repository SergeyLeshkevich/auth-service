package ru.clevertec.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.repository.query.Param;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.clevertec.auth.entity.dto.user.UserRequest;
import ru.clevertec.auth.entity.dto.user.UserResponse;
import ru.clevertec.auth.entity.dto.validation.OnCreate;
import ru.clevertec.auth.entity.dto.validation.OnUpdate;
import ru.clevertec.auth.entity.user.Role;
import ru.clevertec.exceptionhandlerstarter.entity.IncorrectData;

@Validated
@RequestMapping("/users")
@Tag(name = "Users service", description = "Operations related to users")
public interface UserController {

    @Operation(
            summary = "Update user by ID",
            tags = {"User"},
            description = "User update. Returns the updated resource.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "User successfully updated"),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request body or input parameter",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class))),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User by ID not found",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class))),
                    @ApiResponse(
                            responseCode = "500",
                            description = "General application error",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class)))
            })
    @PutMapping("/{id}")
    UserResponse update(@PathVariable("id") Long id, @Validated(OnUpdate.class) @RequestBody UserRequest dto);

    @Operation(
            summary = "Create new user",
            tags = {"User"},
            description = "User creation. Returns the location of a new resource.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "User successfully created"),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request body",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class))),
                    @ApiResponse(
                            responseCode = "500",
                            description = "General application error",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class)))
            })
    @PostMapping
    UserResponse create(@Validated(OnCreate.class) @RequestBody UserRequest dto, @Param("role") Role role);

    @Operation(
            summary = "Get user by ID",
            tags = {"User"},
            description = "Get user. Returns a user by ID.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved user"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User by ID not found",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class))),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Payload is incorrect: malformed, missing mandatory attributes etc",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class))),
                    @ApiResponse(
                            responseCode = "500",
                            description = "General application error",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class)))
            })
    @GetMapping("/{id}")
    UserResponse getById(@PathVariable Long id);

    @Operation(
            summary = "Move user to archive by ID",
            tags = {"User"},
            description = "User successfully moved to archive.")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "404",
                            description = "User by ID not found",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class))),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Payload is incorrect: malformed, missing mandatory attributes etc",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class))),
                    @ApiResponse(
                            responseCode = "500",
                            description = "General application error",
                            content = @Content(schema = @Schema(implementation = IncorrectData.class)))
            })
    @PatchMapping("/{id}")
    void archivedById(@PathVariable Long id);
}
