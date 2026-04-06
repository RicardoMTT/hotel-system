package com.ricardotovart.email_service.kafka.event;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record UserRegisteredEvent(

        String email,

        String fullName,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime registeredAt

) {}
