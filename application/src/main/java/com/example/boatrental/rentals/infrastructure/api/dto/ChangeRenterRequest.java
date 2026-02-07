package com.example.boatrental.rentals.infrastructure.api.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ChangeRenterRequest(@NotNull(message = "newCustomerId is required") UUID newCustomerId) {
}
