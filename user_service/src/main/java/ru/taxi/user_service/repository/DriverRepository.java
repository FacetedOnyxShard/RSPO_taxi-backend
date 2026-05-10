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

import java.util.List;
import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, String> {
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    boolean existsByLicenseNumber(String licenseNumber);
    Optional<Driver> findByEmail(String email);
    List<Driver> findByStatus(DriverStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT d FROM Driver d WHERE d.status = :status ORDER BY d.id ASC FETCH FIRST 1 ROWS ONLY")
    Optional<Driver> findFirstFreeDriverForUpdate(@Param("status") DriverStatus status);
}