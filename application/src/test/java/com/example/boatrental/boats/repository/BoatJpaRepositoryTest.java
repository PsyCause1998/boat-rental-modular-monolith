package com.example.boatrental.boats.repository;

import com.example.boatrental.boats.entity.Boat;
import com.example.boatrental.boats.entity.BoatId;
import com.example.boatrental.boats.entity.BoatStatus;
import com.example.boatrental.boats.repository.BoatJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BoatJpaRepositoryIT {

    @Autowired
    private BoatJpaRepository boatJpaRepository;

    @Test
    @DisplayName("Should save and retrieve a boat")
    void shouldSaveAndRetrieveBoat() {
        BoatId boatId = new BoatId(UUID.randomUUID());
        Boat boat = new Boat(boatId, "Titanic");

        boatJpaRepository.save(boat);

        Optional<Boat> retrievedBoat = boatJpaRepository.findById(boatId);
        assertThat(retrievedBoat).isPresent();
        assertThat(retrievedBoat.get().name()).isEqualTo("Titanic");
        assertThat(retrievedBoat.get().status()).isEqualTo(BoatStatus.AVAILABLE);
    }

    @Test
    @DisplayName("Should mark boat as rented and available")
    void shouldUpdateBoatStatus() {
        BoatId boatId = new BoatId(UUID.randomUUID());
        Boat boat = new Boat(boatId, "Queen Mary");
        boatJpaRepository.save(boat);

        boat.markAsRented();
        boatJpaRepository.save(boat);

        assertThat(boatJpaRepository.findById(boatId))
                .get()
                .extracting(Boat::status)
                .isEqualTo(BoatStatus.RENTED);

        boat.markAsAvailable();
        boatJpaRepository.save(boat);

        assertThat(boatJpaRepository.findById(boatId))
                .get()
                .extracting(Boat::status)
                .isEqualTo(BoatStatus.AVAILABLE);
    }

    @Test
    @DisplayName("Should count boats in the repository")
    void shouldCountBoats() {
        boatJpaRepository.save(new Boat(new BoatId(UUID.randomUUID()), "Boat One"));
        boatJpaRepository.save(new Boat(new BoatId(UUID.randomUUID()), "Boat Two"));

        assertThat(boatJpaRepository.count()).isEqualTo(2);
    }

    @Configuration
    @EntityScan("com.example.boatrental.boats.entity")
    @EnableJpaRepositories("com.example.boatrental.boats.repository")
    static class TestConfig {
    }
}
