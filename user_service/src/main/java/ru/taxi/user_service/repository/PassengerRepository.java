package ru.taxi.user_service.repository;

import org.springframework.stereotype.Repository;
import ru.taxi.user_service.model.Driver;
import ru.taxi.user_service.model.Passenger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class PassengerRepository {
    private final Map<String, Passenger> passengers = new HashMap<>();

    public void add(Passenger passenger) {
        passengers.put(passenger.id(), passenger);
    }

    public Passenger get(String id) {
        return passengers.get(id);
    }
}
