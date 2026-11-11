package dev.wlambertz.rallyon.tournamentmgmt;

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.*;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.ParticipantFieldServiceImpl;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.usecase.ParticipantFieldStore;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.transaction.PlatformTransactionManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.sql.DriverManager;
import java.time.Clock;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.*;

@EnabledIfEnvironmentVariable(named = "RALLYON_FIELD_TEST_URL", matches = "jdbc:postgresql:.*")
class ParticipantFieldPostgresIntegrationTest {
    private final String url = System.getenv("RALLYON_FIELD_TEST_URL");
    private final String user = System.getenv("RALLYON_FIELD_TEST_USER");
    private final String password = System.getenv("RALLYON_FIELD_TEST_PASSWORD");
    private ParticipantFieldService fields;
    private EntryBookService entries;

    @Test
    void migratesAndPreservesPrivateVersionedFieldsAcrossSourceChanges() throws Exception {
        initializeFreshDatabase();
        try (var context = new SpringApplicationBuilder(TournamentmgmtApplication.class)
                .web(WebApplicationType.SERVLET).run(
                        "--server.port=0", "--spring.config.location=file:src/main/resources/application.properties",
                        "--spring.datasource.url=" + url, "--spring.datasource.username=" + user,
                        "--spring.datasource.password=" + password,
                        "--spring.datasource.driver-class-name=org.postgresql.Driver",
                        "--spring.jpa.hibernate.ddl-auto=none")) {
            fields = context.getBean(ParticipantFieldService.class);
            entries = context.getBean(EntryBookService.class);
            virtualStateAndFailedInitialization();
            var ids = acceptedEntries();
            selectionAndAtomicCompetingWrites(ids);
            retainedSelectionsAndExplicitReconciliation(ids);
            var store = context.getBean(ParticipantFieldStore.class);
            var transactionManager = context.getBean(PlatformTransactionManager.class);
            forcedSourceCommitDuringPreparation(ids.getFirst(), store, transactionManager);
            forcedConcurrentReadRemainsConsistent(store, transactionManager);
            assertPrivateStorageInvariants();
        }
    }

    private void initializeFreshDatabase() throws Exception {
        assertThat(System.getenv("RALLYON_FIELD_TEST_ALLOW_MUTATION")).isEqualTo("yes");
        assertThat(number("select count(*) from pg_namespace where nspname='tournamentmgmt'"))
                .as("a fresh, explicitly selected test database is required").isZero();
        Flyway.configure().dataSource(url, user, password).schemas("tournamentmgmt")
                .defaultSchema("tournamentmgmt").target("7").load().migrate();
        sql("""
                insert into tournamentmgmt.tournaments
                    (id, organizer_id, visibility, name, created_by_user_id, last_modified_by_user_id)
                values (701,7,'PRIVATE','Field test',42,42),(702,7,'PRIVATE','Empty book',42,42)
                """);
        sql("""
                insert into tournamentmgmt.tournament_disciplines
                    (id,tournament_id,discipline_id,category,display_name,team_size)
                values (710,701,1,'SINGLES','Singles','SINGLES'),
                       (720,701,2,'DOUBLES','Doubles','DOUBLES'),
                       (730,702,1,'SINGLES','Other singles','SINGLES')
                """);
        sql("""
                insert into tournamentmgmt.tournament_brackets
                    (tournament_discipline_id,bracket_id,display_name,format,capacity_amount,capacity_unit)
                values (710,'A','Original A','ROUND_ROBIN',1,'PARTICIPANTS'),
                       (710,'B','Other B','ROUND_ROBIN',null,null),
                       (720,'A','Doubles A','ROUND_ROBIN',null,null),
                       (730,'Z','Empty Z','ROUND_ROBIN',null,null)
                """);
    }

    private void virtualStateAndFailedInitialization() throws Exception {
        var empty = fields.get(701, 1, "A", 42);
        assertThat(empty.getPrepared()).isFalse();
        assertThat(empty.getVersion()).isZero();
        assertThat(empty.getFreshness()).isEqualTo(FieldFreshness.UNPREPARED);
        assertThat(empty.getSelection()).isNull();
        assertThat(empty.getSourceEntryBookVersion()).isNull();
        assertThat(empty.getUnits()).isEmpty();
        assertThatThrownBy(() -> fields.get(701, 1, "A", 99)).isInstanceOf(ParticipantFieldForbidden.class);
        assertThatThrownBy(() -> fields.get(701, 1, "missing", 42)).isInstanceOf(ParticipantFieldNotFound.class);
        assertThatThrownBy(() -> fields.prepare(701, request(1, "A", List.of(999L)), 0, 42))
                .isInstanceOf(ParticipantFieldInvalid.class);
        assertThat(fields.get(701, 1, "A", 42)).isEqualTo(empty);
        assertThat(number("select count(*) from tournamentmgmt.participant_fields")).isZero();
        assertThat(number("select count(*) from tournamentmgmt.entry_books")).isZero();
        var explicitEmpty = fields.prepare(702, new ParticipantFieldRequest(1L, "Z", List.of(), 0L, 0L), 0, 42);
        assertThat(explicitEmpty.getPrepared()).isTrue();
        assertThat(explicitEmpty.getVersion()).isEqualTo(1);
        assertThat(explicitEmpty.getFreshness()).isEqualTo(FieldFreshness.CURRENT);
        assertThat(number("select count(*) from tournamentmgmt.entry_books where tournament_id=702")).isZero();
    }

    private List<Long> acceptedEntries() {
        entries.intake(701, true, 0, 42);
        var one = entries.create(701, single("one"), bookVersion(), 42).getRecords().getLast().getId();
        entries.decide(701, one, EntryState.ACCEPTED, bookVersion(), 42);
        var two = entries.create(701, single("two"), bookVersion(), 42).getRecords().getLast().getId();
        entries.decide(701, two, EntryState.ACCEPTED, bookVersion(), 42);
        var pair = entries.create(701, new EntryInput(2L, "A", List.of(
                new EntryPlayer("one", "One"), new EntryPlayer("partner", "Partner"))), bookVersion(), 42)
                .getRecords().getLast().getId();
        entries.decide(701, pair, EntryState.ACCEPTED, bookVersion(), 42);
        entries.intake(701, false, bookVersion(), 42);
        return List.of(one, two, pair);
    }

    private void selectionAndAtomicCompetingWrites(List<Long> ids) throws Exception {
        var source = entries.get(701, 42);
        race(() -> fields.prepare(701, request(1, "A", List.of(ids.get(0))), 0, 42),
                () -> fields.prepare(701, request(1, "A", List.of(ids.get(1))), 0, 42));
        long version = fields.get(701, 1, "A", 42).getVersion();
        race(() -> fields.prepare(701, request(1, "A", List.of(ids.get(1), ids.get(0))), version, 42),
                () -> fields.prepare(701, request(1, "A", List.of()), version, 42));
        var full = fields.prepare(701, request(1, "A", List.of(ids.get(1), ids.get(0))),
                fields.get(701, 1, "A", 42).getVersion(), 42);
        assertThat(full.getUnits()).extracting(ParticipantFieldUnit::getEntryId).containsExactly(ids.get(0), ids.get(1));
        assertThat(full.getCapacityReview()).isEqualTo(CapacityReview.MANUAL);
        assertThat(entries.get(701, 42)).isEqualTo(source);
        unchanged(() -> fields.prepare(701, request(1, "A", List.of(ids.get(0))), 0, 42), ParticipantFieldConflict.class);
        unchanged(() -> fields.prepare(701, request(1, "A", List.of(ids.get(0))), full.getVersion(), 99), ParticipantFieldForbidden.class);
        unchanged(() -> fields.prepare(701, request(1, "A", List.of(ids.get(0), ids.get(0))), full.getVersion(), 42), ParticipantFieldInvalid.class);
        var pair = fields.prepare(701, request(2, "A", List.of(ids.get(2))), 0, 42);
        assertThat(pair.getUnits().getFirst().getPlayers()).extracting(EntryPlayer::getKey).containsExactly("one", "partner");
        assertThat(fields.get(701, 1, "A", 42)).isEqualTo(full);
        assertThatThrownBy(() -> fields.prepare(701, request(1, "B", List.of(ids.get(0))), 0, 42))
                .isInstanceOf(ParticipantFieldConflict.class);
        assertThat(number("select count(*) from tournamentmgmt.participant_fields where tournament_id=701 and bracket_id='B'"))
                .as("incompatible first preparation rolls back field initialization").isZero();
    }

    private void retainedSelectionsAndExplicitReconciliation(List<Long> ids) throws Exception {
        var before = fields.get(701, 1, "A", 42);
        sql("update tournamentmgmt.tournament_brackets set display_name='Renamed A' where tournament_discipline_id=710 and bracket_id='A'");
        advanceDraft();
        var stale = fields.get(701, 1, "A", 42);
        sameSaved(before, stale);
        assertThat(stale.getFreshness()).isEqualTo(FieldFreshness.OUTDATED);
        var renamed = fields.prepare(701, request(1, "A", ids.subList(0, 2)), before.getVersion(), 42);
        assertThat(renamed.getSelection().getBracketName()).isEqualTo("Renamed A");
        assertThat(renamed.getUnits()).allMatch(u -> u.getSelection().getBracketName().equals("Original A"));
        sql("delete from tournamentmgmt.tournament_brackets where tournament_discipline_id=710 and bracket_id='A'");
        advanceDraft();
        var removed = fields.get(701, 1, "A", 42);
        sameSaved(renamed, removed);
        assertThat(removed.getFreshness()).isEqualTo(FieldFreshness.OUTDATED);
        unchanged(() -> fields.prepare(701, request(1, "A", ids.subList(0, 2)), removed.getVersion(), 42), ParticipantFieldInvalid.class);
        sql("insert into tournamentmgmt.tournament_brackets(tournament_discipline_id,bracket_id,display_name,format) values(710,'A','Restored A','ROUND_ROBIN')");
        advanceDraft();
        var cleared = fields.prepare(701, request(1, "A", List.of()), removed.getVersion(), 42);
        assertThat(cleared.getUnits()).isEmpty();
        assertThat(cleared.getAudit()).hasSize((int) cleared.getVersion());
        assertThat(cleared.getFreshness()).isEqualTo(FieldFreshness.CURRENT);
        var same = fields.prepare(701, request(1, "A", List.of()), cleared.getVersion(), 42);
        assertThat(same.getVersion()).isEqualTo(cleared.getVersion() + 1);
    }

    private void forcedSourceCommitDuringPreparation(long entry, ParticipantFieldStore store,
                                                      PlatformTransactionManager transactions) throws Exception {
        var pause = new PausedBook(store, transactions);
        var before = fields.get(701, 1, "A", 42);
        var input = request(1, "A", List.of(entry));
        try (var executor = Executors.newSingleThreadExecutor()) {
            var future = executor.submit(() -> pause.service.prepare(701, input, before.getVersion(), 42));
            try {
                assertThat(pause.observed.await(15, TimeUnit.SECONDS)).isTrue();
                entries.withdraw(701, entry, bookVersion(), 42);
                sql("update tournamentmgmt.tournament_brackets set display_name='Concurrent A' where tournament_discipline_id=710 and bracket_id='A'");
                advanceDraft();
            } finally { pause.resume.countDown(); }
            var prepared = future.get(20, TimeUnit.SECONDS);
            assertThat(prepared.getSourceEntryBookVersion()).isEqualTo(input.getSourceEntryBookVersion());
            assertThat(prepared.getObservedTournamentVersion()).isEqualTo(input.getSourceTournamentVersion());
            assertThat(prepared.getSelection().getBracketName()).isEqualTo("Restored A");
            assertThat(prepared.getFreshness()).isEqualTo(FieldFreshness.CURRENT);
            assertThat(prepared.getUnits()).extracting(ParticipantFieldUnit::getEntryId).containsExactly(entry);
            var now = fields.get(701, 1, "A", 42);
            sameSaved(prepared, now);
            assertThat(now.getFreshness()).isEqualTo(FieldFreshness.OUTDATED);
            assertThat(now.getObservedEntryBookVersion()).isGreaterThan(prepared.getObservedEntryBookVersion());
        }
    }

    private void forcedConcurrentReadRemainsConsistent(ParticipantFieldStore store,
                                                       PlatformTransactionManager transactions) throws Exception {
        var before = fields.get(701, 1, "A", 42);
        var pause = new PausedBook(store, transactions);
        try (var executor = Executors.newSingleThreadExecutor()) {
            var future = executor.submit(() -> pause.service.get(701, 1, "A", 42));
            try {
                assertThat(pause.observed.await(15, TimeUnit.SECONDS)).isTrue();
                entries.intake(701, true, bookVersion(), 42);
                fields.prepare(701, request(1, "A", List.of()), before.getVersion(), 42);
            } finally { pause.resume.countDown(); }
            assertThat(future.get(20, TimeUnit.SECONDS)).isEqualTo(before);
            var latest = fields.get(701, 1, "A", 42);
            assertThat(latest.getVersion()).isEqualTo(before.getVersion() + 1);
            assertThat(latest.getUnits()).isEmpty();
        }
    }

    private void assertPrivateStorageInvariants() throws Exception {
        assertThat(number("select count(*) from tournamentmgmt.participant_fields f where f.version <> (select count(*) from tournamentmgmt.participant_field_audit a where a.tournament_id=f.tournament_id and a.discipline_id=f.discipline_id and a.bracket_id=f.bracket_id)"))
                .as("one successful audit event per field version").isZero();
        assertThat(number("select (select count(*) from tournamentmgmt.tournament_participants) + (select count(*) from tournamentmgmt.tournament_bracket_participants) + (select count(*) from tournamentmgmt.tournament_announcement)"))
                .as("field operations do not publish or populate legacy rosters").isZero();
        assertThat(fields.get(701, 1, "A", 42).getAudit()).allMatch(a -> a.getActorId() == 42 && a.getAt() != null);
    }

    private final class PausedBook {
        final CountDownLatch observed = new CountDownLatch(1);
        final CountDownLatch resume = new CountDownLatch(1);
        final ParticipantFieldService service;

        PausedBook(ParticipantFieldStore delegate, PlatformTransactionManager transactions) {
            var once = new AtomicBoolean();
            var proxy = (ParticipantFieldStore) Proxy.newProxyInstance(ParticipantFieldStore.class.getClassLoader(),
                    new Class<?>[]{ParticipantFieldStore.class}, (ignored, method, args) -> {
                        Object value;
                        try { value = method.invoke(delegate, args); }
                        catch (InvocationTargetException error) { throw error.getCause(); }
                        // Pause after a real read establishes the transaction snapshot, without a production test hook.
                        if (method.getName().equals("book") && once.compareAndSet(false, true)) {
                            observed.countDown();
                            if (!resume.await(20, TimeUnit.SECONDS)) throw new IllegalStateException("source commit timed out");
                        }
                        return value;
                    });
            service = new ParticipantFieldServiceImpl(proxy, Clock.systemUTC(), transactions);
        }
    }

    private void race(Callable<ParticipantFieldView> first, Callable<ParticipantFieldView> second) throws Exception {
        var start = new CountDownLatch(1);
        try (var executor = Executors.newFixedThreadPool(2)) {
            var a = executor.submit(() -> compete(first, start));
            var b = executor.submit(() -> compete(second, start));
            start.countDown();
            var results = List.of(a.get(30, TimeUnit.SECONDS), b.get(30, TimeUnit.SECONDS));
            var winners = results.stream().filter(ParticipantFieldView.class::isInstance).map(ParticipantFieldView.class::cast).toList();
            assertThat(winners).hasSize(1);
            assertThat(results.stream().filter(ParticipantFieldConflict.class::isInstance).count()).isEqualTo(1);
            assertThat(fields.get(701, 1, "A", 42)).isEqualTo(winners.getFirst());
        }
    }

    private Object compete(Callable<ParticipantFieldView> operation, CountDownLatch start) throws Exception {
        start.await();
        try { return operation.call(); } catch (ParticipantFieldConflict expected) { return expected; }
    }

    private void unchanged(Runnable operation, Class<? extends Throwable> error) {
        var before = fields.get(701, 1, "A", 42);
        var source = entries.get(701, 42);
        assertThatThrownBy(operation::run).isInstanceOf(error);
        assertThat(fields.get(701, 1, "A", 42)).isEqualTo(before);
        assertThat(entries.get(701, 42)).isEqualTo(source);
    }

    private void sameSaved(ParticipantFieldView expected, ParticipantFieldView actual) {
        assertThat(actual.getVersion()).isEqualTo(expected.getVersion());
        assertThat(actual.getSelection()).isEqualTo(expected.getSelection());
        assertThat(actual.getUnits()).isEqualTo(expected.getUnits());
        assertThat(actual.getAudit()).isEqualTo(expected.getAudit());
        assertThat(actual.getSourceTournamentVersion()).isEqualTo(expected.getSourceTournamentVersion());
        assertThat(actual.getSourceEntryBookVersion()).isEqualTo(expected.getSourceEntryBookVersion());
    }

    private ParticipantFieldRequest request(long discipline, String bracket, List<Long> selected) {
        return new ParticipantFieldRequest(discipline, bracket, selected, draftVersion(), bookVersion());
    }
    private EntryInput single(String key) { return new EntryInput(1L, "A", List.of(new EntryPlayer(key, key))); }
    private long bookVersion() { return entries.get(701, 42).getVersion(); }
    private long draftVersion() {
        try { return number("select version from tournamentmgmt.tournaments where id=701"); }
        catch (Exception error) { throw new IllegalStateException(error); }
    }
    private void advanceDraft() throws Exception { sql("update tournamentmgmt.tournaments set version=version+1 where id=701"); }
    private void sql(String text) throws Exception {
        try (var connection = DriverManager.getConnection(url, user, password); var statement = connection.createStatement()) {
            statement.executeUpdate(text);
        }
    }
    private long number(String query) throws Exception {
        try (var connection = DriverManager.getConnection(url, user, password); var statement = connection.createStatement();
             var result = statement.executeQuery(query)) { result.next(); return result.getLong(1); }
    }
}
