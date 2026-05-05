package ru.taxi.user_service.repository;

import org.springframework.stereotype.Repository;
import ru.taxi.user_service.model.AbstractUser;
import ru.taxi.user_service.model.Driver;
import ru.taxi.user_service.model.Passenger;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class PassengerRepository {
    private final Map<String, Passenger> passengers = new ConcurrentHashMap<>();

    public Passenger save(Passenger passenger) {
        if (passenger.getId() == null) {
            passenger.setId(UUID.randomUUID().toString());
        }
        if (passenger.getCreatedAt() == null) {
            passenger.setCreatedAt(LocalDateTime.now().toString());
        }
        passengers.put(passenger.getId(), passenger);
        return passenger;
    }

    public Optional<Passenger> findById(String id) {
        return Optional.ofNullable(passengers.get(id));
    }

    public List<Passenger> findAll() {
        return new ArrayList<>(passengers.values());
    }

    public boolean existsByEmail(String email) {
        return passengers.values().stream()
                .anyMatch(p -> p.getEmail().equals(email));
    }

    public boolean existsByPhone(String phone) {
        return passengers.values().stream()
                .anyMatch(p -> p.getPhone().equals(phone));
    }

    public void deleteById(String id) {
        passengers.remove(id);
    }
}