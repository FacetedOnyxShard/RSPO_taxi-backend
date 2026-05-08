package ru.taxi.user_service.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)  // включает поля из AbstractUser
@NoArgsConstructor
public class Passenger extends AbstractUser {
}