package com.hotel.room_service.dto;

import com.hotel.room_service.domain.Room;
import com.hotel.room_service.domain.RoomStatus;


public record RoomResponse(
        Long id,
        String roomNumber,
        RoomStatus status,
        String category
) {

    public static RoomResponse from(Room room) {
        return new RoomResponse(
                room.getId(),
                room.getRoomNumber(),
                room.getStatus(),
                room.getCategory().getName()
        );
    }
}
