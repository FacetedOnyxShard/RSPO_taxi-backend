package ru.taxi.worker_service.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationTask {
    private String id;
    private String tripId;
    private String message;
    private String type;
    private NotificationStatus status;
    private int retryCount;
    private String createdAt;
    private String updatedAt;
}