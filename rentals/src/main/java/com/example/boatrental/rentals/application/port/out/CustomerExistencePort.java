package com.example.boatrental.rentals.application.port.out;

import com.example.boatrental.rentals.domain.model.CustomerId;

public interface CustomerExistencePort {
    boolean exists(CustomerId customer);
}
