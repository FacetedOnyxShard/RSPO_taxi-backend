package ru.taxi.user_service.repository;

import org.springframework.stereotype.Repository;
import ru.taxi.user_service.model.Driver;
import ru.taxi.user_service.model.DriverStatus;
import ru.taxi.user_service.model.Passenger;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class DriverRepository {
    private final Map<String, Driver> drivers = new ConcurrentHashMap<>();

    public Driver save(Driver driver) {
        if (driver.getId() == null) {
            driver.setId(UUID.randomUUID().toString());
        }
        if (driver.getCreatedAt() == null) {
            driver.setCreatedAt(LocalDateTime.now().toString());
        }
        drivers.put(driver.getId(), driver);
        return driver;
    }

    public Optional<Driver> findById(String id) {
        return Optional.ofNullable(drivers.get(id));
    }

    public List<Driver> findAll() {
        return new ArrayList<>(drivers.values());
    }

    public List<Driver> findByStatus(String status) {
        return drivers.values().stream()
                .filter(d -> d.getStatus().equals(status))
                .collect(Collectors.toList());
    }

    public boolean existsByEmail(String email) {
        return drivers.values().stream()
                .anyMatch(d -> d.getEmail().equals(email));
    }

    public boolean existsByPhone(String phone) {
        return drivers.values().stream()
                .anyMatch(d -> d.getPhone().equals(phone));
    }

    public boolean existsByLicenseNumber(String licenseNumber) {
        return drivers.values().stream()
                .anyMatch(d -> d.getLicenseNumber().equals(licenseNumber));
    }

    public void updateStatus(String id, DriverStatus status) {
        findById(id).ifPresent(driver -> driver.setStatus(status));
    }
}