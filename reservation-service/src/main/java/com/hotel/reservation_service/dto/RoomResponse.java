package com.hotel.reservation_service.dto;


public record RoomResponse(Long id,
                           String number,
                           RoomStatus status,
                           Long categoryId
) {


}