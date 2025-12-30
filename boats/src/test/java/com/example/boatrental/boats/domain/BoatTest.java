package com.example.boatrental.boats.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class BoatTest {

    @Test
    void boat_name_cannot_be_null() {
        assertThatThrownBy(() -> new Boat(BoatId.newId(), null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void newly_created_boat_should_be_available() {
        Boat boat = new Boat(BoatId.newId(), "Poseidon");

        assertThat(boat.status()).isEqualTo(BoatStatus.AVAILABLE);
        assertThat(boat.isAvailable()).isTrue();
    }

    @Test
    void boat_can_be_marked_as_rented() {
        Boat boat = new Boat(BoatId.newId(), "Poseidon");

        boat.markAsRented();

        assertThat(boat.status()).isEqualTo(BoatStatus.RENTED);
    }

    @Test
    void boat_cannot_be_rented_twice() {
        Boat boat = new Boat(BoatId.newId(), "Poseidon");
        boat.markAsRented();

        assertThatThrownBy(boat::markAsRented)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("not available");
    }

    @Test
    void rented_boat_can_be_marked_available_again() {
        Boat boat = new Boat(BoatId.newId(), "Poseidon");
        boat.markAsRented();

        boat.markAsAvailable();

        assertThat(boat.isAvailable()).isTrue();
    }
}
