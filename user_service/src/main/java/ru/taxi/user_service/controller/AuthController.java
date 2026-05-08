package ru.taxi.user_service.controller;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.taxi.user_service.model.Driver;
import ru.taxi.user_service.model.Passenger;
import ru.taxi.user_service.repository.DriverRepository;
import ru.taxi.user_service.repository.PassengerRepository;
import ru.taxi.user_service.util.JwtUtil;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final PassengerRepository passengerRepository;
    private final DriverRepository driverRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        String role = request.getRole();
        String email = request.getEmail();
        String password = request.getPassword();

        if ("passenger".equalsIgnoreCase(role)) {
            Optional<Passenger> passengerOpt = passengerRepository.findByEmail(email);
            if (passengerOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }
            Passenger passenger = passengerOpt.get();
            if (!passwordEncoder.matches(password, passenger.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }
            String token = jwtUtil.generateToken(passenger.getId(), "PASSENGER");
            return ResponseEntity.ok(new LoginResponse(token));
        } else if ("driver".equalsIgnoreCase(role)) {
            Optional<Driver> driverOpt = driverRepository.findByEmail(email);
            if (driverOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }
            Driver driver = driverOpt.get();
            if (!passwordEncoder.matches(password, driver.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }
            String token = jwtUtil.generateToken(driver.getId(), "DRIVER");
            return ResponseEntity.ok(new LoginResponse(token));
        } else {
            return ResponseEntity.badRequest().body("Invalid role");
        }
    }

    @Data
    public static class LoginRequest {
        @NotBlank
        private String email;
        @NotBlank
        private String password;
        @NotBlank
        private String role;
    }

    @Data
    @AllArgsConstructor
    public static class LoginResponse {
        private String token;
    }
}