package ru.taxi.user_service.repository;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import ru.taxi.user_service.model.Driver;
import ru.taxi.user_service.model.DriverStatus;

import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, String> {
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    boolean existsByLicenseNumber(String licenseNumber);
    Optional<Driver> findByEmail(String email);

    @Query(value = "SELECT * FROM drivers WHERE status = :status ORDER BY id ASC LIMIT 1 FOR UPDATE SKIP LOCKED", nativeQuery = true)
    Optional<Driver> findFirstFreeDriverForUpdate(@Param("status") String status);
}