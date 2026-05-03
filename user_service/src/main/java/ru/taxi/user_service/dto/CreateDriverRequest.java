package ru.taxi.user_service.dto;

import jakarta.validation.constraints.Email;
import ru.taxi.user_service.model.Passenger;

/**
 * DTO for {@link Passenger}
 */
public record CreateDriverRequest(String name, String email, String phone, Long license_number, String status) {
}