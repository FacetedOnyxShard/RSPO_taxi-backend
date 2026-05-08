package ru.taxi.trip_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.taxi.trip_service.client.UserServiceClient;
import ru.taxi.trip_service.client.WorkerServiceClient;
import ru.taxi.trip_service.dto.DriverDto;
import ru.taxi.trip_service.dto.TripCreateRequest;
import ru.taxi.trip_service.dto.TripCreateResponse;
import ru.taxi.trip_service.dto.TripResponse;
import ru.taxi.trip_service.dto.TripStatusUpdateRequest;
import ru.taxi.trip_service.exception.NoAvailableDriverException;
import ru.taxi.trip_service.exception.TripNotFoundException;
import ru.taxi.trip_service.model.DriverStatus;
import ru.taxi.trip_service.model.Trip;
import ru.taxi.trip_service.model.TripStatus;
import ru.taxi.trip_service.repository.TripRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final UserServiceClient userServiceClient;
    private final WorkerServiceClient workerServiceClient;

    @Transactional
    public TripCreateResponse createTrip(TripCreateRequest request) {
        DriverDto driver;
        try {
            driver = userServiceClient.assignDriver();
        } catch (Exception e) {
            throw new NoAvailableDriverException();
        }

        Trip trip = new Trip();
        trip.setPassengerId(request.getPassenger_id());
        trip.setDriverId(driver.getId());
        trip.setOrigin(request.getOrigin());
        trip.setDestination(request.getDestination());
        trip.setStatus(TripStatus.CREATED);
        trip.setPrice(150);

        Trip saved = tripRepository.save(trip);

        workerServiceClient.sendNotification(
                saved.getId(),
                "Trip created. Driver assigned.",
                "TRIP_CREATED"
        );

        return mapToCreateResponse(saved);
    }

    public TripResponse getTrip(String id) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new TripNotFoundException(id));
        return mapToResponse(trip);
    }

    public List<TripResponse> getTripsByPassenger(String passengerId) {
        return tripRepository.findByPassengerId(passengerId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public TripResponse updateTripStatus(String id, TripStatusUpdateRequest statusRequest) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new TripNotFoundException(id));

        TripStatus oldStatus = trip.getStatus();
        TripStatus newStatus = TripStatus.valueOf(statusRequest.getStatus().toUpperCase());

        trip.setStatus(newStatus);
        Trip updated = tripRepository.save(trip);

        if (newStatus == TripStatus.COMPLETED) {
            userServiceClient.updateDriverStatus(trip.getDriverId(), DriverStatus.FREE);
        }

        String message = String.format("Trip status changed from %s to %s",
                oldStatus.name(), newStatus.name());
        workerServiceClient.sendNotification(updated.getId(), message, "STATUS_CHANGE");

        return mapToResponse(updated);
    }

    private TripResponse mapToResponse(Trip trip) {
        return new TripResponse(
                trip.getId(),
                trip.getPassengerId(),
                trip.getDriverId(),
                trip.getStatus().name(),
                trip.getOrigin(),
                trip.getDestination(),
                trip.getPrice(),
                trip.getCreatedAt().toString(),
                trip.getUpdatedAt().toString()
        );
    }

    private TripCreateResponse mapToCreateResponse(Trip trip) {
        return new TripCreateResponse(
                trip.getId(),
                trip.getPassengerId(),
                trip.getDriverId(),
                trip.getStatus().name(),
                trip.getOrigin(),
                trip.getDestination(),
                trip.getPrice(),
                trip.getCreatedAt().toString(),
                trip.getUpdatedAt().toString()
        );
    }
}