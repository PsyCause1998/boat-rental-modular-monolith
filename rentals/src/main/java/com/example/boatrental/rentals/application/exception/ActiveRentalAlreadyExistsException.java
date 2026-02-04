package com.example.boatrental.rentals.application.exception;

import java.util.UUID;

public class ActiveRentalAlreadyExistsException extends RuntimeException {
    public ActiveRentalAlreadyExistsException(UUID boatId) {
        super("Active rental already exists for boat: " + boatId);
    }
}