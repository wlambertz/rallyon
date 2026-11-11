package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.web;

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.AnnouncementConflict;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.AnnouncementContent;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.AnnouncementForbidden;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.AnnouncementNotFound;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.AnnouncementService;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.AnnouncementView;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.head;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AnnouncementControllerSecurityTest {
    private static final String RESOURCE = "/api/tournamentmgmt/announcements/123";
    private static final String CONTENT = """
            {"title":"Club Cup","eventStart":"2026-10-24T08:00:00Z",
             "eventEnd":"2026-10-25T08:00:00Z","venue":"Sports Hall","body":"Entries open"}
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AnnouncementService announcementService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    private AnnouncementView announcement(boolean published) {
        return new AnnouncementView(123L,
                new AnnouncementContent("Club Cup", Instant.parse("2026-10-24T08:00:00Z"),
                        Instant.parse("2026-10-25T08:00:00Z"), "Sports Hall", "<script>alert(1)</script>"),
                published, 4L, Instant.parse("2026-10-01T10:00:00Z"));
    }

    @Test
    void allowsOnlyPublishedPublicPlainTextWithNoPrivateFields() throws Exception {
        when(announcementService.publicRead(123L)).thenReturn(announcement(true));
        mockMvc.perform(get(RESOURCE))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(header().string("Cache-Control", containsString("no-store")))
                .andExpect(header().string("X-Content-Type-Options", "nosniff"))
                .andExpect(content().string(containsString("<script>alert(1)</script>")))
                .andExpect(content().string(containsString("Club Cup")))
                .andExpect(content().string(not(containsString("tournamentId"))))
                .andExpect(content().string(not(containsString("createdByUserId"))))
                .andExpect(content().string(not(containsString("playerIds"))));
        verify(announcementService).publicRead(123L);
    }

    @Test
    void publicMissingAndWithdrawnResultsReturnNotFoundAndAreNotCached() throws Exception {
        when(announcementService.publicRead(123L)).thenThrow(new AnnouncementNotFound("Not found"));
        mockMvc.perform(get(RESOURCE).accept(MediaType.TEXT_PLAIN)).andExpect(status().isNotFound())
                .andExpect(header().string("Cache-Control", containsString("no-store")));
    }

    @Test
    void withdrawnSnapshotIsNeverRenderedEvenIfTheServiceReturnsIt() throws Exception {
        when(announcementService.publicRead(123L)).thenReturn(announcement(false));
        mockMvc.perform(get(RESOURCE)).andExpect(status().isNotFound())
                .andExpect(content().string(not(containsString("Club Cup"))));
    }

    @Test
    void anonymousAccessIsLimitedToTheExactGetResource() throws Exception {
        mockMvc.perform(post(RESOURCE).contentType(MediaType.APPLICATION_JSON).content(CONTENT))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(put(RESOURCE).header("If-Match", "4").contentType(MediaType.APPLICATION_JSON).content(CONTENT))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(delete(RESOURCE).header("If-Match", "4")).andExpect(status().isUnauthorized());
        mockMvc.perform(get(RESOURCE + "/management")).andExpect(status().isUnauthorized());
        mockMvc.perform(get(RESOURCE + "/other")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/tournamentmgmt/announcements/not-a-number")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/tournamentmgmt/config/123")).andExpect(status().isUnauthorized());
        mockMvc.perform(head(RESOURCE)).andExpect(status().isUnauthorized());
        verifyNoInteractions(announcementService);
    }

    @Test
    void invalidBearerTokenIsRejectedEvenOnPublicRoute() throws Exception {
        when(jwtDecoder.decode("invalid-token")).thenThrow(new BadJwtException("invalid"));
        mockMvc.perform(get(RESOURCE).header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(announcementService);
    }

    @Test
    void authenticatedCreatorCanPublishAndObtainLink() throws Exception {
        when(announcementService.create(eq(123L), any(AnnouncementContent.class), eq(42L)))
                .thenReturn(announcement(true));
        mockMvc.perform(post(RESOURCE).with(jwt().jwt(j -> j.claim("rallyon_user_id", 42)
                                .claim("realm_access", Map.of("roles", List.of("rallyon-organizer")))))
                        .contentType(MediaType.APPLICATION_JSON).content(CONTENT))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", RESOURCE));
        verify(announcementService).create(eq(123L), any(AnnouncementContent.class), eq(42L));
    }

    @Test
    void enforcesPreconditionIdentityAndServiceAuthorization() throws Exception {
        mockMvc.perform(put(RESOURCE).with(jwt().jwt(j -> j.claim("rallyon_user_id", 42)
                                .claim("realm_access", Map.of("roles", List.of("rallyon-organizer")))))
                        .contentType(MediaType.APPLICATION_JSON).content(CONTENT))
                .andExpect(status().isBadRequest());
        mockMvc.perform(put(RESOURCE).with(jwt().jwt(j -> j.claim("rallyon_user_id", 42)
                                .claim("realm_access", Map.of("roles", List.of("rallyon-organizer")))))
                        .header("If-Match", "not-a-number").contentType(MediaType.APPLICATION_JSON).content(CONTENT))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post(RESOURCE).with(jwt().jwt(j ->
                                j.claim("realm_access", Map.of("roles", List.of("rallyon-organizer")))))
                        .contentType(MediaType.APPLICATION_JSON).content(CONTENT))
                .andExpect(status().isForbidden());
        when(announcementService.management(123L, 41L)).thenThrow(new AnnouncementForbidden("Forbidden"));
        mockMvc.perform(get(RESOURCE + "/management").with(jwt().jwt(j -> j.claim("rallyon_user_id", 41)
                                .claim("realm_access", Map.of("roles", List.of("rallyon-organizer"))))))
                .andExpect(status().isForbidden());
        when(announcementService.withdraw(123L, 3L, 42L)).thenThrow(new AnnouncementConflict("Stale"));
        mockMvc.perform(delete(RESOURCE).with(jwt().jwt(j -> j.claim("rallyon_user_id", 42)
                                .claim("realm_access", Map.of("roles", List.of("rallyon-organizer")))))
                        .header("If-Match", "3"))
                .andExpect(status().isConflict());
    }

    @Test
    void creatorCanReadWithdrawnSnapshotForRepublication() throws Exception {
        when(announcementService.management(123L, 42L)).thenReturn(announcement(false));
        mockMvc.perform(get(RESOURCE + "/management").with(jwt().jwt(j -> j.claim("rallyon_user_id", 42)
                                .claim("realm_access", Map.of("roles", List.of("rallyon-organizer"))))))
                .andExpect(status().isOk())
                .andExpect(header().string("Cache-Control", containsString("no-store")))
                .andExpect(content().string(containsString("\"published\":false")))
                .andExpect(content().string(containsString("\"version\":4")));
    }
}
