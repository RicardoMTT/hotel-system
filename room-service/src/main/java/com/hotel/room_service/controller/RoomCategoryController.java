package com.hotel.room_service.controller;

import com.hotel.room_service.dto.CreateRoomCategoryRequest;
import com.hotel.room_service.dto.RoomCategoryResponse;
import com.hotel.room_service.service.RoomCategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/room-categories")
public class RoomCategoryController {

    private final RoomCategoryService service;

    public RoomCategoryController(RoomCategoryService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<RoomCategoryResponse> create(
            @RequestBody CreateRoomCategoryRequest request
    ) {
        return ResponseEntity.ok(service.create(request));
    }

    @GetMapping
    public ResponseEntity<List<RoomCategoryResponse>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }
}
