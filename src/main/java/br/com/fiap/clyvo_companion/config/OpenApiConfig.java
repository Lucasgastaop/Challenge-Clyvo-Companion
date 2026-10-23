package br.com.fiap.clyvo_companion.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI clyvoCompanionOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Clyvo Companion API")
                        .description("API REST para gestão de saúde e cuidados de pets. "
                                + "Autentique em POST /auth/login e envie o access token no header Authorization: Bearer.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("FIAP - Java Advanced")
                                .email("contato@fiap.com.br")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components().addSecuritySchemes("bearerAuth",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
