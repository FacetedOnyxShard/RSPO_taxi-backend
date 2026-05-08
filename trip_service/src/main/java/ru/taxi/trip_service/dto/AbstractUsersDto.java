package ru.taxi.trip_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractUsersDto {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String createdAt;
}
