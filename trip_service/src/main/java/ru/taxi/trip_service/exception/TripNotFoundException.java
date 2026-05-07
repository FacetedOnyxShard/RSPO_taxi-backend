package ru.taxi.trip_service.exception;

public class TripNotFoundException extends RuntimeException {
    public TripNotFoundException(String id) {
        super("Trip not found: " + id);
    }
}