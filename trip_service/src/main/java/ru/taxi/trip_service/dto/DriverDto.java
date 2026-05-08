package ru.taxi.trip_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.taxi.trip_service.model.DriverStatus;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class DriverDto extends AbstractUsersDto{
    private String licenseNumber;
    private DriverStatus status;

    public DriverDto(String id, String name, String email, String phone,
                  String createdAt, String licenseNumber, DriverStatus status) {
        super(id, name, email, phone, createdAt);
        this.licenseNumber = licenseNumber;
        this.status = status;
    }
}