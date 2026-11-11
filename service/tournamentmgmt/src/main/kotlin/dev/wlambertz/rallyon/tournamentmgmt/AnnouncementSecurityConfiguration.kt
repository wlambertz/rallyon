package dev.wlambertz.rallyon.tournamentmgmt

import dev.wlambertz.rallyon.iam.keycloak.spring.KeycloakSecurityProperties
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.util.matcher.RequestMatcher

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "rallyon.security.keycloak", name = ["enabled"], havingValue = "true", matchIfMissing = true)
class AnnouncementSecurityConfiguration {
    @Bean
    fun announcementSecurityFilterChain(
        http: HttpSecurity,
        properties: KeycloakSecurityProperties,
        @Qualifier("keycloakJwtAuthenticationConverter") converter: Converter<Jwt, out AbstractAuthenticationToken>
    ): SecurityFilterChain {
        val publicAnnouncement = RequestMatcher { request ->
            request.method == HttpMethod.GET.name() &&
                PUBLIC_PATH.matches(request.requestURI.removePrefix(request.contextPath))
        }
        http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { authorize ->
                authorize.requestMatchers(publicAnnouncement).permitAll()
                properties.permitAll.forEach { pattern -> authorize.requestMatchers(pattern).permitAll() }
                authorize.anyRequest().authenticated()
            }
            .oauth2ResourceServer { it.jwt { jwt -> jwt.jwtAuthenticationConverter(converter) } }
        return http.build()
    }

    companion object {
        private val PUBLIC_PATH = Regex("^/api/tournamentmgmt/announcements/[0-9]+$")
    }
}
