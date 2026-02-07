package com.example.boatrental.rentals.application.port.out;

import com.example.boatrental.rentals.domain.model.CustomerId;

import java.util.UUID;

public interface CustomerExistencePort {
    boolean exists(UUID customerId);
}
