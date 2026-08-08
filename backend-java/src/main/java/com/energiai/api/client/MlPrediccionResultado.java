package com.energiai.api.client;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * EL POSTRE RECIÉN SALIDO DEL HORNO (DTO Response de la IA)
 *
 * La creación técnica que la estación de repostería le entrega en mano
 * al Cocinero Jefe. Contiene la clasificación de la IA (Ineficiente, Moderado, Eficiente)
 * y su nivel de certeza (probabilidad).
 *
 * No va directo a la mesa del comensal: el Cocinero Jefe lo recibe en su mesa de trabajo,
 * lo combina con sus propios acompañamientos (las recomendaciones y el costo mensual)
 * y recién ahí monta el Plato Servido final en la vajilla de la casa.
 */

@Getter
@Setter
public class MlPrediccionResultado {

    private String categoria;
    private Double probabilidad;
    private Map<String, Double> probabilidades;
}