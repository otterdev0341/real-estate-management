package app.configuration.migration;


import io.quarkus.liquibase.LiquibaseFactory;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import liquibase.Liquibase;
import liquibase.changelog.ChangeSetStatus;
import liquibase.exception.LiquibaseException;

import java.util.List;

@ApplicationScoped
public class MigrationService {

    @Inject
    LiquibaseFactory liquibaseFactory;

    void onStart(@Observes StartupEvent ev, LiquibaseFactory liquibaseFactory) {
        // This is a guard to ensure this only runs in the `test` or `dev` profile.
        // It prevents this behavior in a production environment.
        String quarkusProfile = System.getProperty("quarkus.profile", "dev");
        if (quarkusProfile.equals("dev") || quarkusProfile.equals("test")) {
            try (Liquibase liquibase = liquibaseFactory.createLiquibase()) {
                // Drop all objects from the default schema.
                // Note: The dropAll command is available in the open-source Liquibase API,
                // even if the change type is part of Liquibase Pro.
//                liquibase.dropAll();

                // Migrate the database with the main changelog.
                liquibase.update(liquibaseFactory.createContexts(), liquibaseFactory.createLabels());
            } catch (LiquibaseException e) {
                throw new RuntimeException("Failed to run Liquibase migrations on startup.", e);
            }
        }
    }

    public void checkMigration() {
        try (Liquibase liquibase = liquibaseFactory.createLiquibase()) {
            List<ChangeSetStatus> status = liquibase.getChangeSetStatuses(liquibaseFactory.createContexts(), liquibaseFactory.createLabels());
        } catch (LiquibaseException e) {
            throw new RuntimeException(e);
        }
    }

}
