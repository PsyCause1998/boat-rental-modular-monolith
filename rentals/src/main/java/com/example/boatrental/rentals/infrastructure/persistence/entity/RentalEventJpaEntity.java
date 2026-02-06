package com.example.boatrental.rentals.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "rental_events")
public class RentalEventJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "rental_id", nullable = false)
    private RentalJpaEntity rental;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RentalEventType type;

    @Column(nullable = false)
    private Instant occurredAt;

    // Payload minimal selon tes events
    private UUID boatId;
    private UUID customerId;

    protected RentalEventJpaEntity() {
    }

    public RentalEventJpaEntity(RentalJpaEntity rental,
                                RentalEventType type,
                                Instant occurredAt,
                                UUID boatId,
                                UUID customerId) {
        this.rental = rental;
        this.type = type;
        this.occurredAt = occurredAt;
        this.boatId = boatId;
        this.customerId = customerId;
    }

    public Long getId() {
        return id;
    }

    public void setRental(RentalJpaEntity rental) {
        this.rental = rental;
    }

    public RentalEventType getType() {
        return type;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public UUID getBoatId() {
        return boatId;
    }

    public UUID getCustomerId() {
        return customerId;
    }
}
