package com.example.boatrental.rentals.application.port.out;

import com.example.boatrental.rentals.domain.model.BoatId;

public interface BoatAvailabilityPort {
    boolean isAvailableForRental(BoatId boat);
}