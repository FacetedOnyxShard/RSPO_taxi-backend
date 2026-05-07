package ru.taxi.worker_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.taxi.worker_service.dto.NotificationRequest;
import ru.taxi.worker_service.dto.NotificationResponse;
import ru.taxi.worker_service.model.NotificationStatus;
import ru.taxi.worker_service.model.NotificationTask;
import ru.taxi.worker_service.repository.NotificationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository repository;

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

    private NotificationResponse mapToResponse(NotificationTask task) {
        return NotificationResponse.builder()
                .id(task.getId())
                .tripId(task.getTripId())
                .message(task.getMessage())
                .status(task.getStatus().name())
                .retryCount(task.getRetryCount())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}