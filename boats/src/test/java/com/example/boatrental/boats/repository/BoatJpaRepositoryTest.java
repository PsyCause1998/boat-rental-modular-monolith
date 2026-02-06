package com.example.boatrental.boats.repository;

import com.example.boatrental.boats.entity.Boat;
import com.example.boatrental.boats.entity.BoatId;
import com.example.boatrental.boats.entity.BoatStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(BoatJpaRepositoryTest.TestBootConfig.class)
class BoatJpaRepositoryTest {

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

        Optional<Boat> rentedBoat = boatJpaRepository.findById(boatId);
        assertThat(rentedBoat).isPresent();
        assertThat(rentedBoat.get().status()).isEqualTo(BoatStatus.RENTED);

        boat.markAsAvailable();
        boatJpaRepository.save(boat);

        Optional<Boat> availableBoat = boatJpaRepository.findById(boatId);
        assertThat(availableBoat).isPresent();
        assertThat(availableBoat.get().status()).isEqualTo(BoatStatus.AVAILABLE);
    }

    @Test
    @DisplayName("Should count boats in the repository")
    void shouldCountBoats() {
        boatJpaRepository.save(new Boat(new BoatId(UUID.randomUUID()), "Boat One"));
        boatJpaRepository.save(new Boat(new BoatId(UUID.randomUUID()), "Boat Two"));

        long count = boatJpaRepository.count();
        assertThat(count).isEqualTo(2);
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @EntityScan("com.example.boatrental.boats.entity")
    @EnableJpaRepositories("com.example.boatrental.boats.repository")
    static class TestBootConfig { }
}
