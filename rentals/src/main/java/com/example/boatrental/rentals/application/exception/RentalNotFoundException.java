package com.example.boatrental.rentals.application.exception;

import java.util.UUID;

public class RentalNotFoundException extends RuntimeException {
    public RentalNotFoundException(UUID rentalId) {
        super("Rental not found: " + rentalId);
    }
}
