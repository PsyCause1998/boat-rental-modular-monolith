package com.example.boatrental.rentals.application.port.out;

import java.util.UUID;

public interface CustomerExistencePort {
    boolean exists(UUID customerId);
}
