package com.example.boatrental.rentals.application.exception;

import com.example.boatrental.shared.exception.BoatRentalException;

import java.util.UUID;

import static com.example.boatrental.shared.exception.BoatRentalException.ErrorCode.ACTIVE_RENTAL_ALREADY_EXISTS;

public class ActiveRentalAlreadyExistsException extends BoatRentalException {
    public ActiveRentalAlreadyExistsException(UUID boatId) {
        super(ACTIVE_RENTAL_ALREADY_EXISTS, "Active rental already exists for boat: " + boatId);
    }
}