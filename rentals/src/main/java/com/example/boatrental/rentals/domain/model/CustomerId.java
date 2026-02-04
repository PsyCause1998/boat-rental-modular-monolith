package com.example.boatrental.rentals.domain.model;

import java.util.UUID;

/**
 * Reference to a Customer, by ID only.
 */
public record CustomerId(UUID value) {

    public CustomerId {
        if (value == null) throw new IllegalArgumentException("CustomerRef cannot be null");
    }
}