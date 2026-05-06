package ru.taxi.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.taxi.user_service.dto.DriverRegistrationRequest;
import ru.taxi.user_service.dto.DriverResponse;
import ru.taxi.user_service.dto.DriverStatusUpdateRequest;
import ru.taxi.user_service.model.Driver;
import ru.taxi.user_service.model.Passenger;
import ru.taxi.user_service.service.DriverService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
@Validated
public class DriverController {
    private final DriverService driverService;

    @GetMapping
    public List<Driver> getAll() {
        return driverService.getAllDrivers();
    }

    @PostMapping
    public ResponseEntity<DriverResponse> registerDriver(
            @Valid @RequestBody DriverRegistrationRequest request) {
        DriverResponse response = driverService.registerDriver(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DriverResponse> getDriver(
            @PathVariable String id) {
        DriverResponse response = driverService.getDriver(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<DriverResponse> updateDriverStatus(
            @PathVariable String id,
            @Valid @RequestBody DriverStatusUpdateRequest request) {
        DriverResponse response = driverService.updateDriverStatus(id, request.getStatus());
        return ResponseEntity.ok(response);
    }
}