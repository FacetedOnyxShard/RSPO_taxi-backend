package ru.taxi.trip_service.exception;

public class RatingOutOfRangeException extends RuntimeException {
    public RatingOutOfRangeException() {
        super("Rating must be between 1 and 5");
    }
}