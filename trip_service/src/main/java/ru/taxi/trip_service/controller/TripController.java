package ru.taxi.trip_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.taxi.trip_service.dto.*;
import ru.taxi.trip_service.exception.NoAvailableDriverException;
import ru.taxi.trip_service.exception.TripNotFoundException;
import ru.taxi.trip_service.service.TripService;

import java.util.List;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
@Validated
public class TripController {

    private final TripService service;

    @PostMapping
    public ResponseEntity<TripCreateResponse> createTrip(@Valid @RequestBody TripCreateRequest request) {
        TripCreateResponse response = service.createTrip(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TripResponse> getTrip(@PathVariable String id) {
        TripResponse response = service.getTrip(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<TripResponse>> getTripsByPassenger(
            @RequestParam("passenger_id") String passengerId) {
        List<TripResponse> trips = service.getTripsByPassenger(passengerId);
        return ResponseEntity.ok(trips);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TripResponse> updateTripStatus(
            @PathVariable String id,
            @Valid @RequestBody TripStatusUpdateRequest statusRequest) {
        TripResponse response = service.updateTripStatus(id, statusRequest);
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(TripNotFoundException.class)
    public ResponseEntity<String> handleNotFound(TripNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(NoAvailableDriverException.class)
    public ResponseEntity<String> handleNoDriver(NoAvailableDriverException e) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleBadRequest(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}