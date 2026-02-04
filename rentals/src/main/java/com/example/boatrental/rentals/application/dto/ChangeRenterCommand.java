package com.example.boatrental.rentals.application.dto;

import java.util.UUID;

public record ChangeRenterCommand(UUID rentalId, UUID newCustomerId) {
    public ChangeRenterCommand {
        if (rentalId == null) throw new IllegalArgumentException("rentalId cannot be null");
        if (newCustomerId == null) throw new IllegalArgumentException("newCustomerId cannot be null");
    }
}