package com.hotel.room_service.controller;

import com.hotel.room_service.dto.UpdateRoomStatusRequest;
import com.hotel.room_service.service.RoomService;
import com.hotel.room_service.dto.AvailabilityResponse;
import com.hotel.room_service.dto.CreateRoomRequest;
import com.hotel.room_service.dto.RoomResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService service;

    public RoomController(RoomService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<RoomResponse> create(
            @RequestBody CreateRoomRequest request
    ) {
        return ResponseEntity.ok(service.create(request));
    }

    // Return all rooms
    @GetMapping
    public ResponseEntity<List<RoomResponse>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}/availability")
    public ResponseEntity<AvailabilityResponse> availability(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(new AvailabilityResponse(service.isAvailable(id)));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<RoomResponse> updateStatus(
            @PathVariable Long id,
            @RequestBody UpdateRoomStatusRequest request
    ) {
        return ResponseEntity.ok(service.updateStatus(id, request.status()));
    }
}
