package dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.web;

import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.ConfigurationService;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Tournament;
import dev.wlambertz.rallyon.tournamentmgmt.setup.configuration.api.Visibility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
}
