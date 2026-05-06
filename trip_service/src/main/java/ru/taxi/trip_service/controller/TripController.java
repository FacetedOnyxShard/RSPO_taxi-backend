package ru.taxi.trip_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.taxi.trip_service.dto.TripCreateRequest;
import ru.taxi.trip_service.dto.TripCreateResponse;
import ru.taxi.trip_service.service.TripService;

@RestController
@RequestMapping("/trips")
@RequiredArgsConstructor
@Validated
public class TripController {
    private final TripService service;

    @PostMapping
    public ResponseEntity<TripCreateResponse> createTrip(@Valid @RequestBody TripCreateRequest request) {
        TripCreateResponse response = service.createTrip(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
