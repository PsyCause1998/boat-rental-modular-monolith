package com.example.boatrental.rentals.infrastructure.persistence.mapper;

import com.example.boatrental.rentals.infrastructure.persistence.entity.RentalEventJpaEntity;
import com.example.boatrental.rentals.infrastructure.persistence.entity.RentalEventType;
import com.example.boatrental.rentals.infrastructure.persistence.entity.RentalJpaEntity;
import com.example.boatrental.rentals.infrastructure.persistence.entity.RentalStatusJpa;
import com.example.boatrental.rentals.domain.model.*;

import java.util.stream.Collectors;

public final class RentalPersistenceMapper {

    private RentalPersistenceMapper() {}

    public static RentalJpaEntity toEntity(Rental rental) {
        var entity = new RentalJpaEntity(
                rental.getId().value(),
                rental.getBoatId().value(),
                rental.getCustomerId().value(),
                toJpaStatus(rental.getStatus())
        );

        var eventEntities = rental.history().stream()
                .map(ev -> toEventEntity(entity, ev))
                .collect(Collectors.toList());

        entity.setEvents(eventEntities);
        return entity;
    }

    public static Rental toDomain(RentalJpaEntity entity) {
        var history = entity.getEvents().stream()
                .map(RentalPersistenceMapper::toDomainEvent)
                .collect(Collectors.toList());

        return Rental.rehydrate(
                new RentalId(entity.getId()),
                new BoatId(entity.getBoatId()),
                new CustomerId(entity.getCustomerId()),
                toDomainStatus(entity.getStatus()),
                history
        );
    }

    private static RentalStatusJpa toJpaStatus(RentalStatus status) {
        return RentalStatusJpa.valueOf(status.name());
    }

    private static RentalStatus toDomainStatus(RentalStatusJpa status) {
        return RentalStatus.valueOf(status.name());
    }

    private static RentalEventJpaEntity toEventEntity(RentalJpaEntity rental, RentalEvent event) {
        return switch (event) {
            case RentalEvent.RentalCreated e ->
                    new RentalEventJpaEntity(rental, RentalEventType.RENTAL_CREATED, e.occurredAt(),
                            e.boat().value(), e.customer().value());

            case RentalEvent.RentalStarted e ->
                    new RentalEventJpaEntity(rental, RentalEventType.RENTAL_STARTED, e.occurredAt(),
                            null, null);

            case RentalEvent.RenterChanged e ->
                    new RentalEventJpaEntity(rental, RentalEventType.RENTER_CHANGED, e.occurredAt(),
                            null, e.newCustomer().value());

            case RentalEvent.RentalFinished e ->
                    new RentalEventJpaEntity(rental, RentalEventType.RENTAL_FINISHED, e.occurredAt(),
                            null, null);
        };
    }

    private static RentalEvent toDomainEvent(RentalEventJpaEntity e) {
        // IMPORTANT : il faut aussi reconstruire RentalId si tes events le portent.
        // Si tes RentalEvent contiennent RentalId, ajoute une colonne rentalId (ou récupère via e.getRental().getId()).

        // Ici on suppose que RentalId est toujours connu (via le parent).
        // Si tes records d’events exigent un RentalId, passe-le en param.
        throw new UnsupportedOperationException("Implement with rentalId passed in");
    }
}
