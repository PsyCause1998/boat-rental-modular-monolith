package com.example.boatrental.boats.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;

@Embeddable
public record BoatId(UUID value) implements Serializable {

    public BoatId {
        if (value == null) {
            throw new IllegalArgumentException("BoatId cannot be null");
        }
    }

    public static BoatId newId() {
        return new BoatId(UUID.randomUUID());
    }
}
