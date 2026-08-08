package com.energiai.api.client;

import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * EL REPOSTERO AYUDANTE (Mock / Resiliencia Fallback)
 *
 * El fiel guardián que jamás abandona la cocina de Java. Cuando las luces del taller distante
 * se apagan, el Ayudante da un paso al frente. Conociendo el estándar de la casa y utilizando
 * la misma vajilla de respuesta, recrea la experiencia simulada para garantizar que el restaurante
 * jamás deje de servir.
 */

@Component
public class MlModelClientMock implements MlModelClient {

    @Override
    public MlPrediccionResultado predecir(MlPrediccionRequest datos) {

        // Regla simple, sin llamar a ningún servicio externo:
        // si el consumo promedio diario es alto, decimos "Ineficiente".
        String categoria;
        if (datos.getAvgEnergyConsumptionKwh() > 15) {
            categoria = "Ineficiente";
        } else if (datos.getAvgEnergyConsumptionKwh() > 8) {
            categoria = "Moderado";
        } else {
            categoria = "Eficiente";
        }

        MlPrediccionResultado resultado = new MlPrediccionResultado();
        resultado.setCategoria(categoria);
        resultado.setProbabilidad(0.5); // valor fijo, no hay modelo real calculando esto
        resultado.setProbabilidades(Map.of(
                "Eficiente", 0.0,
                "Moderado", 0.0,
                "Ineficiente", 0.0
        ));

        return resultado;
    }
}