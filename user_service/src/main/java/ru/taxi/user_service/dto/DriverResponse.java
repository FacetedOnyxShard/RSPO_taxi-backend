package ru.taxi.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.taxi.user_service.model.DriverStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverResponse {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String createdAt;
    private String licenseNumber;
    private DriverStatus status;
}