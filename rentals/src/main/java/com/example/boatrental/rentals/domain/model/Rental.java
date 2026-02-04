package com.example.boatrental.rentals.domain.model;

import com.example.boatrental.rentals.domain.exception.InvalidRentalStateException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Rental {

    private final RentalId id;
    private final BoatId boat;
    private CustomerId customer;
    private RentalStatus status;

    private final List<RentalEvent> history;

    private Rental(RentalId id, BoatId boat, CustomerId customer, RentalStatus status, List<RentalEvent> history) {
        this.id = Objects.requireNonNull(id);
        this.boat = Objects.requireNonNull(boat);
        this.customer = Objects.requireNonNull(customer);
        this.status = Objects.requireNonNull(status);
        this.history = new ArrayList<>(history);
    }

    public static Rental create(RentalId id, BoatId boat, CustomerId customer, Instant occurredAt) {
        Objects.requireNonNull(occurredAt);

        List<RentalEvent> history = new ArrayList<>();
        history.add(new RentalEvent.RentalCreated(occurredAt, id, boat, customer));

        return new Rental(id, boat, customer, RentalStatus.CREATED, history);
    }

    public static Rental rehydrate(
            RentalId id,
            BoatId boat,
            CustomerId customer,
            RentalStatus status,
            List<RentalEvent> history
    ) {
        return new Rental(
                id,
                boat,
                customer,
                status,
                List.copyOf(history)
        );
    }

    public RentalId id() {
        return id;
    }

    public BoatId boat() {
        return boat;
    }

    public CustomerId customer() {
        return customer;
    }

    public RentalStatus status() {
        return status;
    }

    /**
     * Immutable view of history (append-only internally).
     */
    public List<RentalEvent> history() {
        return List.copyOf(history);
    }

    public void start(Instant occurredAt) {
        Objects.requireNonNull(occurredAt);

        if (status != RentalStatus.CREATED) {
            throw new InvalidRentalStateException(id, status, "start");
        }
        status = RentalStatus.ACTIVE;
        history.add(new RentalEvent.RentalStarted(occurredAt, id));
    }

    public void changeRenter(CustomerId newCustomer, Instant occurredAt) {
        Objects.requireNonNull(newCustomer);
        Objects.requireNonNull(occurredAt);

        if (status != RentalStatus.ACTIVE) {
            throw new InvalidRentalStateException(id, status, "change renter for");
        }
        this.customer = newCustomer;
        history.add(new RentalEvent.RenterChanged(occurredAt, id, newCustomer));
    }

    public void finish(Instant occurredAt) {
        Objects.requireNonNull(occurredAt);

        if (status != RentalStatus.ACTIVE) {
            throw new InvalidRentalStateException(id, status, "finish");
        }
        status = RentalStatus.FINISHED;
        history.add(new RentalEvent.RentalFinished(occurredAt, id));
    }
}
