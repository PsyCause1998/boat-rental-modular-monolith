package com.example.boatrental.rentals.domain.exception;

import com.example.boatrental.rentals.domain.model.RentalId;
import com.example.boatrental.rentals.domain.model.RentalStatus;

public class InvalidRentalStateException extends RuntimeException {

    public InvalidRentalStateException(RentalId id, RentalStatus current, String action) {
        super("Cannot " + action + " rental " + id.value() + " when status is " + current);
    }
}