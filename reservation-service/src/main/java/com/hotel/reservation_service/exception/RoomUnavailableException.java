package com.hotel.reservation_service.exception;

public class RoomUnavailableException extends RuntimeException{

    public RoomUnavailableException(String message) {
        super(message);
    }

}
