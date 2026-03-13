package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.usecase;

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Tournament;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.TournamentStatus;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Visibility;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.DraftUpdateConflictException;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.InvalidDraftUpdateException;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.TournamentNotFoundException;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.entity.TournamentEntity;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.mapping.TournamentMapper;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.persistence.repository.TournamentRepository;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
class UpdateDraftService implements UpdateDraftUseCase {

    private final TournamentRepository tournamentRepository;
    private final TournamentMapper tournamentMapper;

    UpdateDraftService(TournamentRepository tournamentRepository, TournamentMapper tournamentMapper) {
        this.tournamentRepository = tournamentRepository;
        this.tournamentMapper = tournamentMapper;
    }

    @Override
    @Transactional
    public Tournament execute(long tournamentId, Tournament draftChanges, long version, long actingUserId) {
        Objects.requireNonNull(draftChanges, "Tournament draft changes must not be null");
        validateDraftChanges(draftChanges);

        TournamentEntity entity = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TournamentNotFoundException(tournamentId));

        if (entity.getStatus() != TournamentStatus.DRAFT) {
            throw new DraftUpdateConflictException("Only tournaments in DRAFT status can be updated");
        }
        if (entity.getVersion() == null || entity.getVersion() != version) {
            throw new DraftUpdateConflictException(
                    "Draft version mismatch: expected " + entity.getVersion() + " but got " + version);
        }

        Instant now = Instant.now();
        entity.setLastModifiedAt(now);
        entity.setLastModifiedByUserId(actingUserId);
        tournamentMapper.applyDraftReplacement(entity, draftChanges, actingUserId, now);

        return tournamentMapper.toApi(tournamentRepository.save(entity));
    }

    private static void validateDraftChanges(Tournament draftChanges) {
        validateName(draftChanges.name());
        if (draftChanges.visibility() == null) {
            throw new InvalidDraftUpdateException("Visibility must not be null");
        }
        List<?> phases = draftChanges.phases();
        if (phases != null && !phases.isEmpty()) {
            throw new InvalidDraftUpdateException("Phases are not supported by draft updates yet");
        }
    }

    private static void validateName(String name) {
        if (name == null) {
            throw new InvalidDraftUpdateException("Tournament name must not be null");
        }
        if (name.isBlank()) {
            throw new InvalidDraftUpdateException("Tournament name must not be blank");
        }
        if (name.length() > 200) {
            throw new InvalidDraftUpdateException("Tournament name must be <= 200 characters");
        }
    }
}
