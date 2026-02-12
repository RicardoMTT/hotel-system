package com.hotel.room_service.dto;

import java.math.BigDecimal;

public record RoomCategoryResponse(Long id,
                                   String name,
                                   BigDecimal basePrice,
                                   int maxGuests) {
}
