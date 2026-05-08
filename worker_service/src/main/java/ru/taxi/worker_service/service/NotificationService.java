package ru.taxi.worker_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.taxi.worker_service.dto.NotificationRequest;
import ru.taxi.worker_service.dto.NotificationResponse;
import ru.taxi.worker_service.model.NotificationStatus;
import ru.taxi.worker_service.model.NotificationTask;
import ru.taxi.worker_service.repository.NotificationRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository repository;

    @Transactional
    public NotificationResponse createNotification(NotificationRequest request) {
        NotificationTask task = NotificationTask.builder()
                .tripId(request.getTripId())
                .message(request.getMessage())
                .type(request.getType())
                .status(NotificationStatus.PENDING)
                .retryCount(0)
                .build();
        task = repository.save(task);
        return mapToResponse(task);
    }

    public List<NotificationResponse> getNotificationsByTrip(String tripId) {
        return repository.findByTripId(tripId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void processNextTask() {
        Optional<NotificationTask> taskOpt = repository.findPendingTaskForUpdate();
        if (taskOpt.isEmpty()) {
            return;
        }
        NotificationTask task = taskOpt.get();
        task.setStatus(NotificationStatus.PROCESSING);
        repository.save(task);

        try {
            Thread.sleep(500);
            task.setStatus(NotificationStatus.SENT);
            log.info("Notification sent: id={}", task.getId());
        } catch (Exception e) {
            log.error("Failed to send notification id={}, retryCount={}", task.getId(), task.getRetryCount(), e);
            task.setRetryCount(task.getRetryCount() + 1);
            if (task.getRetryCount() >= 3) {
                task.setStatus(NotificationStatus.FAILED);
            } else {
                task.setStatus(NotificationStatus.PENDING);
            }
        }
        repository.save(task);
    }

    private NotificationResponse mapToResponse(NotificationTask task) {
        return NotificationResponse.builder()
                .id(task.getId())
                .tripId(task.getTripId())
                .message(task.getMessage())
                .status(task.getStatus().name())
                .retryCount(task.getRetryCount())
                .createdAt(task.getCreatedAt().toString())
                .updatedAt(task.getUpdatedAt().toString())
                .build();
    }
}