package com.hotel.reservation_service.exception;

public class GuestNotFoundException extends RuntimeException {

    public GuestNotFoundException(String message) {
        super(message);
    }

}
