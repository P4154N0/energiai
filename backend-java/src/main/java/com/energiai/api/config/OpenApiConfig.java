package com.energiai.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AGRARIO - SAME - EnergIAI - API Principal (Backend Java)")
                        .version("1.0.0")
                        .description("API REST pública de diagnóstico de eficiencia energética. " +
                                "Gestiona la validación, orquestación con el servicio de ML y resiliencia con Fallback."));
    }
}