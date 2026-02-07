package com.example.boatrental.rentals.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "rentals",
        indexes = {
                @Index(name = "idx_rentals_boat_status", columnList = "boatId,status")
        }
)
public class RentalJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID boatId;

    @Column(nullable = false)
    private UUID customerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RentalStatusJpa status;

    @OneToMany(mappedBy = "rental", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("occurredAt ASC")
    private List<RentalEventJpaEntity> events = new ArrayList<>();

    protected RentalJpaEntity() {
    }

    public RentalJpaEntity(UUID id, UUID boatId, UUID customerId, RentalStatusJpa status) {
        this.id = id;
        this.boatId = boatId;
        this.customerId = customerId;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public UUID getBoatId() {
        return boatId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public RentalStatusJpa getStatus() {
        return status;
    }

    public List<RentalEventJpaEntity> getEvents() {
        return events;
    }

    public void addEvent(RentalEventJpaEntity event) {
        events.add(event);
        event.setRental(this);
    }

    public void setEvents(List<RentalEventJpaEntity> newEvents) {
        events.clear();
        for (var e : newEvents) addEvent(e);
    }


    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public void setStatus(RentalStatusJpa status) {
        this.status = status;
    }
}
