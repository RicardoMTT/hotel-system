package com.hotel.reservation_service.dto;

import java.time.LocalDate;
import java.util.List;

public record CreateReservationRequest(
        Long roomId,
        LocalDate checkIn,
        LocalDate checkOut,
        List<Long> guestIds,
        Long primaryGuestId
) {
}
