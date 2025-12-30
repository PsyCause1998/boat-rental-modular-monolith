package com.example.boatrental.boats.domain;

import java.util.Objects;

public class Boat {

    private final BoatId id;
    private String name;
    private BoatStatus status;

    public Boat(BoatId id, String name) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.status = BoatStatus.AVAILABLE;
    }

    public BoatId id() {
        return id;
    }

    public String name() {
        return name;
    }

    public BoatStatus status() {
        return status;
    }

    public boolean isAvailable() {
        return status == BoatStatus.AVAILABLE;
    }

    public void markAsRented() {
        if (!isAvailable()) {
            throw new IllegalStateException("Boat is not available");
        }
        this.status = BoatStatus.RENTED;
    }

    public void markAsAvailable() {
        this.status = BoatStatus.AVAILABLE;
    }
}
