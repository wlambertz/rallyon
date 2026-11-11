package dev.wlambertz.rallyon.tournamentmgmt;

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.*;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.sql.DriverManager;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;

@EnabledIfEnvironmentVariable(named = "RALLYON_ENTRY_TEST_URL", matches = "jdbc:postgresql:.*")
class EntryBookPostgresIntegrationTest {
    private final String url = System.getenv("RALLYON_ENTRY_TEST_URL");
    private final String user = System.getenv("RALLYON_ENTRY_TEST_USER");
    private final String password = System.getenv("RALLYON_ENTRY_TEST_PASSWORD");

    @Test
    void migratesAndPreservesAtomicEntryWorkflow() throws Exception {
        assertThat(System.getenv("RALLYON_ENTRY_TEST_ALLOW_MUTATION")).isEqualTo("yes");
        try (var connection = DriverManager.getConnection(url, user, password);
             var statement = connection.createStatement();
             var result = statement.executeQuery("select to_regnamespace('tournamentmgmt')")) {
            result.next();
            assertThat(result.getString(1)).as("dedicated empty database required").isNull();
        }
        Flyway.configure().dataSource(url, user, password).schemas("tournamentmgmt")
                .defaultSchema("tournamentmgmt").target("6").load().migrate();
        try (var connection = DriverManager.getConnection(url, user, password);
             var statement = connection.createStatement()) {
            statement.executeUpdate("""
                    insert into tournamentmgmt.tournaments
                        (id, organizer_id, visibility, name, created_by_user_id, last_modified_by_user_id)
                    values (601, 7, 'PRIVATE', 'Entry test', 42, 42),
                           (602, 7, 'PRIVATE', 'Other tournament', 42, 42)
                    """);
            statement.executeUpdate("""
                    insert into tournamentmgmt.tournament_disciplines
                        (id, tournament_id, discipline_id, category, display_name, team_size)
                    values (610, 601, 1, 'SINGLES', 'Singles', 'SINGLES'),
                           (620, 601, 2, 'DOUBLES', 'Doubles', 'DOUBLES')
                    """);
            statement.executeUpdate("""
                    insert into tournamentmgmt.tournament_brackets
                        (tournament_discipline_id, bracket_id, display_name, format)
                    values (610, 'A', 'Singles A', 'ROUND_ROBIN'),
                           (620, 'A', 'Doubles A', 'ROUND_ROBIN'),
                           (620, 'B', 'Doubles B', 'ROUND_ROBIN')
                    """);
        }
        try (var context = new SpringApplicationBuilder(TournamentmgmtApplication.class)
                .web(WebApplicationType.SERVLET).run(
                        "--server.port=0", "--spring.config.location=file:src/main/resources/application.properties",
                        "--spring.datasource.url=" + url, "--spring.datasource.username=" + user,
                        "--spring.datasource.password=" + password, "--spring.datasource.driver-class-name=org.postgresql.Driver",
                        "--spring.jpa.hibernate.ddl-auto=none")) {
            var service = context.getBean(EntryBookService.class);
            var empty = service.get(601, 42);
            assertThat(empty.getVersion()).isZero();
            assertThat(empty.getIntakeOpen()).isFalse();
            assertThat(empty.getRecords()).isEmpty();
            assertThatThrownBy(() -> service.get(601, 99)).isInstanceOf(EntryForbidden.class);
            assertThatThrownBy(() -> service.create(601, single("p1"), 0, 42)).isInstanceOf(EntryConflict.class);
            assertThat(service.get(601, 42)).isEqualTo(empty);
            try (var connection = DriverManager.getConnection(url, user, password);
                 var statement = connection.createStatement();
                 var result = statement.executeQuery("select count(*) from tournamentmgmt.entry_books")) {
                result.next();
                assertThat(result.getLong(1)).as("reads and rejected first mutations create no book").isZero();
            }

            race(service, 601, () -> service.intake(601, true, 0, 42), () -> service.intake(601, true, 0, 42));
            var first = service.create(601, single("p1"), version(service), 42);
            long singleId = first.getRecords().getFirst().getId();
            service.decide(601, singleId, EntryState.ACCEPTED, version(service), 42);
            var corrected = service.correct(601, singleId, single("p2"), version(service), 42);
            assertThat(record(corrected, singleId).getState()).isEqualTo(EntryState.RECEIVED);
            assertThat(record(corrected, singleId).getPlayers()).isEqualTo(single("p2").getPlayers());
            unchanged(service, () -> service.withdraw(601, singleId, 0, 42), EntryConflict.class);
            unchanged(service, () -> service.correct(601, singleId, single("p3"), version(service), 99), EntryForbidden.class);
            unchanged(service, () -> service.withdraw(601, singleId, version(service), 99), EntryForbidden.class);
            service.withdraw(601, singleId, version(service), 42);
            unchanged(service, () -> service.decide(601, singleId, EntryState.ACCEPTED, version(service), 42), EntryConflict.class);

            var paired = service.create(601, pair("p1", "p2", "A"), version(service), 42);
            long pairId = paired.getRecords().getLast().getId();
            unchanged(service, () -> service.create(601, pair("p2", "p1", "B"), version(service), 42), EntryConflict.class);
            service.decide(601, pairId, EntryState.REJECTED, version(service), 42);
            var replacement = service.create(601, pair("p1", "p3", "B"), version(service), 42);
            long newPairId = replacement.getRecords().getLast().getId();
            unchanged(service, () -> service.decide(601, pairId, EntryState.ACCEPTED, version(service), 42), EntryConflict.class);
            long beforeClose = version(service);
            race(service, 601, () -> service.intake(601, false, beforeClose, 42),
                    () -> service.create(601, single("p9"), beforeClose, 42));
            if (service.get(601, 42).getIntakeOpen()) service.intake(601, false, version(service), 42);
            unchanged(service, () -> service.create(601, single("p8"), version(service), 42), EntryConflict.class);
            unchanged(service, () -> service.correct(601, newPairId, pair("p4", "p5", "A"), version(service), 42), EntryConflict.class);
            service.decide(601, newPairId, EntryState.ACCEPTED, version(service), 42);
            service.withdraw(601, newPairId, version(service), 42);
            service.intake(601, true, version(service), 42);

            long beforeCreate = version(service);
            race(service, 601, () -> service.create(601, pair("p4", "p5", "A"), beforeCreate, 42),
                    () -> service.create(601, pair("p5", "p4", "B"), beforeCreate, 42));
            var active = service.get(601, 42).getRecords().stream()
                    .filter(r -> r.getState() == EntryState.RECEIVED && r.getSelection().getDisciplineId() == 2)
                    .findFirst().orElseThrow();
            long beforeCorrect = version(service);
            race(service, 601, () -> service.correct(601, active.getId(), pair("p6", "p7", "A"), beforeCorrect, 42),
                    () -> service.correct(601, active.getId(), pair("p7", "p8", "B"), beforeCorrect, 42));
            var snapshot = service.get(601, 42);
            try (var connection = DriverManager.getConnection(url, user, password);
                 var statement = connection.createStatement()) {
                statement.executeUpdate("delete from tournamentmgmt.tournament_brackets where tournament_discipline_id = 620");
            }
            assertThat(service.get(601, 42)).isEqualTo(snapshot);
            unchanged(service, () -> service.correct(601, active.getId(), pair("p6", "p7", "A"), version(service), 42), EntryInvalid.class);
            service.withdraw(601, active.getId(), version(service), 42);
            service.intake(602, true, 0, 42);
            assertThatThrownBy(() -> service.withdraw(602, active.getId(), 1, 42)).isInstanceOf(EntryNotFound.class);
            assertThat(service.get(601, 42).getAudit()).allMatch(event -> event.getActorId() == 42);
            try (var connection = DriverManager.getConnection(url, user, password);
                 var statement = connection.createStatement();
                 var result = statement.executeQuery("select (select count(*) from tournamentmgmt.tournament_participants) + (select count(*) from tournamentmgmt.tournament_announcement)")) {
                result.next();
                assertThat(result.getLong(1)).isZero();
            }
        }
    }

    private long version(EntryBookService service) { return service.get(601, 42).getVersion(); }
    private EntryRecord record(EntryBookView view, long id) { return view.getRecords().stream().filter(r -> r.getId() == id).findFirst().orElseThrow(); }
    private EntryInput single(String key) { return new EntryInput(1L, "A", List.of(new EntryPlayer(key, key))); }
    private EntryInput pair(String one, String two, String bracket) {
        return new EntryInput(2L, bracket, List.of(new EntryPlayer(one, one), new EntryPlayer(two, two)));
    }
    private void unchanged(EntryBookService service, Runnable operation, Class<? extends Throwable> error) {
        var before = service.get(601, 42);
        assertThatThrownBy(operation::run).isInstanceOf(error);
        assertThat(service.get(601, 42)).isEqualTo(before);
    }
    private void race(EntryBookService service, long tournament, Callable<EntryBookView> a, Callable<EntryBookView> b) throws Exception {
        var latch = new CountDownLatch(1);
        try (var executor = Executors.newFixedThreadPool(2)) {
            var one = executor.submit(() -> compete(a, latch));
            var two = executor.submit(() -> compete(b, latch));
            latch.countDown();
            var outcomes = List.of(one.get(30, TimeUnit.SECONDS), two.get(30, TimeUnit.SECONDS));
            var winners = outcomes.stream().filter(EntryBookView.class::isInstance).map(EntryBookView.class::cast).toList();
            assertThat(winners).hasSize(1);
            assertThat(outcomes.stream().filter(EntryConflict.class::isInstance).count()).isEqualTo(1);
            var actual = service.get(tournament, 42);
            assertThat(actual.getRecords()).isEqualTo(winners.getFirst().getRecords());
            assertThat(actual.getVersion()).isEqualTo(winners.getFirst().getVersion());
            assertThat(actual.getIntakeOpen()).isEqualTo(winners.getFirst().getIntakeOpen());
            assertThat(actual.getAudit()).isEqualTo(winners.getFirst().getAudit());
        }
    }
    private Object compete(Callable<EntryBookView> operation, CountDownLatch latch) throws Exception {
        latch.await();
        try { return operation.call(); } catch (EntryConflict expected) { return expected; }
    }
}
