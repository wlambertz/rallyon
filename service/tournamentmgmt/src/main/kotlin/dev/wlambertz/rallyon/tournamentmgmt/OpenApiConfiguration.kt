package dev.wlambertz.rallyon.tournamentmgmt

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfiguration {

    @Bean
    fun tournamentmgmtOpenAPI(): OpenAPI = OpenAPI()
        .info(Info().title("Tournament Management API").version("v1"))
        .addSecurityItem(SecurityRequirement().addList(SECURITY_SCHEME_NAME))
        .components(
            Components().addSecuritySchemes(
                SECURITY_SCHEME_NAME,
                SecurityScheme()
                    .name(SECURITY_SCHEME_NAME)
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
            )
        )

    private companion object {
        const val SECURITY_SCHEME_NAME = "bearerAuth"
    }
}
