package ru.taxi.trip_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.taxi.trip_service.dto.TripCreateRequest;
import ru.taxi.trip_service.dto.TripCreateResponse;
import ru.taxi.trip_service.repository.TripRepository;

@Service
@RequiredArgsConstructor
public class TripService {
    private final TripRepository repository;

    public TripCreateResponse createTrip(TripCreateRequest request) {

    }
}
