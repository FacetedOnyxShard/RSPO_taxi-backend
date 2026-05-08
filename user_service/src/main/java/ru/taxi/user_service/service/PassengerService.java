package ru.taxi.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.taxi.user_service.dto.PassengerRegistrationRequest;
import ru.taxi.user_service.dto.PassengerResponse;
import ru.taxi.user_service.model.Passenger;
import ru.taxi.user_service.repository.PassengerRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PassengerService {

    private final PassengerRepository passengerRepository;

    @Transactional
    public PassengerResponse registerPassenger(PassengerRegistrationRequest request) {
        if (passengerRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        if (passengerRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Phone number already registered");
        }

        Passenger passenger = new Passenger();
        passenger.setName(request.getName());
        passenger.setEmail(request.getEmail());
        passenger.setPhone(request.getPhone());

        Passenger saved = passengerRepository.save(passenger);
        return convertToResponse(saved);
    }

    public PassengerResponse getPassenger(String id) {
        Passenger passenger = passengerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Passenger not found with id: " + id));
        return convertToResponse(passenger);
    }

    public List<Passenger> getAllPassengers() {
        return passengerRepository.findAll();
    }

    private PassengerResponse convertToResponse(Passenger passenger) {
        return new PassengerResponse(
                passenger.getId(),
                passenger.getName(),
                passenger.getEmail(),
                passenger.getPhone(),
                passenger.getCreatedAt().toString()
        );
    }
}