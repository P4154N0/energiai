package com.energiai.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

//  Clase de configuración de Spring que define e inyecta la herramienta de cliente HTTP
@Configuration
public class RestClientConfig {

    @Value("${ml.model.service.url}")
    private String mlServiceUrl;

    // Inyectamos el RestClient.Builder que Spring Boot ya tiene autoconfigurado con Jackson
    @Bean
    public RestClient mlRestClient(RestClient.Builder builder) {
        return builder
                .baseUrl(mlServiceUrl)
                .build();
    }
}