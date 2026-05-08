package ru.taxi.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.taxi.user_service.model.DriverStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverStatusUpdateRequest {
    @NotNull(message = "Status is required")
    private DriverStatus status;
}
