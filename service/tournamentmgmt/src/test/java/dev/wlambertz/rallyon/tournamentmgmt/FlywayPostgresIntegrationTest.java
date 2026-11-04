package dev.wlambertz.rallyon.tournamentmgmt;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.DriverManager;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@EnabledIfEnvironmentVariable(named = "RALLYON_FLYWAY_TEST_URL", matches = "jdbc:postgresql:.*")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FlywayPostgresIntegrationTest {

    private final String url = System.getenv("RALLYON_FLYWAY_TEST_URL");
    private final String username = System.getenv("RALLYON_FLYWAY_TEST_USER");
    private final String password = System.getenv("RALLYON_FLYWAY_TEST_PASSWORD");

    @BeforeEach
    void requireDedicatedEmptyDatabase() throws Exception {
        assertThat(System.getenv("RALLYON_FLYWAY_TEST_ALLOW_MUTATION"))
                .as("explicit permission to modify the dedicated test database").isEqualTo("yes");
        try (var connection = DriverManager.getConnection(url, username, password);
             var statement = connection.createStatement();
             var result = statement.executeQuery("select to_regnamespace('tournamentmgmt')")) {
            result.next();
            assertThat(result.getString(1)).as("dedicated empty database required").isNull();
        }
    }

    @Test
    @Order(2)
    void appliesExistingMigrationsBeforeAcceptingWork() throws Exception {
        try (ConfigurableApplicationContext context = application().run(arguments())) {
            try (var connection = DriverManager.getConnection(url, username, password);
                 var statement = connection.createStatement();
                 var result = statement.executeQuery(
                         "select count(*) from tournamentmgmt.flyway_schema_history where success and version is not null")) {
                result.next();
                assertThat(result.getInt(1)).isGreaterThanOrEqualTo(5);
            }
            try (var connection = DriverManager.getConnection(url, username, password);
                 var statement = connection.createStatement();
                 var result = statement.executeQuery("select to_regclass('tournamentmgmt.tournaments')")) {
                result.next();
                assertThat(result.getString(1)).isNotNull();
            }
            assertThat(context.isActive()).isTrue();
        }
    }

    @Test
    @Order(1)
    void failsStartupWhenMigrationFails(@TempDir Path migrations) throws Exception {
        Files.writeString(migrations.resolve("V1__invalid.sql"), "this is not valid SQL;");
        String schema = "flyway_failure_" + UUID.randomUUID().toString().replace("-", "");
        String[] args = arguments();
        String[] failureArgs = java.util.Arrays.copyOf(args, args.length + 3);
        failureArgs[args.length] = "--spring.flyway.locations=filesystem:" + migrations;
        failureArgs[args.length + 1] = "--spring.flyway.schemas=" + schema;
        failureArgs[args.length + 2] = "--spring.flyway.default-schema=" + schema;
        assertThatThrownBy(() -> application().run(failureArgs))
                .hasRootCauseInstanceOf(java.sql.SQLException.class)
                .hasStackTraceContaining("V1__invalid.sql");
    }

    private SpringApplicationBuilder application() {
        return new SpringApplicationBuilder(TournamentmgmtApplication.class).web(WebApplicationType.SERVLET);
    }

    private String[] arguments() {
        return new String[]{
                "--server.port=0",
                "--spring.config.location=file:src/main/resources/application.properties",
                "--spring.datasource.url=" + url,
                "--spring.datasource.username=" + username,
                "--spring.datasource.password=" + password,
                "--spring.datasource.driver-class-name=org.postgresql.Driver",
                "--spring.jpa.hibernate.ddl-auto=none"
        };
    }
}
