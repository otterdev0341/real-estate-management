package app.database;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DatabaseConnectionTest {
    @Inject
    EntityManager entityManager;

    @Test
    @Transactional
    @Order(1) // This ensures this test method runs first in this class
    public void testDatabaseConnectionAndMigration() {
        // This is a simple test to verify that the database is up and running
        // and that Liquibase has created the 'users' table.
        // A successful query on a known table is a good indicator.

        // You can query for the existence of a table by selecting a column.
        // If this query throws an exception, it means the table doesn't exist.
        List<?> resultList = entityManager.createNativeQuery("SELECT id FROM genders LIMIT 1").getResultList();

        // If the query was successful, the resultList will not be null,
        // even if no records are found.
        assertNotNull(resultList, "The query result should not be null, indicating a successful connection and table existence.");

        // Optionally, you can log a message to confirm the test passed.
        System.out.println("✅ Database connection and schema migration successful!");
    }
}