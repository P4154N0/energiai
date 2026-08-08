package com.energiai.api.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * EL PLATO SERVIDO (DTO Response)
 *
 * La obra terminada. La vajilla fina donde convergen el diagnóstico de la repostería (IA/Mock)
 * y los acompañamientos calculados por el Cocinero Jefe (Costo mensual y Recomendaciones).
 * Es el deleite que el Mozo lleva de regreso a la mesa del comensal.
 *
 * Ahora enriquecido con metadatos de observabilidad y resiliencia.
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnalisisEnergeticoResponse {

    private String categoria;
    private Double probabilidad;
    private List<String> recomendaciones;
    private Double costoEstimadoMensual;

    // --- METADATOS DE OBSERVABILIDAD Y RESILIENCIA ---
    private String fuenteDatos;   // ej: "IA_PYTHON_REAL" o "MOCK_FALLBACK"
    private String detalleFuente; // Explicación amigable o causa técnica en caso de falla
}
