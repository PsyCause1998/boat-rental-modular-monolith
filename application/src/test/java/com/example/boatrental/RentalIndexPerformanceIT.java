package com.example.boatrental;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ⚠️ These tests are NOT strict benchmarks.
 * They are meant to:
 * - demonstrate the impact of indexes
 * - detect major performance regressions
 * <p>
 * Timing depends on machine load, Docker, CI, etc.
 * Keep thresholds generous.
 */
@Testcontainers
@SpringBootTest(properties = {
        "spring.jpa.open-in-view=false",
        "spring.jpa.hibernate.ddl-auto=none"
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RentalIndexPerformanceIT {

    private static final Logger log = LoggerFactory.getLogger(RentalIndexPerformanceIT.class);

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    @Resource
    JdbcTemplate jdbc;

    // Ajuste si besoin
    private static final int DATASET_SIZE = 1_000_000; // FINISHED rows
    private static final int BATCH_SIZE = 5_000;

    @BeforeAll
    void setupSchemaOnce() {
        jdbc.execute("DROP TABLE IF EXISTS rentals");

        jdbc.execute("""
                    CREATE TABLE rentals (
                      id UUID PRIMARY KEY,
                      boat_id UUID NOT NULL,
                      customer_id UUID NOT NULL,
                      status VARCHAR(32) NOT NULL
                    )
                """);

        // Important : pas d’index ici, chaque test gère ses index
    }

    @BeforeEach
    void resetData() {
        // On garde la table, mais on nettoie les lignes + index éventuels
        jdbc.execute("TRUNCATE TABLE rentals");
        jdbc.execute("DROP INDEX IF EXISTS idx_rentals_boat_status");
        jdbc.execute("DROP INDEX IF EXISTS uk_active_rental_per_boat");
    }

    @Test
    void compare_query_time_with_and_without_index() {
        UUID targetBoat = UUID.randomUUID();

        // 1) seed data
        seedFinishedRows(DATASET_SIZE);
        insertActiveForBoat(targetBoat);

        // 2) warmup (évite une 1ère exécution "à froid")
        countActiveForBoat(targetBoat);

        // 3) measure without index
        long noIndexMs = measureMs(() -> countActiveForBoat(targetBoat));

        // 4) create index that matches the query predicate
        jdbc.execute("""
                    CREATE INDEX idx_rentals_boat_status
                    ON rentals (boat_id, status)
                """);

        // warmup after index
        countActiveForBoat(targetBoat);

        // 5) measure with index
        long withIndexMs = measureMs(() -> countActiveForBoat(targetBoat));

        log.info("Query time WITHOUT index: {} ms", noIndexMs);
        log.info("Query time WITH index   : {} ms", withIndexMs);

        // Assertion volontairement souple (pédagogique)
        assertThat(withIndexMs).isLessThan(noIndexMs);
    }

    @Test
    void existsActiveRental_query_should_remain_reasonably_fast_with_index() {
        UUID targetBoat = UUID.randomUUID();

        // 1) create index (sinon le test ne veut rien dire)
        jdbc.execute("""
                    CREATE INDEX idx_rentals_boat_status
                    ON rentals (boat_id, status)
                """);

        // 2) seed data
        seedFinishedRows(50_000); // moins que l’autre test pour limiter le temps global
        insertActiveForBoat(targetBoat);

        // 3) warmup
        countActiveForBoat(targetBoat);

        // 4) measure
        long durationMs = measureMs(() -> countActiveForBoat(targetBoat));
        log.info("Indexed query took {} ms", durationMs);

        // Seuil large : but = détecter une catastrophe (index absent / requête cassée)
        assertThat(durationMs).isLessThan(50);
    }

    // ---------------- helpers ----------------

    private Integer countActiveForBoat(UUID boatId) {
        Integer count = jdbc.queryForObject("""
                    SELECT COUNT(*)
                    FROM rentals
                    WHERE boat_id = ? AND status = 'ACTIVE'
                """, Integer.class, boatId);
        return count == null ? 0 : count;
    }

    private void insertActiveForBoat(UUID boatId) {
        jdbc.update("""
                    INSERT INTO rentals (id, boat_id, customer_id, status)
                    VALUES (?, ?, ?, 'ACTIVE')
                """, UUID.randomUUID(), boatId, UUID.randomUUID());
    }

    private void seedFinishedRows(int rows) {
        // Batch insert pour rester rapide (sinon 100k inserts 1 par 1 = long)
        List<Object[]> batch = new ArrayList<>(BATCH_SIZE);

        for (int i = 1; i <= rows; i++) {
            batch.add(new Object[]{
                    UUID.randomUUID(),
                    UUID.randomUUID(),
                    UUID.randomUUID(),
                    "FINISHED"
            });

            if (batch.size() == BATCH_SIZE) {
                flushBatch(batch);
            }
        }

        if (!batch.isEmpty()) {
            flushBatch(batch);
        }
    }

    private void flushBatch(List<Object[]> batch) {
        jdbc.batchUpdate("""
                    INSERT INTO rentals (id, boat_id, customer_id, status)
                    VALUES (?, ?, ?, ?)
                """, batch);
        batch.clear();
    }

    private long measureMs(Runnable action) {
        long start = System.nanoTime();
        action.run();
        return (System.nanoTime() - start) / 1_000_000;
    }
}
