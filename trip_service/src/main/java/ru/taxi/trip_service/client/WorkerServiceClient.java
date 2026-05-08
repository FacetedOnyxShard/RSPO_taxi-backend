package ru.taxi.trip_service.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.taxi.worker_service.dto.NotificationRequest;
@Component
@RequiredArgsConstructor
public class WorkerServiceClient {
    private final RestTemplate restTemplate;

    @Value("${worker-service.url}")
    private String workerServiceUrl;

    public void sendNotification(String tripId, String message, String type) {
        NotificationRequest request = NotificationRequest.builder()
                .tripId(tripId)
                .message(message)
                .type(type)
                .build();
        restTemplate.postForObject(workerServiceUrl + "/notifications", request, Void.class);
    }
}