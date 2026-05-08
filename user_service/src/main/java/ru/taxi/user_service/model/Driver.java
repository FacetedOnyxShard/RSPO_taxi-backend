package ru.taxi.user_service.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Driver extends AbstractUser {
    private String licenseNumber;
    private DriverStatus status;

    public Driver(String id, String name, String email, String phone,
                  String createdAt, String licenseNumber, DriverStatus status) {
        super(id, name, email, phone, createdAt);
        this.licenseNumber = licenseNumber;
        this.status = status;
    }
}