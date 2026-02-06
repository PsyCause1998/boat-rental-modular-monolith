package com.example.boatrental.rentals.domain;

import com.example.boatrental.rentals.domain.exception.InvalidRentalStateException;
import com.example.boatrental.rentals.domain.model.*;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class RentalTest {

    @Test
    void should_create_rental_in_created_state_with_history() {
        RentalId id = RentalId.newId();
        BoatId boat = new BoatId(UUID.randomUUID());
        CustomerId customer = new CustomerId(UUID.randomUUID());
        Instant now = Instant.now();

        Rental rental = Rental.create(id, boat, customer, now);

        assertThat(rental.getStatus()).isEqualTo(RentalStatus.CREATED);
        assertThat(rental.getId()).isEqualTo(id);
        assertThat(rental.getBoatId()).isEqualTo(boat);
        assertThat(rental.getCustomerId()).isEqualTo(customer);
        assertThat(rental.history()).hasSize(1);
        assertThat(rental.history().getFirst()).isInstanceOf(RentalEvent.RentalCreated.class);
    }

    @Test
    void should_start_rental_only_from_created() {
        Rental rental = Rental.create(RentalId.newId(),
                new BoatId(UUID.randomUUID()),
                new CustomerId(UUID.randomUUID()),
                Instant.now());

        rental.start(Instant.now());

        assertThat(rental.getStatus()).isEqualTo(RentalStatus.ACTIVE);
        assertThat(rental.history()).hasSize(2);
        assertThat(rental.history().getLast()).isInstanceOf(RentalEvent.RentalStarted.class);
    }

    @Test
    void should_not_start_rental_twice() {
        Rental rental = Rental.create(RentalId.newId(),
                new BoatId(UUID.randomUUID()),
                new CustomerId(UUID.randomUUID()),
                Instant.now());
        rental.start(Instant.now());

        assertThatThrownBy(() -> rental.start(Instant.now()))
                .isInstanceOf(InvalidRentalStateException.class)
                .hasMessageContaining("Cannot start rental");
    }

    @Test
    void should_change_renter_only_when_active() {
        Rental rental = Rental.create(RentalId.newId(),
                new BoatId(UUID.randomUUID()),
                new CustomerId(UUID.randomUUID()),
                Instant.now());
        rental.start(Instant.now());

        CustomerId newCustomer = new CustomerId(UUID.randomUUID());
        rental.changeRenter(newCustomer, Instant.now());

        assertThat(rental.getCustomerId()).isEqualTo(newCustomer);
        assertThat(rental.history()).hasSize(3);
        assertThat(rental.history().getLast()).isInstanceOf(RentalEvent.RenterChanged.class);
    }

    @Test
    void should_not_change_renter_when_not_active() {
        Rental rental = Rental.create(RentalId.newId(),
                new BoatId(UUID.randomUUID()),
                new CustomerId(UUID.randomUUID()),
                Instant.now());

        assertThatThrownBy(() -> rental.changeRenter(new CustomerId(UUID.randomUUID()), Instant.now()))
                .isInstanceOf(InvalidRentalStateException.class)
                .hasMessageContaining("Cannot change renter");
    }

    @Test
    void should_finish_rental_only_when_active() {
        Rental rental = Rental.create(RentalId.newId(),
                new BoatId(UUID.randomUUID()),
                new CustomerId(UUID.randomUUID()),
                Instant.now());
        rental.start(Instant.now());

        rental.finish(Instant.now());

        assertThat(rental.getStatus()).isEqualTo(RentalStatus.FINISHED);
        assertThat(rental.history()).hasSize(3);
        assertThat(rental.history().getLast()).isInstanceOf(RentalEvent.RentalFinished.class);
    }

    @Test
    void should_not_finish_rental_when_not_active() {
        Rental rental = Rental.create(RentalId.newId(),
                new BoatId(UUID.randomUUID()),
                new CustomerId(UUID.randomUUID()),
                Instant.now());

        assertThatThrownBy(() -> rental.finish(Instant.now()))
                .isInstanceOf(InvalidRentalStateException.class)
                .hasMessageContaining("Cannot finish rental");
    }
}