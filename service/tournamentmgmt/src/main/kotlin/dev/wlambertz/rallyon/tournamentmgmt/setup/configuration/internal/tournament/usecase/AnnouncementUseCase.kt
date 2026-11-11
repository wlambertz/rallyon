package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.usecase

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.Announcement
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.AnnouncementContent
import java.time.Clock

interface AnnouncementStore {
    fun creatorId(tournamentId: Long): Long?
    fun find(tournamentId: Long): Announcement?
    fun insert(announcement: Announcement): Boolean
    fun replace(announcement: Announcement, expectedVersion: Long): Boolean
}

class AnnouncementDenied : RuntimeException()
class AnnouncementMissing : RuntimeException()
class AnnouncementVersionConflict : RuntimeException()

class AnnouncementUseCase(private val store: AnnouncementStore, private val clock: Clock) {
    private fun authorize(id: Long, actor: Long) {
        val creator = store.creatorId(id) ?: throw AnnouncementMissing()
        if (actor <= 0 || actor != creator) throw AnnouncementDenied()
    }

    fun create(id: Long, content: AnnouncementContent, actor: Long): Announcement {
        authorize(id, actor)
        content.validate()
        val announcement = Announcement(id, content, true, 0, clock.instant())
        if (!store.insert(announcement)) throw AnnouncementVersionConflict()
        return announcement
    }

    fun replace(id: Long, content: AnnouncementContent, version: Long, actor: Long): Announcement {
        authorize(id, actor)
        content.validate()
        val current = store.find(id) ?: throw AnnouncementMissing()
        if (current.version != version) throw AnnouncementVersionConflict()
        val replacement = current.copy(content = content, published = true, version = version + 1, publishedAt = clock.instant())
        if (!store.replace(replacement, version)) throw AnnouncementVersionConflict()
        return replacement
    }

    fun withdraw(id: Long, version: Long, actor: Long): Announcement {
        authorize(id, actor)
        val current = store.find(id) ?: throw AnnouncementMissing()
        if (!current.published || current.version != version) throw AnnouncementVersionConflict()
        val withdrawn = current.copy(published = false, version = version + 1)
        if (!store.replace(withdrawn, version)) throw AnnouncementVersionConflict()
        return withdrawn
    }

    fun management(id: Long, actor: Long): Announcement {
        authorize(id, actor)
        return store.find(id) ?: throw AnnouncementMissing()
    }

    fun publicRead(id: Long): Announcement = store.find(id)?.takeIf { it.published } ?: throw AnnouncementMissing()
}
