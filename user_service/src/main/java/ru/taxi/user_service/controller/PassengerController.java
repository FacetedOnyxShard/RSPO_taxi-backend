package ru.taxi.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.taxi.user_service.dto.PassengerRegistrationRequest;
import ru.taxi.user_service.dto.PassengerResponse;
import ru.taxi.user_service.model.Passenger;
import ru.taxi.user_service.service.PassengerService;

import java.util.List;

@RestController
@RequestMapping("/api/passengers")
@RequiredArgsConstructor
@Validated
public class PassengerController {

    private final PassengerService passengerService;

    @GetMapping
    public List<Passenger> getAll() {
        return passengerService.getAllPassengers();
    }

    @PostMapping
    public ResponseEntity<PassengerResponse> registerPassenger(
            @Valid @RequestBody PassengerRegistrationRequest request) {
        PassengerResponse response = passengerService.registerPassenger(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PassengerResponse> getPassenger(@PathVariable String id) {
        PassengerResponse response = passengerService.getPassenger(id);
        return ResponseEntity.ok(response);
    }
}