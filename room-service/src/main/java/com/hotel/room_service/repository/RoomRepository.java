package com.hotel.room_service.repository;

import com.hotel.room_service.domain.Room;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RoomRepository extends JpaRepository<Room, Long> {
}
