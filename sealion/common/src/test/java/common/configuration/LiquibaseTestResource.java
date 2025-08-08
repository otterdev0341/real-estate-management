package common.configuration;

import io.quarkus.liquibase.LiquibaseFactory;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import jakarta.inject.Inject;
import liquibase.Liquibase;
import liquibase.exception.LiquibaseException;

import java.util.Collections;
import java.util.Map;

public class LiquibaseTestResource implements QuarkusTestResourceLifecycleManager {

    @Inject
    LiquibaseFactory liquibaseFactory;

    @Override
    public Map<String, String> start() {
        System.out.println("Starting Liquibase migrations...");
        try (Liquibase liquibase = liquibaseFactory.createLiquibase()) {
            // Run the migrations for the test database.
            // This will apply all changesets from your changelog-master.yaml file.
            liquibase.update(liquibaseFactory.createContexts());
            System.out.println("✅ Liquibase migrations completed successfully.");
        } catch (LiquibaseException e) {
            System.err.println("❌ Failed to run Liquibase migrations: " + e.getMessage());
            throw new RuntimeException("Failed to start LiquibaseTestResource", e);
        }
        // Return an empty map as this resource doesn't provide new configuration properties.
        return Collections.emptyMap();
    }

    @Override
    public void stop() {
        System.out.println("Stopping LiquibaseTestResource. Dropping all database objects...");
        try (Liquibase liquibase = liquibaseFactory.createLiquibase()) {
            // Drops all objects from the test database.
            liquibase.dropAll();
            System.out.println("🗑️ All database objects dropped successfully.");
        } catch (LiquibaseException e) {
            System.err.println("⚠️ Failed to drop all database objects: " + e.getMessage());
            throw new RuntimeException("Failed to stop LiquibaseTestResource", e);
        }
    }
}
