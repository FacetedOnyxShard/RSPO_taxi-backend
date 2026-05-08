package ru.taxi.trip_service.repository;

import org.springframework.stereotype.Repository;
import ru.taxi.trip_service.model.Trip;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class TripRepository {
    private final Map<String, Trip> trips = new ConcurrentHashMap<>();

    public Trip save(Trip trip) {
        trips.put(trip.getId(), trip);
        return trip;
    }

    public Optional<Trip> findById(String id) {
        return Optional.ofNullable(trips.get(id));
    }

    public List<Trip> findAll() {
        return new ArrayList<>(trips.values());
    }

    public List<Trip> findByPassengerId(String passengerId) {
        return trips.values().stream()
                .filter(trip -> passengerId.equals(trip.getPassenger_id()))
                .collect(Collectors.toList());
    }
}