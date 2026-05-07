package ru.taxi.trip_service.model;

import lombok.Getter;

@Getter
public enum DriverStatus {
    FREE("свободен"),
    BUSY("занят"),
    OFFLINE("не в сети");

    private final String description;

    DriverStatus(String description) {
        this.description = description;
    }

    public boolean isAvailable() {
        return this == FREE;
    }
}