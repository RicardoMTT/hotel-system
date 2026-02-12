package com.hotel.guest_service.service;


import com.hotel.guest_service.domain.CreateGuestRequest;
import com.hotel.guest_service.domain.Guest;
import com.hotel.guest_service.domain.GuestResponse;
import com.hotel.guest_service.repository.GuestRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class GuestService {

    private final GuestRepository repository;

    public GuestService(GuestRepository repository) {
        this.repository = repository;
    }

    // REturn all guests
    public List<GuestResponse> getAll() {
        return repository.findAll().stream()
                .map(g -> new GuestResponse(
                        g.getId(),
                        g.getFirstName() + " " + g.getLastName(),
                        g.getDocumentNumber()
                ))
                .collect(Collectors.toList());
    }
    public GuestResponse create(CreateGuestRequest request) {

        repository.findByDocumentNumber(request.documentNumber())
                .ifPresent(g -> {
                    throw new IllegalStateException("Guest already exists");
                });

        Guest guest = new Guest();
        guest.setFirstName(request.firstName());
        guest.setLastName(request.lastName());
        guest.setDocumentType(request.documentType());
        guest.setDocumentNumber(request.documentNumber());
        guest.setPhone(request.phone());
        guest.setEmail(request.email());

        Guest saved = repository.save(guest);

        return new GuestResponse(
                saved.getId(),
                saved.getFirstName() + " " + saved.getLastName(),
                saved.getDocumentNumber()
        );
    }

    public GuestResponse getById(Long id) {
        Guest guest = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Guest not found"));

        return new GuestResponse(
                guest.getId(),
                guest.getFirstName() + " " + guest.getLastName(),
                guest.getDocumentNumber()
        );
    }

}
