package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.usecase;

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Tournament;

public interface UpdateDraftUseCase {

    Tournament execute(long tournamentId, Tournament draftChanges, long version, long actingUserId);
}
