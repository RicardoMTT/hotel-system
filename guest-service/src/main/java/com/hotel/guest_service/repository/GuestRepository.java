package com.hotel.guest_service.repository;

import com.hotel.guest_service.domain.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GuestRepository extends JpaRepository<Guest, Long> {

    Optional<Guest> findByDocumentNumber(String documentNumber);

}
