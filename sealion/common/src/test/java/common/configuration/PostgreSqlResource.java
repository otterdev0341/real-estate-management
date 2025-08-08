package common.configuration;



import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Collections;
import java.util.Map;

public class PostgreSqlResource implements QuarkusTestResourceLifecycleManager {

    private PostgreSQLContainer<?> postgres;

    @Override
    public Map<String, String> start() {
        // Create a new PostgreSQL container with a specific image.
        postgres = new PostgreSQLContainer<>("postgres:17")
                .withDatabaseName("test-db")
                .withUsername("testuser")
                .withPassword("testpass");

        // Start the container
        postgres.start();
        System.out.println("✅ PostgreSQL Testcontainer started successfully.");

        // Return the configuration for the test application.
        // The Quarkus test framework will use these properties to connect to the DB.
        return Collections.singletonMap(
                "quarkus.datasource.jdbc.url", postgres.getJdbcUrl()
        );
    }

    @Override
    public void stop() {
        // Stop the container after all tests are finished.
        if (postgres != null) {
            postgres.stop();
            System.out.println("❌ PostgreSQL Testcontainer stopped.");
        }
    }
}
