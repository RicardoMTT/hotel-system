package com.hotel.room_service.repository;

import com.hotel.room_service.domain.RoomCategory;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RoomCategoryRepository extends JpaRepository<RoomCategory, Long> {
}
