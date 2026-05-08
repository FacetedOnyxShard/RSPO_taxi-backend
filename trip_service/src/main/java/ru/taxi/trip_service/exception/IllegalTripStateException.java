package ru.taxi.trip_service.exception;

public class IllegalTripStateException extends RuntimeException {
    public IllegalTripStateException(String message) {
        super(message);
    }
}