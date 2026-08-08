package com.energiai.api.client;

import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * EL REPOSTERO TITULAR (IA / Microservicio Python)
 *
 * El artista bohemio que habita en un taller distante. Recibe el encargo preciso del Cocinero Jefe
 * a través de un hilo invisible de red HTTP. Interpreta las matemáticas del consumo y devuelve
 * la clasificación exacta esculpida por el modelo de IA.
 */

@Component
@Primary
public class MlModelClientImpl implements MlModelClient {

    private final RestClient restClient;

    public MlModelClientImpl(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public MlPrediccionResultado predecir(MlPrediccionRequest datos) {

        // --- LUPA DE DIAGNÓSTICO EN CONSOLA ---
        System.out.println("=================================================");
        System.out.println(">>> INSPECCIONANDO DATOS ANTES DE ENVIAR A PYTHON <<<");
        if (datos != null) {
            System.out.println("HouseholdSize: " + datos.getHouseholdSize());
            System.out.println("HasAc: " + datos.getHasAc());
            System.out.println("HomeOffice: " + datos.getHomeOffice());
            System.out.println("HousingType: " + datos.getHousingType());
            System.out.println("EquipmentCount: " + datos.getEquipmentCount());
            System.out.println("AvgEnergyConsumptionKwh: " + datos.getAvgEnergyConsumptionKwh());
            System.out.println("PeakUsageLevel: " + datos.getPeakUsageLevel());
        } else {
            System.out.println("¡ATENCIÓN! El objeto datos llegó NULL al cliente.");
        }
        System.out.println("=================================================");
        // -------------------------------------

        return restClient.post()
                .uri("/predict")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(datos)
                .retrieve()
                .body(MlPrediccionResultado.class);
    }
}