package com.example.boatrental.rentals.infrastructure.api.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record StartRentalRequest(@NotNull(message = "boatId is required") UUID boatId,
                                 @NotNull(message = "customerId is required") UUID customerId) {
}
