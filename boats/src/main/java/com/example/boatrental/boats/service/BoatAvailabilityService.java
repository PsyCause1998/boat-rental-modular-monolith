package com.example.boatrental.boats.service;

import com.example.boatrental.boats.domain.Boat;

public class BoatAvailabilityService {

    public void ensureAvailable(Boat boat) {
        if (!boat.isAvailable()) {
            throw new IllegalStateException("Boat is not available for rental");
        }
    }
}
