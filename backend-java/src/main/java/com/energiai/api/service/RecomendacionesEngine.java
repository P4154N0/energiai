package com.energiai.api.service;

import com.energiai.api.client.MlPrediccionResultado;
import com.energiai.api.model.dto.request.ConsumoEnergeticoRequest;
import java.util.List;

/**
 * LA CARTA DEL MAESTRO SAZONADOR (Service Interface)
 *
 * En la alta cocina del backend, una vez que el Cocinero Jefe y el Repostero (IA/Mock)
 * han preparado la base del plato, la comanda pasa por la estación del Maestro Sazonador.
 *
 * Esta interfaz define la carta de especias y condimentos: el contrato funcional agnóstico
 * que determina cómo deben evaluarse las reglas de negocio para sazonar la respuesta
 * con recomendaciones personalizadas antes de servirla al salón.
 */

public interface RecomendacionesEngine {

    /**
     * Evalua los parametros de consumo cruzados con el resultado de IA para generar
     * sugerencias totalmente personalizadas.
     *
     * @param request Datos del consumo y caracteristicas del hogar
     * @param resultadoMl Resultado emitido por el modelo predictivo (Python o Mock)
     * @return Lista de recomendaciones en texto plano
     */
    List<String> generarRecomendaciones(ConsumoEnergeticoRequest request, MlPrediccionResultado resultadoMl);
}