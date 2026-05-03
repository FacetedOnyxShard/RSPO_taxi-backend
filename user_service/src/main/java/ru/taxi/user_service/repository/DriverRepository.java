package ru.taxi.user_service.repository;

import org.springframework.stereotype.Repository;
import ru.taxi.user_service.model.Driver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class DriverRepository {
    private final Map<String, Driver> drivers = new HashMap<>();

    public void add(Driver driver) {
        drivers.put(driver.id(), driver);
    }

    public Driver get(String id) {
        return drivers.get(id);
    }

    public List<Driver> getAll() {
        return new ArrayList<>(drivers.values());
    }
}
