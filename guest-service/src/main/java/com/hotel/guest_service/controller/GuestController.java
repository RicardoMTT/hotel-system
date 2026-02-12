package com.hotel.guest_service.controller;


import com.hotel.guest_service.domain.CreateGuestRequest;
import com.hotel.guest_service.domain.GuestResponse;
import com.hotel.guest_service.service.GuestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/guests")
public class GuestController {


    private final GuestService service;

    public GuestController(GuestService service) {
        this.service = service;
    }

    // return all guests
    @GetMapping
    public ResponseEntity<List<GuestResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PostMapping
    public ResponseEntity<GuestResponse> create(
            @RequestBody CreateGuestRequest request
    ) {
        return ResponseEntity.ok(service.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GuestResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }
}
