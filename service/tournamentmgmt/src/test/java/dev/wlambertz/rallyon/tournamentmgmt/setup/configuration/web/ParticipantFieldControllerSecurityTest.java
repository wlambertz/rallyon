package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.web;

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.CapacityReview;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.EntryPlayer;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.EntrySelection;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.FieldFreshness;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.ParticipantFieldAudit;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.ParticipantFieldConflict;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.ParticipantFieldForbidden;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.ParticipantFieldInvalid;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.ParticipantFieldNotFound;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.ParticipantFieldRequest;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.ParticipantFieldService;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.ParticipantFieldUnit;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.ParticipantFieldView;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.TeamSize;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ParticipantFieldControllerSecurityTest {
    private static final String RESOURCE = "/api/tournamentmgmt/participant-fields/123";
    private static final String INPUT = """
            {"disciplineId":1,"bracketId":"open","entryIds":[8],
             "sourceTournamentVersion":4,"sourceEntryBookVersion":17}
            """;
    private static final String PRIVATE_DETAIL = "Private Player / SQL internals";

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private ParticipantFieldService service;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    private RequestPostProcessor actor(long id) {
        return jwt().jwt(j -> j.claim("rallyon_user_id", id)
                .claim("realm_access", Map.of("roles", List.of("rallyon-organizer"))));
    }

    private MockHttpServletRequestBuilder read() {
        return get(RESOURCE).param("disciplineId", "1").param("bracketId", "open");
    }

    private MockHttpServletRequestBuilder prepare() {
        return put(RESOURCE).header("If-Match", "2").contentType(MediaType.APPLICATION_JSON).content(INPUT);
    }

    private String inputWith(String field, String rawJsonValue) {
        Map<String, String> values = new LinkedHashMap<>(Map.of(
                "disciplineId", "1", "bracketId", "\"open\"", "entryIds", "[8]",
                "sourceTournamentVersion", "4", "sourceEntryBookVersion", "17"));
        if (rawJsonValue == null) values.remove(field);
        else values.put(field, rawJsonValue);
        return values.entrySet().stream().map(entry -> "\"" + entry.getKey() + "\":" + entry.getValue())
                .collect(Collectors.joining(",", "{", "}"));
    }

    private ParticipantFieldView field(FieldFreshness freshness) {
        EntrySelection original = new EntrySelection(1, "Original Singles", "open", "Original Open", TeamSize.SINGLES);
        EntrySelection current = new EntrySelection(1, "Singles", "open", "Open", TeamSize.SINGLES);
        return new ParticipantFieldView(123, 1, "open", true, 3, current, 4L, 17L,
                List.of(new ParticipantFieldUnit(8, original, List.of(new EntryPlayer("p1", "Private Player")))),
                List.of(new ParticipantFieldAudit(3, 42, Instant.parse("2026-11-11T10:00:00Z"),
                        "PREPARE", 1, 4, 17)),
                freshness == FieldFreshness.OUTDATED ? 5 : 4, 17, freshness, CapacityReview.MANUAL);
    }

    @Test
    void bothOperationsRequireAuthentication() throws Exception {
        mvc.perform(read()).andExpect(status().isUnauthorized())
                .andExpect(header().string("Cache-Control", containsString("no-store")));
        mvc.perform(prepare()).andExpect(status().isUnauthorized())
                .andExpect(header().string("Cache-Control", containsString("no-store")));
        verifyNoInteractions(service);
    }

    @Test
    void privateReadPassesSelectorsAndActorAndRetainsBothProvenances() throws Exception {
        when(service.get(123, 1, "open", 42)).thenReturn(field(FieldFreshness.CURRENT));
        mvc.perform(read().with(actor(42)))
                .andExpect(status().isOk()).andExpect(header().string("Cache-Control", containsString("no-store")))
                .andExpect(jsonPath("$.version").value(3))
                .andExpect(jsonPath("$.selection.bracketName").value("Open"))
                .andExpect(jsonPath("$.units[0].selection.bracketName").value("Original Open"))
                .andExpect(jsonPath("$.units[0].players[0].displayName").value("Private Player"))
                .andExpect(jsonPath("$.audit[0].actorId").value(42))
                .andExpect(jsonPath("$.capacityReview").value("MANUAL"));
        verify(service).get(123, 1, "open", 42);
    }

    @Test
    void preparationPassesIndependentFieldAndSourceVersions() throws Exception {
        when(service.prepare(eq(123L), any(ParticipantFieldRequest.class), eq(2L), eq(42L)))
                .thenReturn(field(FieldFreshness.CURRENT));
        mvc.perform(prepare().with(actor(42))).andExpect(status().isOk())
                .andExpect(header().string("Cache-Control", containsString("no-store")))
                .andExpect(jsonPath("$.version").value(3))
                .andExpect(jsonPath("$.sourceTournamentVersion").value(4))
                .andExpect(jsonPath("$.sourceEntryBookVersion").value(17))
                .andExpect(jsonPath("$.freshness").value("CURRENT"));
        verify(service).prepare(eq(123L), argThat(input -> input.getDisciplineId() == 1
                && input.getBracketId().equals("open") && input.getEntryIds().equals(List.of(8L))
                && input.getSourceTournamentVersion() == 4 && input.getSourceEntryBookVersion() == 17),
                eq(2L), eq(42L));
    }

    @Test
    void unpreparedAndRetainedOutdatedFieldsAreSuccessfulPrivateReads() throws Exception {
        ParticipantFieldView unprepared = new ParticipantFieldView(123, 1, "open", false, 0,
                null, null, null, List.of(), List.of(), 4, 0, FieldFreshness.UNPREPARED, CapacityReview.MANUAL);
        when(service.get(123, 1, "open", 42)).thenReturn(unprepared, field(FieldFreshness.OUTDATED));
        mvc.perform(read().with(actor(42))).andExpect(status().isOk())
                .andExpect(jsonPath("$.prepared").value(false)).andExpect(jsonPath("$.version").value(0))
                .andExpect(jsonPath("$.units").isEmpty()).andExpect(jsonPath("$.audit").isEmpty())
                .andExpect(jsonPath("$.freshness").value("UNPREPARED"));
        mvc.perform(read().with(actor(42))).andExpect(status().isOk())
                .andExpect(header().string("Cache-Control", containsString("no-store")))
                .andExpect(jsonPath("$.prepared").value(true)).andExpect(jsonPath("$.version").value(3))
                .andExpect(jsonPath("$.observedTournamentVersion").value(5))
                .andExpect(jsonPath("$.freshness").value("OUTDATED"))
                .andExpect(jsonPath("$.units[0].entryId").value(8));
    }

    @Test
    void explicitEmptySelectionAndNonPathSafeBracketAreMappedWithoutInventingMeaning() throws Exception {
        String bracket = "Open / A";
        String input = """
                {"disciplineId":1,"bracketId":"Open / A","entryIds":[],
                 "sourceTournamentVersion":4,"sourceEntryBookVersion":17}
                """;
        when(service.get(123, 1, bracket, 42)).thenReturn(field(FieldFreshness.CURRENT));
        when(service.prepare(eq(123L), any(ParticipantFieldRequest.class), eq(2L), eq(42L)))
                .thenReturn(field(FieldFreshness.CURRENT));
        mvc.perform(get(RESOURCE).param("disciplineId", "1").param("bracketId", bracket).with(actor(42)))
                .andExpect(status().isOk());
        mvc.perform(put(RESOURCE).with(actor(42)).header("If-Match", "2")
                .contentType(MediaType.APPLICATION_JSON).content(input)).andExpect(status().isOk());
        verify(service).get(123, 1, bracket, 42);
        verify(service).prepare(eq(123L), argThat(request -> request.getBracketId().equals(bracket)
                && request.getEntryIds().isEmpty()), eq(2L), eq(42L));
    }

    @Test
    void missingAndNonpositiveIdentityCannotCallService() throws Exception {
        RequestPostProcessor missing = jwt().jwt(j -> j.claim("realm_access",
                Map.of("roles", List.of("rallyon-organizer"))));
        mvc.perform(read().with(missing)).andExpect(status().isForbidden());
        mvc.perform(prepare().with(missing)).andExpect(status().isForbidden());
        for (long id : List.of(0L, -1L)) {
            mvc.perform(read().with(actor(id))).andExpect(status().isForbidden());
            mvc.perform(prepare().with(actor(id))).andExpect(status().isForbidden());
        }
        verifyNoInteractions(service);
    }

    @Test
    void foreignActorErrorsAreSanitizedForReadAndPreparation() throws Exception {
        when(service.get(123, 1, "open", 99)).thenThrow(new ParticipantFieldForbidden(PRIVATE_DETAIL));
        when(service.prepare(eq(123L), any(ParticipantFieldRequest.class), eq(2L), eq(99L)))
                .thenThrow(new ParticipantFieldForbidden(PRIVATE_DETAIL));
        mvc.perform(read().with(actor(99))).andExpect(status().isForbidden())
                .andExpect(header().string("Cache-Control", containsString("no-store")))
                .andExpect(content().string(not(containsString(PRIVATE_DETAIL))));
        mvc.perform(prepare().with(actor(99))).andExpect(status().isForbidden())
                .andExpect(header().string("Cache-Control", containsString("no-store")))
                .andExpect(content().string(not(containsString(PRIVATE_DETAIL))));
    }

    @Test
    void malformedSelectorsHeadersAndBodiesArePrivateBadRequestsBeforeService() throws Exception {
        List<MockHttpServletRequestBuilder> malformed = List.of(
                get(RESOURCE).param("bracketId", "open"),
                get(RESOURCE).param("disciplineId", "1"),
                get(RESOURCE).param("disciplineId", "not-a-number").param("bracketId", "open"),
                put(RESOURCE).contentType(MediaType.APPLICATION_JSON).content(INPUT),
                put(RESOURCE).header("If-Match", "not-a-number").contentType(MediaType.APPLICATION_JSON).content(INPUT),
                put(RESOURCE).header("If-Match", "9223372036854775808").contentType(MediaType.APPLICATION_JSON).content(INPUT),
                put(RESOURCE).header("If-Match", "2").contentType(MediaType.APPLICATION_JSON).content("{"),
                put(RESOURCE).header("If-Match", "2").contentType(MediaType.APPLICATION_JSON).content("null"));
        for (MockHttpServletRequestBuilder request : malformed) {
            mvc.perform(request.with(actor(42))).andExpect(status().isBadRequest())
                    .andExpect(header().string("Cache-Control", containsString("no-store")))
                    .andExpect(content().string(not(containsString("Private Player"))));
        }
        verifyNoInteractions(service);
    }

    @Test
    void invalidSourceInputAndNulSelectorMapToSanitizedBadRequests() throws Exception {
        when(service.get(123, 1, "\u0000", 42)).thenThrow(new ParticipantFieldInvalid(PRIVATE_DETAIL));
        when(service.prepare(eq(123L), any(ParticipantFieldRequest.class), eq(2L), eq(42L)))
                .thenThrow(new ParticipantFieldInvalid(PRIVATE_DETAIL));
        mvc.perform(get(RESOURCE).param("disciplineId", "1").param("bracketId", "\u0000").with(actor(42)))
                .andExpect(status().isBadRequest()).andExpect(header().string("Cache-Control", containsString("no-store")))
                .andExpect(content().string(not(containsString(PRIVATE_DETAIL))));
        mvc.perform(prepare().with(actor(42)))
                .andExpect(status().isBadRequest()).andExpect(header().string("Cache-Control", containsString("no-store")))
                .andExpect(content().string(not(containsString(PRIVATE_DETAIL))));
    }

    @Test
    void numericRequestFieldsRequireIntegerTokensWithoutCoercion() throws Exception {
        for (String field : List.of("disciplineId", "sourceTournamentVersion", "sourceEntryBookVersion")) {
            for (String token : List.of("1.9", "\"1\"", "1e0", "1.0", "9223372036854775808",
                    "-9223372036854775809", "true", "{}", "[]")) {
                mvc.perform(prepare().with(actor(42)).content(inputWith(field, token)))
                        .andExpect(status().isBadRequest())
                        .andExpect(header().string("Cache-Control", containsString("no-store")));
            }
        }
        verifyNoInteractions(service);
    }

    @Test
    void entryIdsRequireAnArrayOfIntegerTokensWithoutCoercion() throws Exception {
        for (String token : List.of("1.9", "\"1\"", "1e0", "1.0", "9223372036854775808",
                "-9223372036854775809", "true", "{}", "[]", "null")) {
            mvc.perform(prepare().with(actor(42)).content(inputWith("entryIds", "[8," + token + "]")))
                    .andExpect(status().isBadRequest())
                    .andExpect(header().string("Cache-Control", containsString("no-store")));
        }
        for (String token : List.of("8", "\"8\"", "true", "{}")) {
            mvc.perform(prepare().with(actor(42)).content(inputWith("entryIds", token)))
                    .andExpect(status().isBadRequest())
                    .andExpect(header().string("Cache-Control", containsString("no-store")));
        }
        verifyNoInteractions(service);
    }

    @Test
    void requiredBodyFieldsRejectMissingAndNullValuesBeforeService() throws Exception {
        for (String field : List.of("disciplineId", "bracketId", "entryIds", "sourceTournamentVersion",
                "sourceEntryBookVersion")) {
            for (String token : new String[]{null, "null"}) {
                mvc.perform(prepare().with(actor(42)).content(inputWith(field, token)))
                        .andExpect(status().isBadRequest())
                        .andExpect(header().string("Cache-Control", containsString("no-store")));
            }
        }
        for (String token : List.of("1", "true", "{}", "[]")) {
            mvc.perform(prepare().with(actor(42)).content(inputWith("bracketId", token)))
                    .andExpect(status().isBadRequest())
                    .andExpect(header().string("Cache-Control", containsString("no-store")));
        }
        for (String body : List.of("[]", "true", "1", "\"request\"")) {
            mvc.perform(prepare().with(actor(42)).content(body))
                    .andExpect(status().isBadRequest())
                    .andExpect(header().string("Cache-Control", containsString("no-store")));
        }
        verifyNoInteractions(service);
    }

    @Test
    void integralLongBoundaryTokensAreMappedWithoutNarrowing() throws Exception {
        String body = """
                {"disciplineId":9223372036854775807,"bracketId":"open","entryIds":[9223372036854775807],
                 "sourceTournamentVersion":9223372036854775807,"sourceEntryBookVersion":9223372036854775807}
                """;
        when(service.prepare(eq(123L), any(ParticipantFieldRequest.class), eq(2L), eq(42L)))
                .thenReturn(field(FieldFreshness.CURRENT));
        mvc.perform(prepare().with(actor(42)).content(body)).andExpect(status().isOk());
        verify(service).prepare(eq(123L), argThat(input -> input.getDisciplineId() == Long.MAX_VALUE
                && input.getEntryIds().equals(List.of(Long.MAX_VALUE))
                && input.getSourceTournamentVersion() == Long.MAX_VALUE
                && input.getSourceEntryBookVersion() == Long.MAX_VALUE), eq(2L), eq(42L));
    }

    @Test
    void unknownTargetAndStalePreparationHaveDistinctPrivateOutcomes() throws Exception {
        when(service.get(123, 1, "open", 42)).thenThrow(new ParticipantFieldNotFound(PRIVATE_DETAIL));
        when(service.prepare(eq(123L), any(ParticipantFieldRequest.class), eq(2L), eq(42L)))
                .thenThrow(new ParticipantFieldConflict(PRIVATE_DETAIL));
        mvc.perform(read().with(actor(42))).andExpect(status().isNotFound())
                .andExpect(header().string("Cache-Control", containsString("no-store")))
                .andExpect(content().string(not(containsString(PRIVATE_DETAIL))));
        mvc.perform(prepare().with(actor(42))).andExpect(status().isConflict())
                .andExpect(header().string("Cache-Control", containsString("no-store")))
                .andExpect(content().string(not(containsString(PRIVATE_DETAIL))));
    }

    @Test
    void invalidBearerTokensRemainUnauthorized() throws Exception {
        when(jwtDecoder.decode("invalid-field-token")).thenThrow(new BadJwtException("invalid"));
        mvc.perform(read().header("Authorization", "Bearer invalid-field-token"))
                .andExpect(status().isUnauthorized());
        mvc.perform(prepare().header("Authorization", "Bearer invalid-field-token"))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(service);
    }
}
