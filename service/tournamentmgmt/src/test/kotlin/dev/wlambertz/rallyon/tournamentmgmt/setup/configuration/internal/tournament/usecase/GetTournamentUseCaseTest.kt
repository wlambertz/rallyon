package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.usecase

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.Tournament
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.TournamentNotFoundException
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.TournamentStatus
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.Visibility
import java.time.Instant
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class GetTournamentUseCaseTest {
    private val stored = Tournament(
        id = 42, version = 3, organizerId = 5, name = "Saved Cup", visibility = Visibility.PRIVATE,
        status = TournamentStatus.DRAFT, createdAt = Instant.parse("2026-11-02T10:00:00Z"),
        createdByUserId = 12, lastModifiedAt = Instant.parse("2026-11-02T11:00:00Z"), lastModifiedByUserId = 24
    )

    @Test
    fun `returns an unchanged partial draft without saving`() {
        val store = ReadStore(stored)
        val query = GetTournamentService(store)

        assertThat(query.execute(42)).isEqualTo(stored)
        assertThat(query.execute(42)).isEqualTo(stored)
        assertThat(store.lookups).containsExactly(42L, 42L)
        assertThat(store.saves).isZero()
        assertThat(store.current).isEqualTo(stored)
    }

    @Test
    fun `reads all stored statuses without validating draft completeness`() {
        for (status in TournamentStatus.entries) {
            val incomplete = stored.copy(status = status, name = null, visibility = null)
            val store = ReadStore(incomplete)

            assertThat(GetTournamentService(store).execute(42)).isEqualTo(incomplete)
            assertThat(store.lookups).containsExactly(42L)
            assertThat(store.saves).isZero()
        }
    }

    @Test
    fun `missing configuration reports the requested id and never saves`() {
        val store = ReadStore(null)

        val error = assertThrows(TournamentNotFoundException::class.java) {
            GetTournamentService(store).execute(999)
        }

        assertThat(error.message).isEqualTo("Tournament 999 was not found")
        assertThat(store.lookups).containsExactly(999L)
        assertThat(store.saves).isZero()
    }

    private class ReadStore(val current: Tournament?) : TournamentStore {
        val lookups = mutableListOf<Long>()
        var saves = 0

        override fun findById(id: Long): Tournament? {
            lookups += id
            return current
        }

        override fun save(tournament: Tournament, expectedVersion: Long?): Tournament {
            saves++
            error("Retrieval must not save")
        }
    }
}
