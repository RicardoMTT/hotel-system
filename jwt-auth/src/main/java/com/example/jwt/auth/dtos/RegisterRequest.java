package com.example.jwt.auth.dtos;

public record RegisterRequest(
         String email,
         String password,
         String fullName
) {
}
