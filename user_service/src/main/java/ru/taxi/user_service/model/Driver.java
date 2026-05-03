package ru.taxi.user_service.model;

public record Driver(String id, String name, String email, String phone, Long license_number, String status, String created_at) {
}
