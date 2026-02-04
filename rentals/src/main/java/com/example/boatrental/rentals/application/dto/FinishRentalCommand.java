package com.example.boatrental.rentals.application.dto;

import java.util.UUID;

public record FinishRentalCommand(UUID rentalId) {
    public FinishRentalCommand {
        if (rentalId == null) throw new IllegalArgumentException("rentalId cannot be null");
    }
}