package com.example.boatrental;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import jakarta.annotation.Resource;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Testcontainers
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ActiveRentalUniqueIndexIT {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    @Resource
    JdbcTemplate jdbc;

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

        jdbc.execute("""
            CREATE INDEX idx_rentals_boat_status
            ON rentals (boat_id, status)
        """);

        jdbc.execute("""
            CREATE UNIQUE INDEX uk_active_rental_per_boat
            ON rentals (boat_id)
            WHERE status = 'ACTIVE'
        """);
    }

    @Test
    void should_allow_only_one_active_rental_per_boat() {
        UUID boatId = UUID.randomUUID();

        jdbc.update("""
            INSERT INTO rentals (id, boat_id, customer_id, status)
            VALUES (?, ?, ?, 'ACTIVE')
        """, UUID.randomUUID(), boatId, UUID.randomUUID());

        assertThrows(DataIntegrityViolationException.class, () -> {
            jdbc.update("""
                INSERT INTO rentals (id, boat_id, customer_id, status)
                VALUES (?, ?, ?, 'ACTIVE')
            """, UUID.randomUUID(), boatId, UUID.randomUUID());
        });
    }
}
