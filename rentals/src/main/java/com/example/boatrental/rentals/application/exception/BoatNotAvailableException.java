package com.example.boatrental.rentals.application.exception;

import java.util.UUID;

public class BoatNotAvailableException extends RuntimeException {
    public BoatNotAvailableException(UUID boatId) {
        super("Boat is not available for rental: " + boatId);
    }
}
