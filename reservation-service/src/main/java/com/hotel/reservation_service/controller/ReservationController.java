package com.hotel.reservation_service.controller;


import com.hotel.reservation_service.domain.Reservation;
import com.hotel.reservation_service.dto.CreateReservationRequest;
import com.hotel.reservation_service.dto.ReservationResponse;
import com.hotel.reservation_service.service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService service;

    public ReservationController(ReservationService service) {
        this.service = service;
    }


    @PostMapping
    public ResponseEntity<ReservationResponse> create(
            @RequestBody CreateReservationRequest request,
            @RequestHeader("X-User-Id") Long userId // ← Desde header
    ) {
        return ResponseEntity.ok(service.create(request,userId));
    }


    @GetMapping
    public ResponseEntity<List<ReservationResponse>> all() {

        // Que realiza
        // 1. Obtiene todas las reservas
        // 2. Convierte cada reserva a ReservationResponse
        // 3. Retorna la lista de ReservationResponse
        List<ReservationResponse> reservations = service.getAll()
                .stream()
                .map(item -> ReservationResponse.from(item))
                .toList();
        return ResponseEntity.ok(reservations);
    }

    // Obtener una 
    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> getById(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {
       Optional<Reservation> reservation = service.getById(id,userId);

       if (reservation.isEmpty()){
           return ResponseEntity.notFound().build();
       }
       ReservationResponse reservationResponse = ReservationResponse.from(reservation.get());

        return ResponseEntity.ok(reservationResponse);

    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ReservationResponse> cancel(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(service.cancel(id));
    }


    @PutMapping("/{id}/check-in")
    public ResponseEntity<ReservationResponse> checkIn(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(service.checkIn(id));
    }

    @PutMapping("/{id}/check-out")
    public ResponseEntity<ReservationResponse> checkOut(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(service.checkOut(id));
    }
}
