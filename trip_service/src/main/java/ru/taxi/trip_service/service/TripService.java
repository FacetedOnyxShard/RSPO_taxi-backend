package ru.taxi.trip_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.taxi.trip_service.client.UserServiceClient;
import ru.taxi.trip_service.dto.*;
import ru.taxi.trip_service.exception.NoAvailableDriverException;
import ru.taxi.trip_service.exception.TripNotFoundException;
import ru.taxi.trip_service.model.DriverStatus;
import ru.taxi.trip_service.model.Trip;
import ru.taxi.trip_service.model.TripStatus;
import ru.taxi.trip_service.repository.TripRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository repository;
    private final UserServiceClient userServiceClient;

    private final ReentrantLock assignmentLock = new ReentrantLock();

    public TripCreateResponse createTrip(TripCreateRequest request) {
        Trip trip;
        assignmentLock.lock();
        try {
            List<DriverDto> freeDrivers = userServiceClient.getAllDrivers().stream()
                    .filter(d -> d.getStatus().isAvailable())
                    .toList();

            if (freeDrivers.isEmpty()) {
                throw new NoAvailableDriverException();
            }

            DriverDto driver = freeDrivers.getFirst();
            userServiceClient.updateDriverStatus(driver.getId(), DriverStatus.BUSY);

            trip = new Trip();
            trip.setId(UUID.randomUUID().toString());
            trip.setPassenger_id(request.getPassenger_id());
            trip.setDriver_id(driver.getId());
            trip.setOrigin(request.getOrigin());
            trip.setDestination(request.getDestination());
            trip.setStatus(TripStatus.CREATED.name());
            trip.setPrice(calculatePrice());
            trip.setCreated_at(Instant.now().toString());
            trip.setUpdated_at(trip.getCreated_at());

            repository.save(trip);
        } finally {
            assignmentLock.unlock();
        }

        return mapToCreateResponse(trip);
    }

    public TripResponse getTrip(String id) {
        Trip trip = repository.findById(id)
                .orElseThrow(() -> new TripNotFoundException(id));
        return mapToResponse(trip);
    }

    public List<TripResponse> getTripsByPassenger(String passengerId) {
        return repository.findByPassengerId(passengerId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public TripResponse updateTripStatus(String id, TripStatusUpdateRequest statusRequest) {
        Trip trip = repository.findById(id)
                .orElseThrow(() -> new TripNotFoundException(id));

        TripStatus newStatus = TripStatus.valueOf(statusRequest.getStatus().toUpperCase());
        trip.setStatus(newStatus.name());
        trip.setUpdated_at(Instant.now().toString());
        repository.save(trip);

        if (newStatus == TripStatus.COMPLETED) {
            userServiceClient.updateDriverStatus(trip.getDriver_id(), DriverStatus.FREE);
        }

        return mapToResponse(trip);
    }

    private TripResponse mapToResponse(Trip trip) {
        return new TripResponse(
                trip.getId(),
                trip.getPassenger_id(),
                trip.getDriver_id(),
                trip.getStatus(),
                trip.getOrigin(),
                trip.getDestination(),
                trip.getPrice(),
                trip.getCreated_at(),
                trip.getUpdated_at()
        );
    }

    private TripCreateResponse mapToCreateResponse(Trip trip) {
        return new TripCreateResponse(
                trip.getId(),
                trip.getPassenger_id(),
                trip.getDriver_id(),
                trip.getStatus(),
                trip.getOrigin(),
                trip.getDestination(),
                trip.getPrice(),
                trip.getCreated_at(),
                trip.getUpdated_at()
        );
    }

    private int calculatePrice() {
        return 150;
    }
}