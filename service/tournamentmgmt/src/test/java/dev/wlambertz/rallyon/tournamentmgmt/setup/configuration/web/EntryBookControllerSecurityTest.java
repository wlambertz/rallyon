package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.web;

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class EntryBookControllerSecurityTest {
    private static final String RESOURCE = "/api/tournamentmgmt/entries/123";
    private static final String INPUT = """
            {"disciplineId":1,"bracketId":"open","players":[{"key":"p1","displayName":"Player One"}]}
            """;

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private EntryBookService service;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    private RequestPostProcessor actor(long id) {
        return jwt().jwt(j -> j.claim("rallyon_user_id", id)
                .claim("realm_access", Map.of("roles", List.of("rallyon-organizer"))));
    }

    private EntryBookView book() {
        return new EntryBookView(123, true, 7,
                List.of(new EntryRecord(8, new EntrySelection(1, "Singles", "open", "Open", TeamSize.SINGLES),
                        List.of(new EntryPlayer("p1", "Private Player")), EntryState.RECEIVED)),
                List.of(new EntryAudit(8L, "CREATE", null, EntryState.RECEIVED, 42,
                        Instant.parse("2026-11-11T10:00:00Z"))));
    }

    @Test
    void allEntryOperationsRejectAnonymousAccess() throws Exception {
        mvc.perform(get(RESOURCE)).andExpect(status().isUnauthorized());
        mvc.perform(put(RESOURCE + "/intake").header("If-Match", "0")
                .contentType(MediaType.APPLICATION_JSON).content("{\"open\":true}"))
                .andExpect(status().isUnauthorized());
        mvc.perform(post(RESOURCE + "/records").header("If-Match", "0")
                .contentType(MediaType.APPLICATION_JSON).content(INPUT)).andExpect(status().isUnauthorized());
        mvc.perform(put(RESOURCE + "/records/8").header("If-Match", "0")
                .contentType(MediaType.APPLICATION_JSON).content(INPUT)).andExpect(status().isUnauthorized());
        mvc.perform(put(RESOURCE + "/records/8/decision").header("If-Match", "0")
                .contentType(MediaType.APPLICATION_JSON).content("{\"state\":\"ACCEPTED\"}"))
                .andExpect(status().isUnauthorized());
        mvc.perform(delete(RESOURCE + "/records/8").header("If-Match", "0"))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(service);
    }

    @Test
    void privateReadCarriesActorAndRequestsNoCaching() throws Exception {
        when(service.get(123, 42)).thenReturn(book());
        mvc.perform(get(RESOURCE).with(actor(42)))
                .andExpect(status().isOk()).andExpect(header().string("Cache-Control", containsString("no-store")))
                .andExpect(jsonPath("$.version").value(7))
                .andExpect(jsonPath("$.records[0].players[0].displayName").value("Private Player"))
                .andExpect(jsonPath("$.audit[0].actorId").value(42));
        verify(service).get(123, 42);
    }

    @Test
    void allMutationsPassTheBookVersionAndActorAndRequestNoCaching() throws Exception {
        when(service.intake(123, true, 6, 42)).thenReturn(book());
        when(service.create(eq(123L), any(EntryInput.class), eq(6L), eq(42L))).thenReturn(book());
        when(service.correct(eq(123L), eq(8L), any(EntryInput.class), eq(6L), eq(42L))).thenReturn(book());
        when(service.decide(123, 8, EntryState.ACCEPTED, 6, 42)).thenReturn(book());
        when(service.withdraw(123, 8, 6, 42)).thenReturn(book());
        mvc.perform(put(RESOURCE + "/intake").with(actor(42)).header("If-Match", "6")
                .contentType(MediaType.APPLICATION_JSON).content("{\"open\":true}"))
                .andExpect(status().isOk()).andExpect(header().string("Cache-Control", containsString("no-store")));
        mvc.perform(post(RESOURCE + "/records").with(actor(42)).header("If-Match", "6")
                .contentType(MediaType.APPLICATION_JSON).content(INPUT))
                .andExpect(status().isCreated()).andExpect(header().string("Cache-Control", containsString("no-store")));
        mvc.perform(put(RESOURCE + "/records/8").with(actor(42)).header("If-Match", "6")
                .contentType(MediaType.APPLICATION_JSON).content(INPUT))
                .andExpect(status().isOk()).andExpect(header().string("Cache-Control", containsString("no-store")));
        mvc.perform(put(RESOURCE + "/records/8/decision").with(actor(42)).header("If-Match", "6")
                .contentType(MediaType.APPLICATION_JSON).content("{\"state\":\"ACCEPTED\"}"))
                .andExpect(status().isOk()).andExpect(header().string("Cache-Control", containsString("no-store")));
        mvc.perform(delete(RESOURCE + "/records/8").with(actor(42)).header("If-Match", "6"))
                .andExpect(status().isOk()).andExpect(header().string("Cache-Control", containsString("no-store")));
        verify(service).create(eq(123L), argThat(i -> i.getPlayers().getFirst().getKey().equals("p1")), eq(6L), eq(42L));
        verify(service).correct(eq(123L), eq(8L), any(EntryInput.class), eq(6L), eq(42L));
        verify(service).intake(123, true, 6, 42);
        verify(service).decide(123, 8, EntryState.ACCEPTED, 6, 42);
        verify(service).withdraw(123, 8, 6, 42);
    }

    @Test
    void malformedPreconditionsAndRequiredDecisionsDoNotCallService() throws Exception {
        mvc.perform(post(RESOURCE + "/records").with(actor(42)).contentType(MediaType.APPLICATION_JSON).content(INPUT))
                .andExpect(status().isBadRequest()).andExpect(header().string("Cache-Control", containsString("no-store")));
        mvc.perform(delete(RESOURCE + "/records/8").with(actor(42)).header("If-Match", "not-a-version"))
                .andExpect(status().isBadRequest());
        mvc.perform(put(RESOURCE + "/intake").with(actor(42)).header("If-Match", "0")
                .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
        mvc.perform(put(RESOURCE + "/records/8/decision").with(actor(42)).header("If-Match", "0")
                .contentType(MediaType.APPLICATION_JSON).content("{\"state\":null}"))
                .andExpect(status().isBadRequest());
        mvc.perform(put(RESOURCE + "/records/8/decision").with(actor(42)).header("If-Match", "0")
                .contentType(MediaType.APPLICATION_JSON).content("{\"state\":\"UNKNOWN\"}"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }

    @Test
    void missingOrNonpositiveIdentityIsRejectedBeforeService() throws Exception {
        mvc.perform(get(RESOURCE).with(jwt().jwt(j -> j.claim("realm_access", Map.of("roles", List.of("rallyon-organizer"))))))
                .andExpect(status().isForbidden());
        mvc.perform(get(RESOURCE).with(actor(0))).andExpect(status().isForbidden());
        mvc.perform(get(RESOURCE).with(actor(-1))).andExpect(status().isForbidden());
        verifyNoInteractions(service);
    }

    @Test
    void foreignUserCannotSeeEntryContentOnReadOrMutation() throws Exception {
        when(service.get(123, 99)).thenThrow(new EntryForbidden("Private Player"));
        when(service.withdraw(123, 8, 6, 99)).thenThrow(new EntryForbidden("Private Player"));
        mvc.perform(get(RESOURCE).with(actor(99))).andExpect(status().isForbidden())
                .andExpect(content().string(not(containsString("Private Player"))))
                .andExpect(header().string("Cache-Control", containsString("no-store")));
        mvc.perform(delete(RESOURCE + "/records/8").with(actor(99)).header("If-Match", "6"))
                .andExpect(status().isForbidden()).andExpect(content().string(not(containsString("Private Player"))));
    }

    @Test
    void errorsMapToContractStatusesWithoutEntryPayloads() throws Exception {
        when(service.get(123, 42)).thenThrow(new EntryNotFound("Private Player"));
        mvc.perform(get(RESOURCE).with(actor(42))).andExpect(status().isNotFound())
                .andExpect(content().string(not(containsString("Private Player"))));
        when(service.create(eq(123L), any(EntryInput.class), eq(6L), eq(42L))).thenThrow(new EntryInvalid("Invalid entry"));
        mvc.perform(post(RESOURCE + "/records").with(actor(42)).header("If-Match", "6")
                .contentType(MediaType.APPLICATION_JSON).content(INPUT)).andExpect(status().isBadRequest());
        when(service.correct(eq(123L), eq(900L), any(EntryInput.class), eq(6L), eq(42L)))
                .thenThrow(new EntryNotFound("Missing"));
        mvc.perform(put(RESOURCE + "/records/900").with(actor(42)).header("If-Match", "6")
                .contentType(MediaType.APPLICATION_JSON).content(INPUT)).andExpect(status().isNotFound());
        when(service.withdraw(123, 8, 6, 42)).thenThrow(new EntryConflict("Stale version"));
        mvc.perform(delete(RESOURCE + "/records/8").with(actor(42)).header("If-Match", "6"))
                .andExpect(status().isConflict()).andExpect(header().string("Cache-Control", containsString("no-store")));
    }

    @Test
    void invalidBearerTokenRemainsUnauthorized() throws Exception {
        when(jwtDecoder.decode("invalid-entry-token")).thenThrow(new BadJwtException("invalid"));
        mvc.perform(get(RESOURCE).header("Authorization", "Bearer invalid-entry-token"))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(service);
    }
}
