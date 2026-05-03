package ru.taxi.user_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.taxi.user_service.dto.CreateDriverRequest;
import ru.taxi.user_service.dto.CreateDriverResponse;
import ru.taxi.user_service.dto.GetDriverRequest;
import ru.taxi.user_service.dto.GetDriverResponse;
import ru.taxi.user_service.model.Driver;
import ru.taxi.user_service.service.DriverService;
import ru.taxi.user_service.service.DriverService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {
    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @PostMapping
    public ResponseEntity<CreateDriverResponse> createDriver(@RequestBody CreateDriverRequest request) {
        Driver created = driverService.createDriver(request);

        CreateDriverResponse response = new CreateDriverResponse(
                created.id(),
                created.name(),
                created.email(),
                created.phone(),
                created.license_number(),
                created.status(),
                created.created_at()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("{id}")
    public ResponseEntity<GetDriverResponse> getDriver(@PathVariable String id) {
        Driver driver = driverService.getDriver(new GetDriverRequest(id));
        GetDriverResponse response = new GetDriverResponse(
                driver.name(),
                driver.email(),
                driver.phone(),
                driver.license_number(),
                driver.status(),
                driver.created_at()
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping
    public ResponseEntity<List<GetDriverResponse>> getAllDrivers() {
        List<Driver> drivers = driverService.getAllDrivers();
        List<GetDriverResponse> responses = drivers.stream()
                .map(driver -> new GetDriverResponse(
                        driver.name(),
                        driver.email(),
                        driver.phone(),
                        driver.license_number(),
                        driver.status(),
                        driver.created_at()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }
}
