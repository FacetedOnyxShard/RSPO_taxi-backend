package ru.taxi.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.taxi.user_service.model.Passenger;

public interface PassengerRepository extends JpaRepository<Passenger, String> {
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
}