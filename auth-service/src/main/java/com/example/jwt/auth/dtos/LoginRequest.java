package com.example.jwt.auth.dtos;

public record LoginRequest(
        String email,
        String password
) {
}
