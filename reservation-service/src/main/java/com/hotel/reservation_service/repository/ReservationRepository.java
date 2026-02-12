package com.hotel.reservation_service.repository;

import com.hotel.reservation_service.domain.Reservation;
import com.hotel.reservation_service.domain.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> , JpaSpecificationExecutor<Reservation> {


    // Buscar solo si pertenece al usuario que hizo la reserva
    Optional<Reservation> findByIdAndCreatedByUserId(Long id, Long userId);

    // evita reservas solapadas.
    @Query("""
    SELECT COUNT(r) > 0
    FROM Reservation r
    WHERE r.roomId = :roomId
    AND r.status <> 'CANCELLED'
    AND (:checkIn < r.checkOutDate AND :checkOut > r.checkInDate)
""")
    Boolean existsOverlappingReservation(
            Long roomId,
            LocalDate checkIn,
            LocalDate checkOut
    );


    List<Reservation> findByStatus(ReservationStatus status);

    List<Reservation> findByRoomId(Long roomId);

    @Query("""
    SELECT r FROM Reservation r
    JOIN r.guests g
    WHERE g.guestId = :guestId
""")
    List<Reservation> findByGuestId(Long guestId);

    @Query("""
    SELECT r FROM Reservation r
    WHERE r.checkInDate >= :from
      AND r.checkOutDate <= :to
""")
    List<Reservation> findByDateRange(
            LocalDate from,
            LocalDate to
    );

}
