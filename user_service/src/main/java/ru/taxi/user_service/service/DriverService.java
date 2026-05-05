package ru.taxi.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.taxi.user_service.dto.DriverRegistrationRequest;
import ru.taxi.user_service.dto.DriverResponse;
import ru.taxi.user_service.dto.PassengerRegistrationRequest;
import ru.taxi.user_service.dto.PassengerResponse;
import ru.taxi.user_service.model.Driver;
import ru.taxi.user_service.model.Passenger;
import ru.taxi.user_service.repository.DriverRepository;
import ru.taxi.user_service.repository.PassengerRepository;

@Service
@RequiredArgsConstructor
public class DriverService {
    private final DriverRepository driverRepository;

    public DriverResponse registerDriver(DriverRegistrationRequest request) {
        if (driverRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        if (driverRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Phone number already registered");
        }
        if (driverRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw new RuntimeException("License number already registered");
        }

        Driver driver = new Driver();
        driver.setName(request.getName());
        driver.setEmail(request.getEmail());
        driver.setPhone(request.getPhone());
        driver.setLicenseNumber(request.getLicenseNumber());
        driver.setStatus(request.getStatus());

        Driver saved = driverRepository.save(driver);
        return convertToResponse(saved);
    }

    public DriverResponse getDriver(String id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + id));
        return convertToResponse(driver);
    }

    public DriverResponse updateDriverStatus(String id, String status) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + id));

        driver.setStatus(status);
        Driver updated = driverRepository.save(driver);
        return convertToResponse(updated);
    }

    private DriverResponse convertToResponse(Driver driver) {
        return new DriverResponse(
                driver.getId(),
                driver.getName(),
                driver.getEmail(),
                driver.getPhone(),
                driver.getCreatedAt(),
                driver.getLicenseNumber(),
                driver.getStatus()
        );
    }
}