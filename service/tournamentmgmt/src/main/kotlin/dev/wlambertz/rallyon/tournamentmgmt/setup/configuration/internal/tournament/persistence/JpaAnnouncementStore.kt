package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.Announcement
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.AnnouncementContent
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.usecase.AnnouncementStore
import jakarta.persistence.EntityManager
import java.sql.Timestamp
import java.time.Instant
import java.time.OffsetDateTime
import org.springframework.stereotype.Repository

@Repository
class JpaAnnouncementStore(private val entityManager: EntityManager) : AnnouncementStore {
    override fun creatorId(tournamentId: Long): Long? = entityManager.createNativeQuery(
        "SELECT created_by_user_id FROM tournamentmgmt.tournaments WHERE id = :id", java.lang.Long::class.java
    ).setParameter("id", tournamentId).resultList.firstOrNull().let { (it as? Number)?.toLong() }

    override fun find(tournamentId: Long): Announcement? {
        val row = entityManager.createNativeQuery(
            "SELECT title, event_start, event_end, venue, body, published, version, published_at " +
                "FROM tournamentmgmt.tournament_announcement WHERE tournament_id = :id"
        ).setParameter("id", tournamentId).resultList.firstOrNull() as? Array<*> ?: return null
        return Announcement(
            tournamentId,
            AnnouncementContent(row[0] as String, instant(row[1]), instant(row[2]), row[3] as String, row[4] as String),
            row[5] as Boolean, (row[6] as Number).toLong(), instant(row[7])
        )
    }

    override fun insert(announcement: Announcement): Boolean = parameters(
        entityManager.createNativeQuery(
            "INSERT INTO tournamentmgmt.tournament_announcement " +
                "(tournament_id, title, event_start, event_end, venue, body, published, version, published_at) " +
                "VALUES (:id, :title, :start, :end, :venue, :body, :published, :version, :publishedAt) " +
                "ON CONFLICT (tournament_id) DO NOTHING"
        ), announcement
    ).executeUpdate() == 1

    override fun replace(announcement: Announcement, expectedVersion: Long): Boolean = parameters(
        entityManager.createNativeQuery(
            "UPDATE tournamentmgmt.tournament_announcement SET title = :title, event_start = :start, event_end = :end, " +
                "venue = :venue, body = :body, published = :published, version = :version, published_at = :publishedAt " +
                "WHERE tournament_id = :id AND version = :expectedVersion"
        ), announcement
    ).setParameter("expectedVersion", expectedVersion).executeUpdate() == 1

    private fun parameters(query: jakarta.persistence.Query, announcement: Announcement): jakarta.persistence.Query = query
        .setParameter("id", announcement.tournamentId)
        .setParameter("title", announcement.content.title)
        .setParameter("start", announcement.content.eventStart)
        .setParameter("end", announcement.content.eventEnd)
        .setParameter("venue", announcement.content.venue)
        .setParameter("body", announcement.content.body)
        .setParameter("published", announcement.published)
        .setParameter("version", announcement.version)
        .setParameter("publishedAt", announcement.publishedAt)

    private fun instant(value: Any?): Instant = when (value) {
        is Instant -> value
        is Timestamp -> value.toInstant()
        is OffsetDateTime -> value.toInstant()
        else -> error("Unexpected announcement timestamp type")
    }
}
