package com.example.jwt.auth.dtos;

public record LoginResponse(
        String token,
        long expiresIn) {
}
