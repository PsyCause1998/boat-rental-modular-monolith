package com.example.boatrental;

import com.example.boatrental.rentals.application.dto.StartRentalCommand;
import com.example.boatrental.rentals.application.port.in.StartRentalUseCase;
import com.example.boatrental.rentals.application.port.out.BoatAvailabilityPort;
import com.example.boatrental.rentals.application.port.out.CustomerExistencePort;
import com.example.boatrental.rentals.application.port.out.RentalRepository;
import com.example.boatrental.rentals.application.port.out.TimeProvider;
import com.example.boatrental.rentals.application.service.StartRentalService;
import com.example.boatrental.rentals.domain.model.BoatId;
import com.example.boatrental.rentals.domain.model.Rental;
import com.example.boatrental.rentals.domain.model.RentalId;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(
        properties = {
                "spring.jpa.open-in-view=false",
                // on laisse Hibernate tranquille, on gère le schema en SQL dans le test
                "spring.jpa.hibernate.ddl-auto=none"
        }
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StartRentalConcurrencyIT {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    @Resource
    JdbcTemplate jdbc;

    @Resource
    StartRentalUseCase startRentalUseCase;

    @BeforeAll
    void setupSchema() {
        jdbc.execute("DROP TABLE IF EXISTS rentals");

        jdbc.execute("""
                    CREATE TABLE rentals (
                      id UUID PRIMARY KEY,
                      boat_id UUID NOT NULL,
                      customer_id UUID NOT NULL,
                      status VARCHAR(32) NOT NULL
                    )
                """);

        // Index perf (facultatif mais cohérent)
        jdbc.execute("""
                    CREATE INDEX idx_rentals_boat_status
                    ON rentals (boat_id, status)
                """);

        // Règle métier DB: 1 seul ACTIVE par boat
        jdbc.execute("""
                    CREATE UNIQUE INDEX uk_active_rental_per_boat
                    ON rentals (boat_id)
                    WHERE status = 'ACTIVE'
                """);
    }

    @Test
    void should_allow_only_one_active_rental_per_boat_even_under_concurrency() throws Exception {
        UUID boatId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        int threads = 2;
        ExecutorService pool = Executors.newFixedThreadPool(threads);

        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch start = new CountDownLatch(1);

        List<Future<Object>> futures = new ArrayList<>();

        for (int i = 0; i < threads; i++) {
            futures.add(pool.submit(() -> {
                ready.countDown();
                start.await(3, TimeUnit.SECONDS);
                try {
                    // Appelle le vrai service (use case)
                    return startRentalUseCase.start(new StartRentalCommand(boatId, customerId));
                } catch (Exception ex) {
                    return ex;
                }
            }));
        }

        // attend que les 2 threads soient prêts, puis go
        assertThat(ready.await(3, TimeUnit.SECONDS)).isTrue();
        start.countDown();

        int success = 0;
        int dataIntegrityFails = 0;
        int otherFails = 0;

        for (Future<Object> f : futures) {
            Object result = f.get(5, TimeUnit.SECONDS);
            if (result instanceof RentalId) {
                success++;
            } else if (result instanceof DataIntegrityViolationException) {
                dataIntegrityFails++;
            } else if (result instanceof Exception) {
                otherFails++;
                // utile si tu veux voir l’exception exacte
                // ((Exception) result).printStackTrace();
            }
        }

        pool.shutdownNow();

        // On attend exactement 1 succès + 1 échec DB (le 2e insert ACTIVE)
        assertThat(success).isEqualTo(1);
        assertThat(dataIntegrityFails).isEqualTo(1);
        assertThat(otherFails).isEqualTo(0);

        // Et la DB doit contenir 1 seul ACTIVE pour ce boat
        Integer count = jdbc.queryForObject("""
                    SELECT COUNT(*)
                    FROM rentals
                    WHERE boat_id = ? AND status = 'ACTIVE'
                """, Integer.class, boatId);

        assertThat(count).isEqualTo(1);
    }

    @TestConfiguration
    static class TestWiring {

        @Bean
        StartRentalUseCase startRentalUseCase(
                RentalRepository rentalRepository,
                BoatAvailabilityPort boatAvailabilityPort,
                CustomerExistencePort customerExistencePort,
                TimeProvider timeProvider
        ) {
            return new StartRentalService(
                    rentalRepository,
                    boatAvailabilityPort,
                    customerExistencePort,
                    timeProvider
            );
        }

        @Bean
        BoatAvailabilityPort boatAvailabilityPort() {
            return (boatId) -> true;
        }

        @Bean
        CustomerExistencePort customerExistencePort() {
            return (customerId) -> true;
        }

        @Bean
        TimeProvider timeProvider() {
            return () -> Instant.parse("2026-02-06T12:00:00Z");
        }

        /**
         * Implémentation JDBC simple du port RentalRepository pour le test.
         * But: provoquer la contrainte DB en concurrence.
         */
        @Bean
        RentalRepository rentalRepository(JdbcTemplate jdbc) {
            return new RentalRepository() {

                @Override
                public boolean existsActiveRentalForBoat(BoatId boat) {
                    Integer count = jdbc.queryForObject("""
                                SELECT COUNT(*)
                                FROM rentals
                                WHERE boat_id = ? AND status = 'ACTIVE'
                            """, Integer.class, boat.value());
                    return count != null && count > 0;
                }

                @Override
                public Rental save(Rental rental) {
                    jdbc.update("""
                                        INSERT INTO rentals (id, boat_id, customer_id, status)
                                        VALUES (?, ?, ?, ?)
                                    """,
                            rental.getId().value(),
                            rental.getBoatId().value(),
                            rental.getCustomerId().value(),
                            rental.getStatus().name()
                    );
                    return rental;
                }

                @Override
                public java.util.Optional<Rental> findById(RentalId id) {
                    throw new UnsupportedOperationException("Not needed for this test");
                }
            };
        }
    }
}
