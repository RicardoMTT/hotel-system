package com.hotel.room_service.service;


import com.hotel.room_service.domain.Room;
import com.hotel.room_service.domain.RoomCategory;
import com.hotel.room_service.domain.RoomStatus;
import com.hotel.room_service.dto.CreateRoomRequest;
import com.hotel.room_service.dto.RoomResponse;
import com.hotel.room_service.repository.RoomCategoryRepository;
import com.hotel.room_service.repository.RoomRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomCategoryRepository categoryRepository;

    public RoomService(
            RoomRepository roomRepository,
            RoomCategoryRepository categoryRepository
    ) {
        this.roomRepository = roomRepository;
        this.categoryRepository = categoryRepository;
    }

    //Return all rooms
    public List<RoomResponse> findAll() {
        return roomRepository.findAll()
                .stream()
                .map(this::map)
                .toList();
    }
    public RoomResponse create(CreateRoomRequest request) {

        RoomCategory category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        Room room = new Room();
        room.setRoomNumber(request.roomNumber());
        room.setFloor(request.floor());
        room.setStatus(RoomStatus.AVAILABLE);
        room.setCategory(category);

        Room saved = roomRepository.save(room);

        return map(saved);
    }

    public boolean isAvailable(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        return room.getStatus() == RoomStatus.AVAILABLE;
    }

    private RoomResponse map(Room room) {
        return new RoomResponse(
                room.getId(),
                room.getRoomNumber(),
                room.getStatus(),
                room.getCategory().getName()
        );
    }

    public RoomResponse updateStatus(Long roomId, RoomStatus status) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        room.setStatus(status);
        roomRepository.save(room);

        return RoomResponse.from(room);
    }
}
