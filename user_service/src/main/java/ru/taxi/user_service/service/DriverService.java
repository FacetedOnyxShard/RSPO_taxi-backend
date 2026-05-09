package ru.taxi.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.taxi.user_service.dto.DriverListResponse;
import ru.taxi.user_service.dto.DriverRegistrationRequest;
import ru.taxi.user_service.dto.DriverResponse;
import ru.taxi.user_service.model.Driver;
import ru.taxi.user_service.model.DriverStatus;
import ru.taxi.user_service.repository.DriverRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DriverService {

    private final DriverRepository driverRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
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
        driver.setPassword(passwordEncoder.encode(request.getPassword()));

        Driver saved = driverRepository.save(driver);
        return convertToResponse(saved);
    }

    public DriverResponse getDriver(String id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + id));
        return convertToResponse(driver);
    }

    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    public DriverListResponse convertToListResponse(Driver driver) {
        return new DriverListResponse(
                driver.getId(),
                driver.getName(),
                driver.getEmail(),
                driver.getPhone(),
                driver.getCreatedAt().toString(),
                driver.getLicenseNumber(),
                driver.getStatus()
        );
    }

    @Transactional
    public DriverResponse updateDriverStatus(String id, DriverStatus status) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + id));
        driver.setStatus(status);
        Driver updated = driverRepository.save(driver);
        return convertToResponse(updated);
    }

    @Transactional
    public DriverResponse assignFreeDriver() {
        log.info("All drivers: {}", driverRepository.findAll());
        Driver driver = driverRepository.findFirstFreeDriverForUpdate(DriverStatus.FREE)
                .orElseThrow(() -> new RuntimeException("No free drivers available"));
        driver.setStatus(DriverStatus.BUSY);
        Driver updated = driverRepository.save(driver);
        return convertToResponse(updated);
    }

    private DriverResponse convertToResponse(Driver driver) {
        return new DriverResponse(
                driver.getId(),
                driver.getName(),
                driver.getEmail(),
                driver.getPhone(),
                driver.getCreatedAt().toString(),
                driver.getLicenseNumber(),
                driver.getStatus()
        );
    }
}