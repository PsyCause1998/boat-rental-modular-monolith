package com.example.boatrental.rentals.application.port.out;

import java.util.UUID;

public interface BoatAvailabilityPort {
    boolean isAvailableForRental(UUID boatId);
}