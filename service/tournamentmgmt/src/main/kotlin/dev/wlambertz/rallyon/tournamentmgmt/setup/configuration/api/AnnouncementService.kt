package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api

import java.time.Instant

data class AnnouncementContent(
    val title: String?,
    val eventStart: Instant?,
    val eventEnd: Instant?,
    val venue: String?,
    val body: String?
)

data class AnnouncementView(
    val tournamentId: Long,
    val content: AnnouncementContent,
    val published: Boolean,
    val version: Long,
    val publishedAt: Instant
)

interface AnnouncementService {
    fun create(id: Long, content: AnnouncementContent, actor: Long): AnnouncementView
    fun replace(id: Long, content: AnnouncementContent, version: Long, actor: Long): AnnouncementView
    fun withdraw(id: Long, version: Long, actor: Long): AnnouncementView
    fun management(id: Long, actor: Long): AnnouncementView
    fun publicRead(id: Long): AnnouncementView
}

class AnnouncementInvalid(message: String) : RuntimeException(message)
class AnnouncementForbidden(message: String) : RuntimeException(message)
class AnnouncementNotFound(message: String) : RuntimeException(message)
class AnnouncementConflict(message: String) : RuntimeException(message)
