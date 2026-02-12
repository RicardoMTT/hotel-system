package com.hotel.reservation_service.client;

import com.hotel.reservation_service.dto.GuestResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "guest-service", url = "http://localhost:8082")
//@FeignClient(name = "guest-service")
public interface GuestClient {
    @GetMapping("/guests/{id}")
    GuestResponse getGuestById(@PathVariable("id") Long guestId);
}
