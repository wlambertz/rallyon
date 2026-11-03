package dev.wlambertz.rallyon.iam.keycloak.spring;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sun.net.httpserver.HttpServer;
import dev.wlambertz.rallyon.iam.keycloak.core.KeycloakPrincipalFactory;
import dev.wlambertz.rallyon.iam.keycloak.core.KeycloakValidationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class KeycloakJwtDecoderTest {

    private static final String ISSUER = "https://issuer.example.test/realms/rallyon";
    private static final String AUDIENCE = "rallyon-api";
    private RSAKey signingKey;
    private HttpServer jwksServer;
    private JwtDecoder decoder;
    private KeycloakJwtGrantedAuthoritiesConverter authorities;

    @BeforeEach
    void configureDecoderWithLocalJwks() throws Exception {
        signingKey = new RSAKeyGenerator(2048).keyID("test-key").generate();
        byte[] jwks = new JWKSet(signingKey.toPublicJWK()).toString().getBytes(StandardCharsets.UTF_8);
        jwksServer = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        jwksServer.createContext("/jwks", exchange -> {
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, jwks.length);
            try (var body = exchange.getResponseBody()) {
                body.write(jwks);
            }
        });
        jwksServer.start();
        KeycloakSecurityProperties properties = new KeycloakSecurityProperties();
        properties.setIssuerUri(URI.create(ISSUER));
        properties.setJwksUri(URI.create("http://127.0.0.1:" + jwksServer.getAddress().getPort() + "/jwks"));
        properties.setAudience(AUDIENCE);
        properties.setRoleMappings(Map.of("rallyon-organizer", "ROLE_ORGANIZER"));
        decoder = new KeycloakSecurityAutoConfiguration().keycloakJwtDecoder(properties);
        authorities = new KeycloakJwtGrantedAuthoritiesConverter(new KeycloakPrincipalFactory(properties.toConfig()));
    }

    @AfterEach
    void stopJwksServer() {
        if (jwksServer != null) {
            jwksServer.stop(0);
        }
    }

    @Test
    void acceptsSignedTokenAndMapsOnlyConfiguredRoles() throws Exception {
        Jwt jwt = decoder.decode(token(claims(), signingKey));
        assertThat(jwt.getSubject()).isEqualTo("test-subject");
        assertThat(authorities.convert(jwt)).extracting("authority").containsExactly("ROLE_ORGANIZER");
    }

    @Test
    void rejectsSignatureFromAnotherKeyWithSameKeyId() throws Exception {
        RSAKey otherKey = new RSAKeyGenerator(2048).keyID(signingKey.getKeyID()).generate();
        String encoded = token(claims(), otherKey);
        assertThatThrownBy(() -> decoder.decode(encoded)).isInstanceOf(JwtException.class);
    }

    @Test
    void rejectsWrongIssuer() throws Exception {
        String encoded = token(claims().issuer("https://other.example.test"), signingKey);
        assertThatThrownBy(() -> decoder.decode(encoded)).isInstanceOf(JwtException.class)
                .hasMessageContaining("iss");
    }

    @Test
    void rejectsWrongAudience() throws Exception {
        String encoded = token(claims().audience("other-api"), signingKey);
        assertThatThrownBy(() -> decoder.decode(encoded)).isInstanceOf(JwtException.class)
                .hasMessageContaining("audience");
    }

    @Test
    void rejectsExpiredTokenBeyondClockSkew() throws Exception {
        String encoded = token(claims().issueTime(Date.from(Instant.now().minusSeconds(600)))
                .expirationTime(Date.from(Instant.now().minusSeconds(300))), signingKey);
        assertThatThrownBy(() -> decoder.decode(encoded)).isInstanceOf(JwtException.class)
                .hasMessageContaining("expired");
    }

    @Test
    void rejectsMissingNumericUserIdAfterDecoding() throws Exception {
        Jwt jwt = decoder.decode(token(claims().claim("rallyon_user_id", null), signingKey));
        assertThatThrownBy(() -> authorities.convert(jwt)).isInstanceOf(KeycloakValidationException.class)
                .hasMessageContaining("rallyon_user_id");
    }

    @Test
    void rejectsNonNumericUserIdAfterDecoding() throws Exception {
        Jwt jwt = decoder.decode(token(claims().claim("rallyon_user_id", "not-a-number"), signingKey));
        assertThatThrownBy(() -> authorities.convert(jwt)).isInstanceOf(KeycloakValidationException.class)
                .hasMessageContaining("rallyon_user_id");
    }

    @Test
    void rejectsUnmappedRolesAfterDecoding() throws Exception {
        Jwt jwt = decoder.decode(token(claims().claim("realm_access", Map.of("roles", List.of("unmapped"))), signingKey));
        assertThatThrownBy(() -> authorities.convert(jwt)).isInstanceOf(KeycloakValidationException.class)
                .hasMessageContaining("mapped roles");
    }

    private JWTClaimsSet.Builder claims() {
        return new JWTClaimsSet.Builder()
                .subject("test-subject")
                .issuer(ISSUER)
                .audience(AUDIENCE)
                .issueTime(Date.from(Instant.now().minusSeconds(10)))
                .expirationTime(Date.from(Instant.now().plusSeconds(300)))
                .claim("rallyon_user_id", 42)
                .claim("realm_access", Map.of("roles", List.of("rallyon-organizer", "unmapped")));
    }

    private String token(JWTClaimsSet.Builder claims, RSAKey key) throws JOSEException {
        SignedJWT jwt = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(key.getKeyID()).build(), claims.build());
        jwt.sign(new RSASSASigner(key));
        return jwt.serialize();
    }
}
