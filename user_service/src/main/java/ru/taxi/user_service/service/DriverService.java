package ru.taxi.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.taxi.user_service.dto.CreateDriverRequest;
import ru.taxi.user_service.dto.GetDriverRequest;
import ru.taxi.user_service.model.Driver;
import ru.taxi.user_service.repository.DriverRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DriverService {
    private final DriverRepository repository;

    public Driver createDriver(CreateDriverRequest request) {
        Driver created = new Driver(
                UUID.randomUUID().toString(),
                request.name(),
                request.email(),
                request.phone(),
                request.license_number(),
                request.status(),
                LocalDateTime.now().toString()
        );
        repository.add(created);
        return created;
    }

    public Driver getDriver(GetDriverRequest request) {
        return repository.get(request.id());
    }

    public List<Driver> getAllDrivers() {
        return repository.getAll();
    }
}
