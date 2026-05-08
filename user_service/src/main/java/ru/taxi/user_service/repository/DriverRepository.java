package ru.taxi.user_service.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import ru.taxi.user_service.model.Driver;
import ru.taxi.user_service.model.DriverStatus;

import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, String> {
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    boolean existsByLicenseNumber(String licenseNumber);
    Optional<Driver> findByEmail(String email);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT d FROM Driver d WHERE d.status = :status ORDER BY d.id ASC LIMIT 1")
    Optional<Driver> findFirstFreeDriverForUpdate(DriverStatus status);
}