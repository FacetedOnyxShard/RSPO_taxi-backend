package ru.taxi.trip_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripResponse {
    private String id;
    private String passenger_id;
    private String driver_id;
    private String status;
    private String origin;
    private String destination;
    private int price;
    private String created_at;
    private String updated_at;
}
