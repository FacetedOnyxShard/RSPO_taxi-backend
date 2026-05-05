package ru.taxi.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverStatusUpdateRequest {
    @NotBlank(message = "Status is required")
    @Pattern(regexp = "available|busy|offline", message = "Status must be: available, busy, or offline")
    private String status;
}
