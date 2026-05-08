package ru.taxi.trip_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.taxi.trip_service.model.Trip;

import java.util.List;

public interface TripRepository extends JpaRepository<Trip, String> {
    List<Trip> findByPassengerId(String passengerId);
}