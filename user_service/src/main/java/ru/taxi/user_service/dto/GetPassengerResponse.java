package ru.taxi.user_service.dto;

import jakarta.validation.constraints.Email;
import ru.taxi.user_service.model.Passenger;

/**
 * DTO for {@link Passenger}
 */
public record GetPassengerResponse(String name, @Email String email, String phone, String created_at) {
}