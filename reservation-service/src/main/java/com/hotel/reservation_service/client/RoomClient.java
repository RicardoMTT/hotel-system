package com.hotel.reservation_service.client;


import com.hotel.reservation_service.dto.AvailabilityResponse;
import com.hotel.reservation_service.dto.RoomResponse;
import com.hotel.reservation_service.dto.UpdateRoomStatusRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = "room-service", url = "http://localhost:8083")
//@FeignClient(name = "room-service")
public interface RoomClient {

    @GetMapping("/rooms/{id}/availability")
    AvailabilityResponse checkAvailability(@PathVariable("id") Long roomId);

    @PutMapping("/rooms/{id}/status")
    RoomResponse updateRoomStatus(
            @PathVariable("id") Long roomId,
            @RequestBody UpdateRoomStatusRequest request
    );
}
