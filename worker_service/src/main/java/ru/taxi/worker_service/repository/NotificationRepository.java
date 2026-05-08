package ru.taxi.worker_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.taxi.worker_service.model.NotificationTask;
import ru.taxi.worker_service.model.NotificationStatus;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<NotificationTask, String> {

    List<NotificationTask> findByTripId(String tripId);

    @Query(value = "SELECT * FROM notification_tasks WHERE status = 'PENDING' ORDER BY created_at ASC LIMIT 1 FOR UPDATE SKIP LOCKED", nativeQuery = true)
    Optional<NotificationTask> findPendingTaskForUpdate();
}