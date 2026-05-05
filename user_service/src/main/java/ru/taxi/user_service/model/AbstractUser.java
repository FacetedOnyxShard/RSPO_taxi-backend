package ru.taxi.user_service.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractUser {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String createdAt;
}