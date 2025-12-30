package com.example.boatrental.boats.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class BoatIdTest {

    @Test
    void should_create_boat_id_with_valid_uuid() {
        UUID uuid = UUID.randomUUID();

        BoatId id = new BoatId(uuid);

        assertThat(id.value()).isEqualTo(uuid);
    }

    @Test
    void should_not_allow_null_uuid() {
        assertThatThrownBy(() -> new BoatId(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("BoatId");
    }

    @Test
    void should_generate_new_id() {
        BoatId id1 = BoatId.newId();
        BoatId id2 = BoatId.newId();

        assertThat(id1).isNotEqualTo(id2);
    }
}
