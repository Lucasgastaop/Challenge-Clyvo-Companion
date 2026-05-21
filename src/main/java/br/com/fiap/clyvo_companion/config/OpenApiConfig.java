package br.com.fiap.clyvo_companion.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI clyvoCompanionOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Clyvo Companion API")
                        .description("API REST para gestão de saúde e cuidados de pets")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("FIAP - Java Advanced")
                                .email("contato@fiap.com.br")));
    }
}
