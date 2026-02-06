package com.example.boatrental.rentals.application.exception;

import com.example.boatrental.shared.exception.BoatRentalException;

import java.util.UUID;

import static com.example.boatrental.shared.exception.BoatRentalException.ErrorCode.RENTAL_NOT_FOUND;

public class RentalNotFoundException extends BoatRentalException {
    public RentalNotFoundException(UUID rentalId) {
        super(RENTAL_NOT_FOUND, "Rental not found: " + rentalId);
    }
}
