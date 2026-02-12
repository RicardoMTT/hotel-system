package com.hotel.reservation_service.dto;

import com.hotel.reservation_service.domain.Reservation;
import com.hotel.reservation_service.domain.ReservationStatus;


// Un record es una clase inmutable que se utiliza para representar datos simples.
// Un record crea por detras un constructor, getters
// Usado para dto (data transfer object) u objetos de lectura
public record ReservationResponse(
        Long reservationId,
        ReservationStatus status
) {

    // Método estático para convertir Reservation a ReservationResponse
    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getStatus()

        );
    }
}
