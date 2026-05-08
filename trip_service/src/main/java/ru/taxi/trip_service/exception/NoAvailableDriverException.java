package ru.taxi.trip_service.exception;

public class NoAvailableDriverException extends RuntimeException {
    public NoAvailableDriverException() {
        super("No free drivers available at the moment");
    }
}