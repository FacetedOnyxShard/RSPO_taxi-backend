package ru.taxi.worker_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {
    private String id;
    private String tripId;
    private String message;
    private String status;
    private int retryCount;
    private String createdAt;
    private String updatedAt;
}