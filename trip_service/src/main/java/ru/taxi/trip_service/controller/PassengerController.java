package ru.taxi.trip_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.taxi.trip_service.client.UserServiceClient;
import ru.taxi.trip_service.dto.*;
import ru.taxi.trip_service.exception.NoAvailableDriverException;
import ru.taxi.trip_service.exception.TripNotFoundException;
import ru.taxi.trip_service.service.TripService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/passengers")
@RequiredArgsConstructor
@Validated
public class PassengerController {
    private final UserServiceClient client;

    @GetMapping
    public List<PassengerDto> getAll() {
        return client.getAllPassengers();
    }

    @GetMapping("/{id}")
    public PassengerDto getPassenger(@PathVariable String id) {
        return client.getPassenger(id).orElseThrow(() -> new RuntimeException("Passenger not found"));
    }
}