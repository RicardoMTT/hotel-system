package com.hotel.reservation_service.service;


import com.hotel.reservation_service.client.GuestClient;
import com.hotel.reservation_service.client.RoomClient;
import com.hotel.reservation_service.domain.Reservation;
import com.hotel.reservation_service.domain.ReservationGuest;
import com.hotel.reservation_service.domain.ReservationStatus;
import com.hotel.reservation_service.dto.*;
import com.hotel.reservation_service.exception.GuestNotFoundException;
import com.hotel.reservation_service.exception.RoomUnavailableException;
import com.hotel.reservation_service.repository.ReservationRepository;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReservationService {

    private static final Logger log = LoggerFactory.getLogger(ReservationService.class);
    private final ReservationRepository repository;
    private final RoomClient roomClient;
    private final GuestClient guestClient;

    public ReservationService(ReservationRepository repository,
                              RoomClient roomClient,
                              GuestClient guestClient) {
        this.repository = repository;
        this.roomClient = roomClient;
        this.guestClient = guestClient;
    }


    // Get resesrvation by uuid , check optional
    public Optional<Reservation> getById(Long id, Long userId){
        return repository.findByIdAndCreatedByUserId(id,userId);
    }

    // get all reservations
    public List<Reservation> getAll() {
        return repository.findAll();
    }

    public List<ReservationResponse> list(
            ReservationStatus status,
            Long roomId,
            Long guestId,
            LocalDate from,
            LocalDate to
    ) {
        Specification<Reservation> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (roomId != null) {
                predicates.add(cb.equal(root.get("roomId"), roomId));
            }

            if (guestId != null) {
                predicates.add(cb.equal(
                        root.join("guests").get("guestId"), guestId
                ));
            }

            if (from != null && to != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("checkInDate"), to));
                predicates.add(cb.greaterThanOrEqualTo(root.get("checkOutDate"), from));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        List<Reservation> reservations = repository.findAll(spec);

        return reservations.stream()
                .map(ReservationResponse::from)
                .toList();
    }


    public ReservationResponse cancel(Long reservationId) {

        Reservation reservation = repository.findById(reservationId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Reservation not found")
                );

        validateCancelable(reservation);

        reservation.setStatus(ReservationStatus.CANCELLED);

        repository.save(reservation);

        return new ReservationResponse(
                reservation.getId(),
                reservation.getStatus()
        );
    }

    private void validateCancelable(Reservation reservation) {

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new IllegalStateException("Reservation is already cancelled");
        }

        if (reservation.getStatus() == ReservationStatus.CHECKED_IN ||
                reservation.getStatus() == ReservationStatus.CHECKED_OUT) {

            throw new IllegalStateException(
                    "Reservation cannot be cancelled after check-in"
            );
        }
    }

    public ReservationResponse create(CreateReservationRequest request,Long userId) {


        validateDates(request.checkIn(), request.checkOut());

        // validateAvailability(request.roomId(), request.checkIn(), request.checkOut());

        // 🔹 1. Verificar disponibilidad en Room Service
//        AvailabilityResponse availability = roomClient.checkAvailability(request.roomId());
//        if (!availability.available()) {
////            throw new IllegalArgumentException("Room is not available");
//            throw new RoomUnavailableException("Cuarto no disponible");
//        }

        try {
            AvailabilityResponse availability = roomClient.checkAvailability(request.roomId());
            if (!availability.available()) {
                throw new RoomUnavailableException("Cuarto no disponible");
            }
        } catch (FeignException.NotFound e) {
            throw new RoomUnavailableException("Cuartoasdadz no disponible");
        } catch (FeignException e) {
            throw new RoomUnavailableException("Cuartozzz no disponible");
        }


        // 🔹 2. Validar que todos los Guest existan en Guest Service
        for (Long guestId : request.guestIds()) {
            try {
                guestClient.getGuestById(guestId);
            } catch (Exception e) {
//                throw new IllegalArgumentException("Guest not found: " + guestId);
                throw new GuestNotFoundException("Guest not found: " + guestId);
            }
        }

        //
        Reservation reservation = new Reservation();
        reservation.setRoomId(request.roomId());
        reservation.setCheckInDate(request.checkIn());
        reservation.setCheckOutDate(request.checkOut());
        reservation.setStatus(ReservationStatus.RESERVED);
        reservation.setCreatedByUserId(userId);

        for (Long guestId : request.guestIds()) {
            ReservationGuest rg = new ReservationGuest();
            rg.setGuestId(guestId);
            rg.setPrimaryGuest(guestId.equals(request.primaryGuestId()));
            rg.setReservation(reservation);
            reservation.getGuests().add(rg);
        }

        repository.save(reservation);

        // 4. ACTUALIZAR EL ESTADO DEL ROOM → RESERVED
        roomClient.updateRoomStatus(
                request.roomId(),
                new UpdateRoomStatusRequest(RoomStatus.RESERVED)
        );
        return new ReservationResponse(reservation.getId(), reservation.getStatus());
    }

    private void validateDates(LocalDate checkIn, LocalDate checkOut) {
        if (!checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException("Check-out must be after check-in");
        }
    }

    private void validateAvailability(Long roomId, LocalDate checkIn, LocalDate checkOut) {
        System.out.println("TTTTTTTT");
        Boolean exists = repository.existsOverlappingReservation(roomId, checkIn, checkOut);
        log.info("TESTsssss" + exists);

        if (Boolean.TRUE.equals(exists)) {
            throw new IllegalStateException("Room is not available for selected dates");
        }
    }


    public ReservationResponse checkIn(Long reservationId) {

        Reservation reservation = repository.findById(reservationId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Reservation not found")
                );

        validateCheckIn(reservation);

        reservation.setStatus(ReservationStatus.CHECKED_IN);
        reservation.setCheckInDate(LocalDate.now());

        repository.save(reservation);

        return new ReservationResponse(
                reservation.getId(),
                reservation.getStatus()
        );
    }

    private void validateCheckIn(Reservation reservation) {

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new IllegalStateException("Reservation is cancelled");
        }

        if (reservation.getStatus() == ReservationStatus.CHECKED_IN) {
            throw new IllegalStateException("Reservation already checked-in");
        }

        if (reservation.getStatus() == ReservationStatus.CHECKED_OUT) {
            throw new IllegalStateException("Reservation already checked-out");
        }

        if (LocalDate.now().isBefore(reservation.getCheckInDate())) {
            throw new IllegalStateException("Check-in date has not arrived yet");
        }
    }


    public ReservationResponse checkOut(Long reservationId) {

        Reservation reservation = repository.findById(reservationId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Reservation not found")
                );

        validateCheckOut(reservation);

        reservation.setStatus(ReservationStatus.CHECKED_OUT);
        reservation.setCheckOutDate(LocalDate.now());

        repository.save(reservation);

        return new ReservationResponse(
                reservation.getId(),
                reservation.getStatus()
        );
    }

    private void validateCheckOut(Reservation reservation) {

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new IllegalStateException("Reservation is cancelled");
        }

        if (reservation.getStatus() == ReservationStatus.CHECKED_IN) {
            throw new IllegalStateException("Reservation already checked-in");
        }

        if (reservation.getStatus() == ReservationStatus.CHECKED_OUT) {
            throw new IllegalStateException("Reservation already checked-out");
        }

        if (LocalDate.now().isBefore(reservation.getCheckOutDate())) {
            throw new IllegalStateException("Check-out date has not arrived yet");
        }
    }
}
