package com.example.boatrental.boats.entity;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(
        name = "boats",
        indexes = {
                @Index(
                        name = "idx_boat_id_status",
                        columnList = "boat_id, status"
                )
        }
)
public class Boat {

    @EmbeddedId
    @AttributeOverride(name = "value", column = @Column(name = "boat_id", nullable = false, updatable = false, unique = true))
    private BoatId id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BoatStatus status;

    /**
     * Required by JPA
     */
    protected Boat() {
    }

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
