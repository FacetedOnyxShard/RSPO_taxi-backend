package ru.taxi.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.taxi.user_service.dto.CreatePassengerRequest;
import ru.taxi.user_service.dto.GetPassengerRequest;
import ru.taxi.user_service.model.Passenger;
import ru.taxi.user_service.repository.PassengerRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PassengerService {
    private final PassengerRepository repository;

    public Passenger createPassenger(CreatePassengerRequest request) {
        Passenger created = new Passenger(
                UUID.randomUUID().toString(),
                request.name(),
                request.email(),
                request.phone(),
                LocalDateTime.now().toString()
        );
        repository.add(created);
        return created;
    }

    public Passenger getPassenger(GetPassengerRequest request) {
        return repository.get(request.id());
    }
}
