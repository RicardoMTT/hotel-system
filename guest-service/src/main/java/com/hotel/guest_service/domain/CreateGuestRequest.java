package com.hotel.guest_service.domain;

public record CreateGuestRequest(
        String firstName,
        String lastName,
        String documentType,
        String documentNumber,
        String phone,
        String email
) {
}
