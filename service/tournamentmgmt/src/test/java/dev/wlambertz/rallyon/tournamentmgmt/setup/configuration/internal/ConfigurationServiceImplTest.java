package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal;

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Tournament;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Visibility;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.mapping.TournamentApiMapping;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.usecase.CreateDraftUseCase;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.usecase.GetTournamentUseCase;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.usecase.UpdateDraftUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfigurationServiceImplTest {

    @Mock
    private CreateDraftUseCase createDraftUseCase;

    @Mock
    private UpdateDraftUseCase updateDraftUseCase;

    @Mock
    private GetTournamentUseCase getTournamentUseCase;

    private ConfigurationServiceImpl configurationService;

    @BeforeEach
    void setUp() {
        configurationService = new ConfigurationServiceImpl(createDraftUseCase, updateDraftUseCase, getTournamentUseCase);
    }

    @Test
    void createDraftDelegatesToUseCase() {
        long organizerId = 10L;
        String name = "RallyOn Invitational";
        Visibility visibility = Visibility.PUBLIC;
        long actingUserId = 99L;

        Tournament expected = Tournament.builder().id(42L).build();
        when(createDraftUseCase.execute(organizerId, name, TournamentApiMapping.toDomain(visibility), actingUserId)).thenReturn(TournamentApiMapping.toDomain(expected));

        Tournament result = configurationService.createDraft(organizerId, name, visibility, actingUserId);

        assertEquals(expected, result);
        verify(createDraftUseCase).execute(organizerId, name, TournamentApiMapping.toDomain(visibility), actingUserId);
    }

    @Test
    void createDraftRejectsNullName() {
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> configurationService.createDraft(1L, null, Visibility.PUBLIC, 2L)
        );

        verifyNoInteractions(createDraftUseCase);
        assertEquals("Tournament name must not be null", exception.getMessage());
    }

    @Test
    void createDraftRejectsNullVisibility() {
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> configurationService.createDraft(1L, "Name", null, 2L)
        );

        verifyNoInteractions(createDraftUseCase);
        assertEquals("Visibility must not be null", exception.getMessage());
    }

    @Test
    void updateDraftDelegatesToUseCase() {
        Tournament draftChanges = Tournament.builder().name("Updated Cup").visibility(Visibility.PUBLIC).build();
        Tournament expected = Tournament.builder().id(42L).build();

        when(updateDraftUseCase.execute(42L, TournamentApiMapping.toDomain(draftChanges), 3L, 99L)).thenReturn(TournamentApiMapping.toDomain(expected));

        Tournament result = configurationService.updateDraft(42L, draftChanges, 3L, 99L);

        assertEquals(expected, result);
        verify(updateDraftUseCase).execute(42L, TournamentApiMapping.toDomain(draftChanges), 3L, 99L);
    }

    @Test
    void updateDraftRejectsNullPayload() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> configurationService.updateDraft(42L, null, 3L, 99L)
        );

        verifyNoInteractions(updateDraftUseCase);
        assertEquals("Tournament draft changes must not be null", exception.getMessage());
    }

    @Test
    void getMapsTheStoredSnapshotWithoutCallingWriteUseCases() {
        Tournament expected = Tournament.builder().id(42L).version(3L)
                .name("Saved Cup").visibility(Visibility.PRIVATE).createdByUserId(12L).build();
        when(getTournamentUseCase.execute(42L)).thenReturn(TournamentApiMapping.toDomain(expected));

        assertEquals(expected, configurationService.get(42L));
        verify(getTournamentUseCase).execute(42L);
        verifyNoInteractions(createDraftUseCase, updateDraftUseCase);
    }

    @Test
    void getTranslatesTheDomainNotFoundException() {
        when(getTournamentUseCase.execute(42L)).thenThrow(
                new dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.tournament.domain.TournamentNotFoundException(42L));

        TournamentNotFoundException error = assertThrows(TournamentNotFoundException.class,
                () -> configurationService.get(42L));

        assertEquals("Tournament 42 was not found", error.getMessage());
        verifyNoInteractions(createDraftUseCase, updateDraftUseCase);
    }
}
