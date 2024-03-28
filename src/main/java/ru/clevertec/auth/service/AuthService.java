package ru.clevertec.auth.service;


import ru.clevertec.auth.entity.dto.auth.JwtRequest;
import ru.clevertec.auth.entity.dto.auth.JwtResponse;

public interface AuthService {

    JwtResponse login(JwtRequest loginRequest);

    JwtResponse refresh(String refreshToken);

    JwtResponse validate(String token);
}
