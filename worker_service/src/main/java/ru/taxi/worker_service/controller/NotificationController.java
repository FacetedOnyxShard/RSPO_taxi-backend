package ru.taxi.worker_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.taxi.worker_service.dto.NotificationRequest;
import ru.taxi.worker_service.dto.NotificationResponse;
import ru.taxi.worker_service.service.NotificationService;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService service;

    @PostMapping
    public ResponseEntity<NotificationResponse> addNotification(
            @Valid @RequestBody NotificationRequest request) {
        NotificationResponse response = service.createNotification(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getNotifications(
            @RequestParam("trip_id") String tripId) {
        List<NotificationResponse> list = service.getNotificationsByTrip(tripId);
        return ResponseEntity.ok(list);
    }
}