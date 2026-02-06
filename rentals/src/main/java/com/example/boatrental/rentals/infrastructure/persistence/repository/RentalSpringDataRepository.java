package com.example.boatrental.rentals.infrastructure.persistence.repository;

import com.example.boatrental.rentals.infrastructure.persistence.entity.RentalJpaEntity;
import com.example.boatrental.rentals.infrastructure.persistence.entity.RentalStatusJpa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RentalSpringDataRepository extends JpaRepository<RentalJpaEntity, UUID> {
    boolean existsByBoatIdAndStatus(UUID boatId, RentalStatusJpa status);
}

