package com.example.boatrental.rentals.application.port.out;

import com.example.boatrental.rentals.domain.model.BoatId;

import java.util.UUID;

public interface BoatAvailabilityPort {
    boolean isAvailableForRental(UUID boatId);
}