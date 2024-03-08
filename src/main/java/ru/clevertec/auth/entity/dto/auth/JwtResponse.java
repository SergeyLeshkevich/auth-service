package ru.clevertec.auth.entity.dto.auth;

import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class JwtResponse {

    private long id;
    private UUID uuid;
    private String username;
    private Set<String> roles;
    private String accessToken;
    private String refreshToken;
}
