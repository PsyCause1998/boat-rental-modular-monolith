package com.example.boatrental.rentals.application.exception;

import com.example.boatrental.shared.exception.BoatRentalException;

import java.util.UUID;

import static com.example.boatrental.shared.exception.BoatRentalException.ErrorCode.BOAT_NOT_AVAILABLE;

public class BoatNotAvailableException extends BoatRentalException {
    public BoatNotAvailableException(UUID boatId) {
        super(BOAT_NOT_AVAILABLE, "Boat is not available for rental: " + boatId);
    }
}
