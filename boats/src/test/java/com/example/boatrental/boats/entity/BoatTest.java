package com.example.boatrental.boats.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class BoatTest {

    @Test
    void should_create_boat_with_available_status_by_default() {
        BoatId id = BoatId.newId();

        Boat boat = new Boat(id, "Black Pearl");

        assertThat(boat.id()).isEqualTo(id);
        assertThat(boat.name()).isEqualTo("Black Pearl");
        assertThat(boat.status()).isEqualTo(BoatStatus.AVAILABLE);
        assertThat(boat.isAvailable()).isTrue();
    }

    @Test
    void should_mark_boat_as_rented_when_available() {
        Boat boat = new Boat(BoatId.newId(), "Poseidon");

        boat.markAsRented();

        assertThat(boat.status()).isEqualTo(BoatStatus.RENTED);
        assertThat(boat.isAvailable()).isFalse();
    }

    @Test
    void should_throw_exception_when_marking_rented_boat_as_rented_again() {
        Boat boat = new Boat(BoatId.newId(), "Atlantis");
        boat.markAsRented();

        assertThatThrownBy(boat::markAsRented)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Boat is not available");
    }

    @Test
    void should_mark_boat_as_available() {
        Boat boat = new Boat(BoatId.newId(), "Odyssey");
        boat.markAsRented();

        boat.markAsAvailable();

        assertThat(boat.status()).isEqualTo(BoatStatus.AVAILABLE);
        assertThat(boat.isAvailable()).isTrue();
    }
}
