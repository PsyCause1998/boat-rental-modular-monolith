package com.example.boatrental.rentals.application.exception;

import com.example.boatrental.shared.exception.BoatRentalException;

import java.util.UUID;

import static com.example.boatrental.shared.exception.BoatRentalException.ErrorCode.CUSTOMER_NOT_FOUND;

public class CustomerNotFoundException extends BoatRentalException {
    public CustomerNotFoundException(UUID customerId) {
        super(CUSTOMER_NOT_FOUND, "Customer not found: " + customerId);
    }
}
