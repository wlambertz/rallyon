package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.usecase

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.Announcement
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.AnnouncementContent
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class AnnouncementUseCaseTest {
    private val now = Instant.parse("2026-11-11T12:00:00Z")
    private val content = AnnouncementContent("Club event", now, now.plusSeconds(3600), "Hall", "Information")
    private val store = MemoryStore()
    private val useCase = AnnouncementUseCase(store, Clock.fixed(now, ZoneOffset.UTC))

    @Test
    fun `only creator can publish and explicit content is visible`() {
        assertThrows(AnnouncementDenied::class.java) { useCase.create(1, content, 9) }
        assertEquals(null, store.announcement)
        assertEquals(content, useCase.create(1, content, 7).content)
        assertEquals(content, useCase.publicRead(1).content)
        assertThrows(AnnouncementVersionConflict::class.java) { useCase.create(1, content, 7) }
    }

    @Test
    fun `withdrawal hides snapshot until explicitly republished with current version`() {
        useCase.create(1, content, 7)
        val withdrawn = useCase.withdraw(1, 0, 7)
        assertEquals(1L, withdrawn.version)
        assertThrows(AnnouncementMissing::class.java) { useCase.publicRead(1) }
        assertEquals(withdrawn, useCase.management(1, 7))
        assertThrows(AnnouncementDenied::class.java) { useCase.management(1, 9) }
        assertThrows(AnnouncementVersionConflict::class.java) { useCase.replace(1, content, 0, 7) }
        val republished = useCase.replace(1, content.copy(venue = "New hall"), 1, 7)
        assertEquals(2L, republished.version)
        assertTrue(republished.published)
        assertEquals("New hall", useCase.publicRead(1).content.venue)
    }

    @Test
    fun `invalid content and losing compare and set leave the existing snapshot intact`() {
        val original = useCase.create(1, content, 7)
        assertThrows(IllegalArgumentException::class.java) { useCase.replace(1, content.copy(title = " "), 0, 7) }
        assertThrows(IllegalArgumentException::class.java) { useCase.replace(1, content.copy(body = "x".repeat(10001)), 0, 7) }
        assertThrows(IllegalArgumentException::class.java) { useCase.replace(1, content.copy(eventEnd = now.minusSeconds(1)), 0, 7) }
        store.rejectNextWrite = true
        assertThrows(AnnouncementVersionConflict::class.java) { useCase.replace(1, content.copy(venue = "Other"), 0, 7) }
        assertEquals(original, useCase.publicRead(1))
    }

    @Test
    fun `other users cannot replace or withdraw and stale withdrawal cannot change the snapshot`() {
        val original = useCase.create(1, content, 7)
        assertThrows(AnnouncementDenied::class.java) { useCase.replace(1, content.copy(venue = "Other"), 0, 9) }
        assertThrows(AnnouncementDenied::class.java) { useCase.withdraw(1, 0, 9) }
        assertThrows(AnnouncementVersionConflict::class.java) { useCase.withdraw(1, 1, 7) }
        assertEquals(original, useCase.publicRead(1))
    }

    @Test
    fun `field boundaries and missing values are enforced without changing the current version`() {
        val boundary = content.copy(title = "t".repeat(200), venue = "v".repeat(500), body = "b".repeat(10000))
        val original = useCase.create(1, boundary, 7)
        val invalid = listOf(
            content.copy(title = "t".repeat(201)), content.copy(venue = "v".repeat(501)),
            content.copy(body = "b".repeat(10001)), content.copy(title = null),
            content.copy(venue = " "), content.copy(body = null),
            content.copy(title = "a\u0000b"), content.copy(venue = "a\u0000b"), content.copy(body = "a\u0000b"),
            content.copy(eventStart = null), content.copy(eventEnd = null),
            content.copy(eventStart = Instant.MIN), content.copy(eventEnd = Instant.MAX),
            content.copy(eventEnd = now.minusSeconds(1))
        )
        invalid.forEach { candidate ->
            assertThrows(IllegalArgumentException::class.java) { useCase.replace(1, candidate, 0, 7) }
            assertEquals(original, useCase.publicRead(1))
        }
    }

    private class MemoryStore : AnnouncementStore {
        var announcement: Announcement? = null
        var rejectNextWrite = false
        override fun creatorId(tournamentId: Long): Long? = if (tournamentId == 1L) 7 else null
        override fun find(tournamentId: Long): Announcement? = announcement
        override fun insert(announcement: Announcement): Boolean {
            if (this.announcement != null) return false
            this.announcement = announcement
            return true
        }
        override fun replace(announcement: Announcement, expectedVersion: Long): Boolean {
            if (rejectNextWrite || this.announcement?.version != expectedVersion) return false
            this.announcement = announcement
            return true
        }
    }
}
