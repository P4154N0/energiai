package com.energiai.api.service.impl;

import com.energiai.api.client.MlModelClient;
import com.energiai.api.client.MlPrediccionRequest;
import com.energiai.api.client.MlPrediccionResultado;
import com.energiai.api.model.dto.request.ConsumoEnergeticoRequest;
import com.energiai.api.model.dto.response.AnalisisEnergeticoResponse;
import com.energiai.api.service.AnalisisEnergeticoService;
import com.energiai.api.service.RecomendacionesEngine;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * EL COCINERO JEFE Y EL ARTE DEL FALLBACK (Service Implementation)
 *
 * Tras las puertas vaivén de la cocina, el Cocinero Jefe orquesta la magia. Él no es el mozo;
 * él gobierna los fuegos de la lógica de negocio. Lee la comanda, calcula costos y sazona
 * las recomendaciones.
 *
 * Sin embargo, al llegar el momento del postre —la compleja predicción de Inteligencia Artificial—
 * el Cocinero Jefe sabe que esa delicia no nace de su estación. Confecciona una comanda interna
 * y solicita el saber del Repostero Titular (Servicio Python).
 *
 * Mas, si el Repostero Titular ha ausentado su presencia o sus hornos se han enfriado (falla de red),
 * el Cocinero Jefe no desespera ni envía una bandeja vacía al salón: acude de inmediato al
 * Repostero Ayudante (MlModelClientMock). Éste, en un acto de perfecta resiliencia, emplata
 * la réplica exacta en la misma vajilla dorada. El comensal en el salón jamás sabrá del drama
 * en la cocina; solo disfrutará de la excelencia de su banquete.
 */
@Service
public class AnalisisEnergeticoServiceImpl implements AnalisisEnergeticoService {

    private final MlModelClient realClient;
    private final MlModelClient mockClient;
    private final RecomendacionesEngine recomendacionesEngine;

    public AnalisisEnergeticoServiceImpl(
            @Qualifier("mlModelClientImpl") MlModelClient realClient,
            @Qualifier("mlModelClientMock") MlModelClient mockClient,
            RecomendacionesEngine recomendacionesEngine) {
        this.realClient = realClient;
        this.mockClient = mockClient;
        this.recomendacionesEngine = recomendacionesEngine;
    }

    @Override
    public AnalisisEnergeticoResponse analizar(ConsumoEnergeticoRequest request) {

        // 1. Transformación al DTO que requiere Python
        MlPrediccionRequest mlRequest = mapToMlRequest(request);

        MlPrediccionResultado resultadoMl;
        String fuenteDatos;
        String detalleFuente;

        // 2. Estrategia de Fallback (Resiliencia ante fallas de red)
        try {
            resultadoMl = realClient.predecir(mlRequest);
            fuenteDatos = "IA_PYTHON_REAL";
            detalleFuente = "Procesado exitosamente por el modelo de IA en Python";
        } catch (Exception e) {
            System.err.println("⚠️ ALERTA: No se pudo conectar con el servicio de IA en Python (" + e.getMessage() + ")");
            System.out.println("🔄 ACTIVANDO MODO FALLBACK: Utilizando MlModelClientMock para responder...");

            resultadoMl = mockClient.predecir(mlRequest);
            fuenteDatos = "MOCK_FALLBACK";
            detalleFuente = "Respuesta estimada por caída o timeout del servicio de IA. Causa técnica: " + e.getMessage();
        }

        // 3. Cálculos de negocio propios de Java (Costo Estimado Mensual)
        Double consumoTotal = request.getConsumoTotalMesAnterior();
        Double tarifa = request.getCostoPorKwh();
        Double costoEstimado = (consumoTotal != null) ? consumoTotal * tarifa : 0.0;

        // 4. Construcción de recomendaciones personalizadas según perfil e IA
        List<String> recomendaciones = recomendacionesEngine.generarRecomendaciones(request, resultadoMl);

        // 5. Retorno de la respuesta con metadatos de observabilidad
        return new AnalisisEnergeticoResponse(
                resultadoMl.getCategoria(),
                resultadoMl.getProbabilidad(),
                recomendaciones,
                costoEstimado,
                fuenteDatos,
                detalleFuente
        );
    }

    private MlPrediccionRequest mapToMlRequest(ConsumoEnergeticoRequest request) {
        MlPrediccionRequest mlReq = new MlPrediccionRequest();
        mlReq.setHouseholdSize(request.getHouseholdSize());
        mlReq.setHasAc(request.getHasAc());
        mlReq.setHomeOffice(request.getHomeOffice());

        if (request.getHousingType() != null) {
            mlReq.setHousingType(request.getHousingType().name());
        }

        mlReq.setEquipmentCount(request.getEquipmentCount());

        if (request.getConsumoTotalMesAnterior() != null) {
            mlReq.setAvgEnergyConsumptionKwh(request.getConsumoTotalMesAnterior() / 31.0);
        } else {
            mlReq.setAvgEnergyConsumptionKwh(0.0);
        }

        if (request.getPeakUsageLevel() != null) {
            mlReq.setPeakUsageLevel(request.getPeakUsageLevel().name());
        }

        return mlReq;
    }
}
