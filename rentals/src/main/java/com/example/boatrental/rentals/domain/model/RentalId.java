package com.example.boatrental.rentals.domain.model;

import java.util.UUID;

public record RentalId(UUID value) {
    public RentalId {
        if (value == null) {
            throw new IllegalArgumentException("RentalId cannot be null");
        }
    }

    public static RentalId newId() {
        return new RentalId(UUID.randomUUID());
    }
}
