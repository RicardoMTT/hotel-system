package com.example.jwt.auth.dtos;

public record RegisterResponse(
        String email,
        String fullName
) {
}
