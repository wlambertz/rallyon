package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain

import java.time.Instant

data class AnnouncementContent(
    val title: String?,
    val eventStart: Instant?,
    val eventEnd: Instant?,
    val venue: String?,
    val body: String?
) {
    fun validate() {
        require(!title.isNullOrBlank() && title.length <= 200) { "Title must contain 1 to 200 characters" }
        require(!venue.isNullOrBlank() && venue.length <= 500) { "Venue must contain 1 to 500 characters" }
        require(!body.isNullOrBlank() && body.length <= 10000) { "Body must contain 1 to 10000 characters" }
        require(listOf(title, venue, body).none { it?.contains('\u0000') == true }) { "Text must not contain NUL" }
        require(eventStart != null && eventEnd != null && !eventEnd.isBefore(eventStart)) { "Event time range is invalid" }
        require(eventStart >= MIN_PERSISTED_TIME && eventEnd <= MAX_PERSISTED_TIME) { "Event time is outside the supported range" }
    }

    companion object {
        private val MIN_PERSISTED_TIME = Instant.parse("-4712-01-01T00:00:00Z")
        private val MAX_PERSISTED_TIME = Instant.parse("+294276-12-31T23:59:59.999999Z")
    }
}

data class Announcement(
    val tournamentId: Long,
    val content: AnnouncementContent,
    val published: Boolean,
    val version: Long,
    val publishedAt: Instant
)
