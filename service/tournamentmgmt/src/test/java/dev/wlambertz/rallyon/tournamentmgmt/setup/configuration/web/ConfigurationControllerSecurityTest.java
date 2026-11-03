package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.web;

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.ConfigurationService;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Tournament;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.TournamentStatus;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Visibility;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.DraftUpdateConflictException;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.InvalidDraftUpdateException;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.internal.TournamentNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "rallyon.security.keycloak.issuer-uri=http://localhost:8081/realms/rallyon",
        "rallyon.security.keycloak.jwks-uri=http://localhost:8081/realms/rallyon/protocol/openid-connect/certs",
        "rallyon.security.keycloak.audience=rallyon-api"
})
@AutoConfigureMockMvc
class ConfigurationControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConfigurationService configurationService;

    private final ObjectMapper objectMapper = JsonMapper.builder()
            .findAndAddModules()
            .build();

    @Test
    void rejectsAnonymousRequests() throws Exception {
        mockMvc.perform(
                post("/api/tournamentmgmt/config/drafts")
                        .param("organizerId", "5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Test Cup",
                                  "visibility": "PUBLIC"
                                }
                                """)
        ).andExpect(status().isUnauthorized());
    }

    @Test
    void acceptsOrganizerRole() throws Exception {
        when(configurationService.createDraft(eq(5L), eq("Test Cup"), eq(Visibility.PUBLIC), anyLong()))
                .thenReturn(Tournament.builder().id(123L).build());

        mockMvc.perform(
                post("/api/tournamentmgmt/config/drafts")
                        .param("organizerId", "5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Test Cup",
                                  "visibility": "PUBLIC"
                                }
                                """)
                        .with(jwt().jwt(jwt -> jwt
                                .issuer("http://localhost:8081/realms/rallyon")
                                .audience(List.of("rallyon-api"))
                                .claim("sub", "42")
                                .claim("rallyon_user_id", 42)
                                .claim("realm_access", Map.of("roles", List.of("rallyon-organizer")))
                        ))
        ).andExpect(status().isCreated());

        verify(configurationService).createDraft(eq(5L), eq("Test Cup"), eq(Visibility.PUBLIC), eq(42L));
    }

    @Test
    void retrievesExistingConfigurationInThePublishedRepresentation() throws Exception {
        Tournament stored = Tournament.builder()
                .id(123L)
                .version(3L)
                .organizerId(5L)
                .name("Saved Cup")
                .visibility(Visibility.PUBLIC)
                .status(TournamentStatus.DRAFT)
                .createdAt(Instant.parse("2026-01-01T10:00:00Z"))
                .createdByUserId(42L)
                .lastModifiedAt(Instant.parse("2026-01-02T10:00:00Z"))
                .lastModifiedByUserId(42L)
                .build();
        when(configurationService.get(123L)).thenReturn(stored);

        mockMvc.perform(get("/api/tournamentmgmt/config/123").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                        {
                          "id": 123,
                          "version": 3,
                          "organizerId": 5,
                          "name": "Saved Cup",
                          "visibility": "PUBLIC",
                          "status": "DRAFT",
                          "createdAt": "2026-01-01T10:00:00Z",
                          "createdByUserId": 42,
                          "lastModifiedAt": "2026-01-02T10:00:00Z",
                          "lastModifiedByUserId": 42,
                          "description": null,
                          "schedule": null,
                          "venue": null,
                          "courts": null,
                          "disciplines": null,
                          "bracketRosters": null
                        }
                        """));

        verify(configurationService).get(123L);
    }

    @Test
    void mapsMissingConfigurationsToNotFoundProblemDetail() throws Exception {
        when(configurationService.get(123L)).thenThrow(new TournamentNotFoundException(123L));

        mockMvc.perform(get("/api/tournamentmgmt/config/123").with(jwt()))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.detail").value("Tournament 123 was not found"));

        verify(configurationService).get(123L);
    }

    @Test
    void rejectsAnonymousConfigurationReadsBeforeRetrieval() throws Exception {
        mockMvc.perform(get("/api/tournamentmgmt/config/123"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(configurationService);
    }

    @Test
    void permitsNonOrganizerToReadAnotherCreatorsPrivateConfiguration() throws Exception {
        when(configurationService.get(123L)).thenReturn(Tournament.builder()
                .id(123L)
                .visibility(Visibility.PRIVATE)
                .createdByUserId(99L)
                .build());

        // jwt() supplies authentication directly; this checks authorization, not token validation.
        mockMvc.perform(get("/api/tournamentmgmt/config/123")
                        .with(jwt().jwt(jwt -> jwt
                                .subject("42")
                                .claim("rallyon_user_id", 42)
                                .claim("realm_access", Map.of("roles", List.of("rallyon-audience"))))
                                .authorities(List.of())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(123))
                .andExpect(jsonPath("$.visibility").value("PRIVATE"))
                .andExpect(jsonPath("$.createdByUserId").value(99));

        verify(configurationService).get(123L);
    }

    @Test
    void doesNotRequireNumericUserIdForAuthenticatedConfigurationReads() throws Exception {
        when(configurationService.get(123L)).thenReturn(Tournament.builder().id(123L).build());

        mockMvc.perform(get("/api/tournamentmgmt/config/123")
                        .with(jwt().jwt(jwt -> jwt.subject("external-subject")).authorities(List.of())))
                .andExpect(status().isOk());

        verify(configurationService).get(123L);
    }

    @Test
    void rejectsMalformedBearerTokenBeforeConfigurationRetrieval() throws Exception {
        // The real decoder rejects malformed input without fetching JWKS; issuer/audience
        // and signature validation require separate shared IAM and real-token evidence.
        mockMvc.perform(get("/api/tournamentmgmt/config/123")
                        .header("Authorization", "Bearer not-a-jwt"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(configurationService);
    }

    @Test
    void rejectsAnonymousUpdateRequests() throws Exception {
        mockMvc.perform(
                put("/api/tournamentmgmt/config/123/draft")
                        .header("If-Match", "3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateDraftBody())
        ).andExpect(status().isUnauthorized());
    }

    @Test
    void updatesDraftForAuthenticatedOrganizer() throws Exception {
        when(configurationService.updateDraft(eq(123L), org.mockito.ArgumentMatchers.any(Tournament.class), eq(3L), anyLong()))
                .thenReturn(Tournament.builder().id(123L).build());

        mockMvc.perform(
                put("/api/tournamentmgmt/config/123/draft")
                        .header("If-Match", "3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateDraftBody())
                        .with(jwt().jwt(jwt -> jwt
                                .issuer("http://localhost:8081/realms/rallyon")
                                .audience(List.of("rallyon-api"))
                                .claim("sub", "42")
                                .claim("rallyon_user_id", 42)
                                .claim("realm_access", Map.of("roles", List.of("rallyon-organizer")))
                        ))
        ).andExpect(status().isOk());

        verify(configurationService).updateDraft(eq(123L), org.mockito.ArgumentMatchers.any(Tournament.class), eq(3L), eq(42L));
    }

    @Test
    void mapsInvalidDraftUpdatesToBadRequest() throws Exception {
        when(configurationService.updateDraft(eq(123L), org.mockito.ArgumentMatchers.any(Tournament.class), eq(3L), anyLong()))
                .thenThrow(new InvalidDraftUpdateException("Tournament name must not be blank"));

        mockMvc.perform(authenticatedUpdateRequest())
                .andExpect(status().isBadRequest());
    }

    @Test
    void mapsMissingDraftsToNotFound() throws Exception {
        when(configurationService.updateDraft(eq(123L), org.mockito.ArgumentMatchers.any(Tournament.class), eq(3L), anyLong()))
                .thenThrow(new TournamentNotFoundException(123L));

        mockMvc.perform(authenticatedUpdateRequest())
                .andExpect(status().isNotFound());
    }

    @Test
    void mapsDraftConflictsToConflict() throws Exception {
        when(configurationService.updateDraft(eq(123L), org.mockito.ArgumentMatchers.any(Tournament.class), eq(3L), anyLong()))
                .thenThrow(new DraftUpdateConflictException("Version mismatch"));

        mockMvc.perform(authenticatedUpdateRequest())
                .andExpect(status().isConflict());
    }

    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder authenticatedUpdateRequest()
            throws Exception {
        return put("/api/tournamentmgmt/config/123/draft")
                .header("If-Match", "3")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateDraftBody())
                .with(jwt().jwt(jwt -> jwt
                        .issuer("http://localhost:8081/realms/rallyon")
                        .audience(List.of("rallyon-api"))
                        .claim("sub", "42")
                        .claim("rallyon_user_id", 42)
                        .claim("realm_access", Map.of("roles", List.of("rallyon-organizer")))
                ));
    }

    private String updateDraftBody() throws Exception {
        return objectMapper.writeValueAsString(Tournament.builder()
                .visibility(Visibility.PUBLIC)
                .name("Updated Cup")
                .description("Fresh description")
                .registrationWindows(List.of())
                .courts(List.of())
                .disciplines(List.of())
                .bracketRosters(Map.of())
                .phases(List.of())
                .build());
    }
}
