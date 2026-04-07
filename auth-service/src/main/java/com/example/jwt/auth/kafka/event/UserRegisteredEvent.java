package com.example.jwt.auth.kafka.event;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * Evento que se publica en Kafka cuando un usuario se registra exitosamente.
 * Viaja serializado como JSON en el topic "user.registered".
 *
 * Usamos record de Java 21 → inmutable, sin boilerplate.
 */
public record UserRegisteredEvent(
        String email,

        String fullName,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime registeredAt
) {

    /**
     * Factory method para construir el evento desde el servicio,
     * inyectando la fecha actual automáticamente.
     */
    public static UserRegisteredEvent of(String email, String fullName) {
        return new UserRegisteredEvent(email, fullName, LocalDateTime.now());
    }
}
