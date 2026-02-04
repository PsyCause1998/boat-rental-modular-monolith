package com.example.boatrental.rentals.domain.model;

import java.time.Instant;

/**
 * Domain events for Rental history (append-only).
 * We keep them as simple records for now.
 */
public sealed interface RentalEvent permits
        RentalEvent.RentalCreated,
        RentalEvent.RentalStarted,
        RentalEvent.RenterChanged,
        RentalEvent.RentalFinished {

    Instant occurredAt();

    record RentalCreated(Instant occurredAt, RentalId rentalId, BoatId boat,
                         CustomerId customer) implements RentalEvent {
    }

    record RentalStarted(Instant occurredAt, RentalId rentalId) implements RentalEvent {
    }

    record RenterChanged(Instant occurredAt, RentalId rentalId, CustomerId newCustomer) implements RentalEvent {
    }

    record RentalFinished(Instant occurredAt, RentalId rentalId) implements RentalEvent {
    }
}
