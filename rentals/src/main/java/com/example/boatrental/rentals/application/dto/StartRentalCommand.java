package com.example.boatrental.rentals.application.dto;

import java.util.UUID;

public record StartRentalCommand(UUID boatId, UUID customerId) {
    public StartRentalCommand {
        if (boatId == null) throw new IllegalArgumentException("boatId cannot be null");
        if (customerId == null) throw new IllegalArgumentException("customerId cannot be null");
    }
}

