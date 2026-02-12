package com.hotel.room_service.dto;


public record CreateRoomRequest(
        String roomNumber,
        int floor,
        Long categoryId
) {
}
