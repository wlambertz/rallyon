package dev.wlambertz.rallyon.tournamentmgmt;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.io.TempDir;
import org.flywaydb.core.Flyway;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.AnnouncementContent;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.AnnouncementConflict;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.AnnouncementService;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.AnnouncementNotFound;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.AnnouncementInvalid;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.ConfigurationService;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Tournament;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Visibility;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;
import java.time.Instant;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

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
    void upgradesExistingTournamentWithoutPublishingAnAnnouncement() throws Exception {
        Flyway.configure()
                .dataSource(url, username, password)
                .schemas("tournamentmgmt")
                .defaultSchema("tournamentmgmt")
                .target("5")
                .load()
                .migrate();
        try (var connection = DriverManager.getConnection(url, username, password);
             var statement = connection.createStatement()) {
            statement.executeUpdate("""
                    insert into tournamentmgmt.tournaments
                        (id, organizer_id, visibility, name, created_by_user_id, last_modified_by_user_id)
                    values (42, 7, 'PRIVATE', 'Existing draft', 9, 9)
                    """);
        }
        try (ConfigurableApplicationContext context = application().run(arguments())) {
            try (var connection = DriverManager.getConnection(url, username, password);
                 var statement = connection.createStatement();
                 var result = statement.executeQuery(
                         "select count(*) from tournamentmgmt.flyway_schema_history where success and version is not null")) {
                result.next();
                assertThat(result.getInt(1)).isGreaterThanOrEqualTo(6);
            }
            try (var connection = DriverManager.getConnection(url, username, password);
                 var statement = connection.createStatement();
                 var result = statement.executeQuery("select to_regclass('tournamentmgmt.tournaments')")) {
                result.next();
                assertThat(result.getString(1)).isNotNull();
            }
            try (var connection = DriverManager.getConnection(url, username, password);
                 var statement = connection.createStatement()) {
                try (var result = statement.executeQuery("select name from tournamentmgmt.tournaments where id = 42")) {
                    assertThat(result.next()).isTrue();
                    assertThat(result.getString(1)).isEqualTo("Existing draft");
                }
                try (var result = statement.executeQuery("select count(*) from tournamentmgmt.tournament_announcement")) {
                    assertThat(result.next()).isTrue();
                    assertThat(result.getInt(1)).isZero();
                }
                String insert = """
                        insert into tournamentmgmt.tournament_announcement
                            (tournament_id, title, event_start, event_end, venue, body, published, version, published_at)
                        values (%d, 'Cup', '2026-11-15T10:00:00Z', '2026-11-15T16:00:00Z',
                                'Hall', 'Information', true, 0, '2026-11-01T12:00:00Z')
                        """;
                statement.executeUpdate(insert.formatted(42));
                assertThatThrownBy(() -> statement.executeUpdate(
                        insert.formatted(42)))
                        .isInstanceOfSatisfying(SQLException.class, error -> assertThat(error.getSQLState()).isEqualTo("23505"));
                assertThatThrownBy(() -> statement.executeUpdate(
                        insert.formatted(99999)))
                        .isInstanceOfSatisfying(SQLException.class, error -> assertThat(error.getSQLState()).isEqualTo("23503"));
                assertThatThrownBy(() -> statement.executeUpdate("""
                        update tournamentmgmt.tournament_announcement
                        set event_end = event_start - interval '1 second' where tournament_id = 42
                        """))
                        .isInstanceOfSatisfying(SQLException.class, error -> assertThat(error.getSQLState()).isEqualTo("23514"));
                assertThatThrownBy(() -> statement.executeUpdate("""
                        update tournamentmgmt.tournament_announcement
                        set body = repeat('a', 10001) where tournament_id = 42
                        """))
                        .isInstanceOfSatisfying(SQLException.class, error -> assertThat(error.getSQLState()).isEqualTo("23514"));
            }
            try (var connection = DriverManager.getConnection(url, username, password);
                 var statement = connection.createStatement()) {
                statement.executeUpdate("""
                        insert into tournamentmgmt.tournaments
                            (id, organizer_id, visibility, name, created_by_user_id, last_modified_by_user_id)
                        values (43, 7, 'PRIVATE', 'Concurrent draft', 9, 9),
                               (44, 7, 'PRIVATE', 'Concurrent creation', 9, 9),
                               (45, 7, 'PRIVATE', 'Time boundaries', 9, 9)
                        """);
            }
            var service = context.getBean(AnnouncementService.class);
            var start = Instant.parse("2026-11-15T10:00:00Z");
            var content = new AnnouncementContent("Cup", start, start.plusSeconds(3600), "Hall", "Details");
            var boundaries = new AnnouncementContent("Range", Instant.parse("-4712-01-01T00:00:00Z"),
                    Instant.parse("+294276-12-31T23:59:59.999999Z"), "Hall", "Details");
            service.create(45, boundaries, 9);
            assertThat(service.publicRead(45).getContent()).isEqualTo(boundaries);
            var badInitial = new AnnouncementContent("Bad\u0000title", start, start.plusSeconds(3600), "Hall", "Details");
            assertThatThrownBy(() -> service.create(44, badInitial, 9)).isInstanceOf(AnnouncementInvalid.class);
            assertThatThrownBy(() -> service.publicRead(44)).isInstanceOf(AnnouncementNotFound.class);
            var createGate = new CountDownLatch(1);
            try (var executor = Executors.newFixedThreadPool(2)) {
                var first = executor.submit(() -> concurrentCreate(service, content, createGate));
                var alternate = new AnnouncementContent("Other Cup", start, start.plusSeconds(3600), "Hall", "Details");
                var second = executor.submit(() -> concurrentCreate(service, alternate, createGate));
                createGate.countDown();
                var outcomes = java.util.List.of(first.get(), second.get());
                assertThat(outcomes).contains("conflict");
                var winner = outcomes.stream().filter(result -> !result.equals("conflict")).toList();
                assertThat(winner).hasSize(1);
                var expected = winner.getFirst().equals(content.getTitle()) ? content : alternate;
                assertThat(service.publicRead(44).getContent()).isEqualTo(expected);
                assertThat(service.publicRead(44).getPublished()).isTrue();
            }
            assertThat(service.publicRead(44).getVersion()).isZero();
            assertThat(service.create(43, content, 9).getVersion()).isZero();
            var invalidText = new AnnouncementContent("Bad\u0000title", start, start.plusSeconds(3600), "Hall", "Details");
            var invalidTime = new AnnouncementContent("Cup", start, Instant.MAX, "Hall", "Details");
            assertThatThrownBy(() -> service.replace(43, invalidText, 0, 9)).isInstanceOf(AnnouncementInvalid.class);
            assertThatThrownBy(() -> service.replace(43, invalidTime, 0, 9)).isInstanceOf(AnnouncementInvalid.class);
            assertThat(service.publicRead(43).getContent()).isEqualTo(content);
            var configuration = context.getBean(ConfigurationService.class);
            var draft = configuration.get(43);
            configuration.updateDraft(43, Tournament.builder().name("Concurrent draft")
                    .visibility(Visibility.PUBLIC).build(), draft.getVersion(), 10);
            assertThat(service.publicRead(43).getVersion()).isZero();
            var gate = new CountDownLatch(1);
            try (var executor = Executors.newFixedThreadPool(2)) {
                var first = executor.submit(() -> concurrentReplace(service, content, gate));
                var alternate = new AnnouncementContent("Corrected Cup", start, start.plusSeconds(7200), "Other hall", "New details");
                var second = executor.submit(() -> concurrentReplace(service, alternate, gate));
                gate.countDown();
                var outcomes = java.util.List.of(first.get(), second.get());
                assertThat(outcomes).contains("conflict");
                var winner = outcomes.stream().filter(result -> !result.equals("conflict")).toList();
                assertThat(winner).hasSize(1);
                var expected = winner.getFirst().equals(content.getTitle()) ? content : alternate;
                assertThat(service.publicRead(43).getContent()).isEqualTo(expected);
            }
            assertThat(service.publicRead(43).getVersion()).isEqualTo(1);
            assertThat(service.withdraw(43, 1, 9).getVersion()).isEqualTo(2);
            assertThat(service.management(43, 9).getPublished()).isFalse();
            var currentDraft = configuration.get(43);
            configuration.updateDraft(43, Tournament.builder().name("Concurrent draft")
                    .visibility(Visibility.PRIVATE).build(), currentDraft.getVersion(), 10);
            assertThatThrownBy(() -> service.publicRead(43)).isInstanceOf(AnnouncementNotFound.class);
            currentDraft = configuration.get(43);
            configuration.updateDraft(43, Tournament.builder().name("Concurrent draft")
                    .visibility(Visibility.PUBLIC).build(), currentDraft.getVersion(), 10);
            assertThatThrownBy(() -> service.publicRead(43)).isInstanceOf(AnnouncementNotFound.class);
            assertThat(context.isActive()).isTrue();
        }
    }

    private String concurrentCreate(AnnouncementService service, AnnouncementContent content, CountDownLatch gate)
            throws InterruptedException {
        gate.await();
        try {
            service.create(44, content, 9);
            return content.getTitle();
        } catch (AnnouncementConflict expected) {
            return "conflict";
        }
    }

    private String concurrentReplace(AnnouncementService service, AnnouncementContent content, CountDownLatch gate)
            throws InterruptedException {
        gate.await();
        try {
            service.replace(43, content, 0, 9);
            return content.getTitle();
        } catch (AnnouncementConflict expected) {
            return "conflict";
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
