package ru.taxi.trip_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class DriverDto extends AbstractUsersDto {
    private String licenseNumber;
    private String status;

    public DriverDto(String id, String name, String email, String phone,
                     String createdAt, String licenseNumber, String status) {
        super(id, name, email, phone, createdAt);
        this.licenseNumber = licenseNumber;
        this.status = status;
    }
}