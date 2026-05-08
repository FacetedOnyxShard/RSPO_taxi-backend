package ru.taxi.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.taxi.user_service.dto.PassengerListResponse;
import ru.taxi.user_service.dto.PassengerRegistrationRequest;
import ru.taxi.user_service.dto.PassengerResponse;
import ru.taxi.user_service.model.Passenger;
import ru.taxi.user_service.repository.PassengerRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PassengerService {

    private final PassengerRepository passengerRepository;
    private final PasswordEncoder passwordEncoder;

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
        passenger.setPassword(passwordEncoder.encode(request.getPassword()));

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

    public PassengerListResponse convertToListResponse(Passenger passenger) {
        return new PassengerListResponse(
                passenger.getId(),
                passenger.getName(),
                passenger.getEmail(),
                passenger.getPhone(),
                passenger.getCreatedAt().toString()
        );
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