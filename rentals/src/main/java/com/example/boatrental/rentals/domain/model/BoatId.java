package com.example.boatrental.rentals.domain.model;

import java.util.UUID;

/**
 * Reference to a Boat, by ID only.
 */
public record BoatId(UUID value) {

    public BoatId {
        if (value == null) throw new IllegalArgumentException("BoatRef cannot be null");
    }
}