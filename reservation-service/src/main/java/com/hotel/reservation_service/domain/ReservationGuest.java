package com.hotel.reservation_service.domain;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;


@Entity
@Table(name = "reservation_guests")
public class ReservationGuest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reservation_id")
    @JsonBackReference // evita el ciclo infinito
    private Reservation reservation;

    @Column(nullable = false)
    private Long guestId;

    private boolean primaryGuest;

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public Long getGuestId() {
        return guestId;
    }

    public void setGuestId(Long guestId) {
        this.guestId = guestId;
    }

    public boolean isPrimaryGuest() {
        return primaryGuest;
    }

    public void setPrimaryGuest(boolean primaryGuest) {
        this.primaryGuest = primaryGuest;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
