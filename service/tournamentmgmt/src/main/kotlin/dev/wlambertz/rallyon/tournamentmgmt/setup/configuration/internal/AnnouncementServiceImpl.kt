package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.AnnouncementConflict
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.AnnouncementContent
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.AnnouncementForbidden
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.AnnouncementInvalid
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.AnnouncementNotFound
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.AnnouncementService
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.AnnouncementView
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.Announcement
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.AnnouncementContent as DomainContent
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.usecase.AnnouncementDenied
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.usecase.AnnouncementMissing
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.usecase.AnnouncementStore
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.usecase.AnnouncementUseCase
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.usecase.AnnouncementVersionConflict
import java.time.Clock
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AnnouncementServiceImpl(store: AnnouncementStore, tournamentClock: Clock) : AnnouncementService {
    private val useCase = AnnouncementUseCase(store, tournamentClock)

    @Transactional
    override fun create(id: Long, content: AnnouncementContent, actor: Long): AnnouncementView = translate {
        useCase.create(id, content.toDomain(), actor).toApi()
    }

    @Transactional
    override fun replace(id: Long, content: AnnouncementContent, version: Long, actor: Long): AnnouncementView = translate {
        useCase.replace(id, content.toDomain(), version, actor).toApi()
    }

    @Transactional
    override fun withdraw(id: Long, version: Long, actor: Long): AnnouncementView = translate {
        useCase.withdraw(id, version, actor).toApi()
    }

    @Transactional(readOnly = true)
    override fun management(id: Long, actor: Long): AnnouncementView = translate { useCase.management(id, actor).toApi() }

    @Transactional(readOnly = true)
    override fun publicRead(id: Long): AnnouncementView = translate { useCase.publicRead(id).toApi() }

    private fun AnnouncementContent.toDomain(): DomainContent = DomainContent(title, eventStart, eventEnd, venue, body)

    private fun Announcement.toApi(): AnnouncementView = AnnouncementView(
        tournamentId, AnnouncementContent(content.title, content.eventStart, content.eventEnd, content.venue, content.body),
        published, version, publishedAt
    )

    private fun <T> translate(operation: () -> T): T = try {
        operation()
    } catch (error: IllegalArgumentException) {
        throw AnnouncementInvalid(error.message ?: "Invalid announcement")
    } catch (_: AnnouncementDenied) {
        throw AnnouncementForbidden("Announcement access denied")
    } catch (_: AnnouncementMissing) {
        throw AnnouncementNotFound("Announcement or tournament not found")
    } catch (_: AnnouncementVersionConflict) {
        throw AnnouncementConflict("Announcement version conflict")
    }
}
