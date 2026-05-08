package ru.taxi.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.taxi.user_service.dto.DriverRegistrationRequest;
import ru.taxi.user_service.dto.DriverResponse;
import ru.taxi.user_service.dto.DriverStatusUpdateRequest;
import ru.taxi.user_service.model.Driver;
import ru.taxi.user_service.service.DriverService;

import java.util.List;

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

    @PostMapping("/assign")
    public ResponseEntity<DriverResponse> assignDriver() {
        DriverResponse response = driverService.assignFreeDriver();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DriverResponse> getDriver(@PathVariable String id) {
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