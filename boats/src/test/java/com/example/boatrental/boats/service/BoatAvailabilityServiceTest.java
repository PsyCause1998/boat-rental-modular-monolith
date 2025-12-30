package com.example.boatrental.boats.service;

import com.example.boatrental.boats.domain.*;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class BoatAvailabilityServiceTest {

    private final BoatAvailabilityService service = new BoatAvailabilityService();

    @Test
    void should_allow_available_boat() {
        Boat boat = new Boat(BoatId.newId(), "Poseidon");

        assertThatCode(() -> service.ensureAvailable(boat))
                .doesNotThrowAnyException();
    }

    @Test
    void should_throw_if_boat_not_available() {
        Boat boat = new Boat(BoatId.newId(), "Poseidon");
        boat.markAsRented();

        assertThatThrownBy(() -> service.ensureAvailable(boat))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("not available");
    }
}
