package ru.taxi.trip_service.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.taxi.trip_service.dto.DriverDto;
import ru.taxi.trip_service.dto.PassengerDto;
import ru.taxi.trip_service.model.DriverStatus;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserServiceClient {

    private final RestTemplate restTemplate;

    @Value("${user-service.url}")
    private String userServiceUrl;

    public DriverDto assignDriver() {
        return restTemplate.postForObject(
                userServiceUrl + "/api/drivers/assign",
                null,
                DriverDto.class
        );
    }

    public List<DriverDto> getAllDrivers() {
        ResponseEntity<List<DriverDto>> response = restTemplate.exchange(
                userServiceUrl + "/api/drivers",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        return response.getBody() != null ? response.getBody() : Collections.emptyList();
    }

    public Optional<DriverDto> getDriver(String driverId) {
        try {
            DriverDto driver = restTemplate.getForObject(
                    userServiceUrl + "/api/drivers/{id}",
                    DriverDto.class,
                    driverId);
            return Optional.ofNullable(driver);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public void updateDriverStatus(String driverId, DriverStatus newStatus) {
        Map<String, String> body = Map.of("status", newStatus.name());
        restTemplate.patchForObject(
                userServiceUrl + "/api/drivers/{id}/status",
                body,
                Void.class,
                driverId);
    }

    public List<PassengerDto> getAllPassengers() {
        log.info("Sending request to: {}/api/passengers", userServiceUrl);
        try {
            ResponseEntity<List<PassengerDto>> response = restTemplate.exchange(
                    userServiceUrl + "/api/passengers",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<PassengerDto>>() {}
            );
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody() != null ? response.getBody() : Collections.emptyList();
            } else {
                log.error("Error: status {}", response.getStatusCode());
                return Collections.emptyList();
            }
        } catch (RestClientException e) {
            log.error("Failed to get passengers: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    public Optional<PassengerDto> getPassenger(String passengerId) {
        try {
            PassengerDto passenger = restTemplate.getForObject(
                    userServiceUrl + "/api/passengers/{id}",
                    PassengerDto.class,
                    passengerId);
            return Optional.ofNullable(passenger);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}