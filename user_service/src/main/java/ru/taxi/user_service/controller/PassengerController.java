package ru.taxi.user_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.taxi.user_service.dto.CreatePassengerRequest;
import ru.taxi.user_service.dto.CreatePassengerResponse;
import ru.taxi.user_service.dto.GetPassengerRequest;
import ru.taxi.user_service.dto.GetPassengerResponse;
import ru.taxi.user_service.model.Passenger;
import ru.taxi.user_service.service.PassengerService;

@RestController
@RequestMapping("/api/passengers")
public class PassengerController {
    private final PassengerService passengerService;

    public PassengerController(PassengerService passengerService) {
        this.passengerService = passengerService;
    }

    @PostMapping
    public ResponseEntity<CreatePassengerResponse> createPassenger(@RequestBody CreatePassengerRequest request) {
        Passenger created = passengerService.createPassenger(request);

        CreatePassengerResponse response = new CreatePassengerResponse(
                created.id(),
                created.name(),
                created.email(),
                created.phone(),
                created.created_at()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("{id}")
    public ResponseEntity<GetPassengerResponse> getPassenger(@PathVariable String id) {
        Passenger passenger = passengerService.getPassenger(new GetPassengerRequest(id));
        GetPassengerResponse response = new GetPassengerResponse(
                passenger.name(),
                passenger.email(),
                passenger.phone(),
                passenger.created_at()
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
