package ru.taxi.worker_service.repository;

import org.springframework.stereotype.Repository;
import ru.taxi.worker_service.model.NotificationStatus;
import ru.taxi.worker_service.model.NotificationTask;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class NotificationRepository {
    private final Map<String, NotificationTask> tasks = new ConcurrentHashMap<>();

    public NotificationTask save(NotificationTask task) {
        if (task.getId() == null) {
            task.setId(UUID.randomUUID().toString());
        }
        if (task.getCreatedAt() == null) {
            task.setCreatedAt(Instant.now().toString());
        }
        task.setUpdatedAt(Instant.now().toString());
        tasks.put(task.getId(), task);
        return task;
    }

    public Optional<NotificationTask> findById(String id) {
        return Optional.ofNullable(tasks.get(id));
    }

    public List<NotificationTask> findByTripId(String tripId) {
        return tasks.values().stream()
                .filter(t -> tripId.equals(t.getTripId()))
                .collect(Collectors.toList());
    }

    public Optional<NotificationTask> claimPendingTask() {
        synchronized (tasks) {
            for (NotificationTask task : tasks.values()) {
                if (task.getStatus() == NotificationStatus.PENDING) {
                    task.setStatus(NotificationStatus.PROCESSING);
                    task.setUpdatedAt(Instant.now().toString());
                    return Optional.of(task);
                }
            }
        }
        return Optional.empty();
    }

    public void updateStatus(String id, NotificationStatus status) {
        synchronized (tasks) {
            NotificationTask task = tasks.get(id);
            if (task != null) {
                task.setStatus(status);
                task.setUpdatedAt(Instant.now().toString());
            }
        }
    }

    public void incrementRetry(String id) {
        synchronized (tasks) {
            NotificationTask task = tasks.get(id);
            if (task != null) {
                task.setRetryCount(task.getRetryCount() + 1);
                task.setUpdatedAt(Instant.now().toString());
            }
        }
    }
}