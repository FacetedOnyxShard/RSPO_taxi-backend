package ru.taxi.trip_service.repository;

import org.springframework.stereotype.Repository;
import ru.taxi.trip_service.model.Trip;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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
}
