package com.hotel.room_service.dto;

import com.hotel.room_service.domain.RoomStatus;

public record UpdateRoomStatusRequest(RoomStatus status) {
}
