package ru.taxi.trip_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.taxi.trip_service.model.Trip;

import java.time.Instant;
import java.util.List;

public interface TripRepository extends JpaRepository<Trip, String> {
    List<Trip> findByPassengerId(String passengerId);

    @Query("SELECT COUNT(t), COALESCE(AVG(t.price), 0) FROM Trip t WHERE t.createdAt >= :start AND t.createdAt < :end")
    List<Object[]> getCountAndAvgPriceBetween(@Param("start") Instant start, @Param("end") Instant end);
}