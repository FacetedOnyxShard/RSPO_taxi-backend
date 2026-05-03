package ru.taxi.user_service.dto;

import ru.taxi.user_service.model.Passenger;

/**
 * DTO for {@link Passenger}
 */
public record GetDriverRequest(String id) {
}