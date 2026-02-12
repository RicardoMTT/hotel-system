package com.hotel.guest_service.domain;


public record GuestResponse(
        Long id,
        String fullName,
        String documentNumber
) {
}
