package ru.taxi.user_service.dto;

import jakarta.validation.constraints.Email;
import ru.taxi.user_service.model.Passenger;

/**
 * DTO for {@link Passenger}
 */
public record CreatePassengerResponse(String id, String name, @Email String email, String phone, String created_at) {
}