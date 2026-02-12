package com.hotel.room_service.dto;

import java.math.BigDecimal;

public record CreateRoomCategoryRequest(
        String name,
        String description,
        BigDecimal basePrice,
        int maxGuests
) {
}
