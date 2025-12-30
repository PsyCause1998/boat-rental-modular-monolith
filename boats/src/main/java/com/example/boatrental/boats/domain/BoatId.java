package com.example.boatrental.boats.domain;

import java.util.UUID;

public record BoatId(UUID value) {

    public BoatId {
        if (value == null) {
            throw new IllegalArgumentException("BoatId cannot be null");
        }
    }

    public static BoatId newId() {
        return new BoatId(UUID.randomUUID());
    }
}

