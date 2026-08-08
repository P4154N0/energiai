package com.energiai.api.service.impl;

import com.energiai.api.client.MlPrediccionResultado;
import com.energiai.api.model.dto.request.ConsumoEnergeticoRequest;
import com.energiai.api.service.RecomendacionesEngine;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * LA ESTACIÓN DEL MAESTRO SAZONADOR (SRP Implementation)
 *
 * En esta estación de la cocina, el Maestro Sazonador (Chef Saucier) recibe el plato base
 * horneado por la Inteligencia Artificial y la comanda detallada del comensal.
 *
 * Su única y sagrada misión (SRP) es sazonar el banquete añadiendo especias acumulativas
 * (reglas de negocio): un toque de pimienta para el trabajo remoto, una pizca de hierbas
 * para la climatización y el sazón justo para el uso de electrodomésticos.
 */

@Service
public class RecomendacionesEngineImpl implements RecomendacionesEngine {

    @Override
    public List<String> generarRecomendaciones(ConsumoEnergeticoRequest request, MlPrediccionResultado resultadoMl) {
        List<String> recomendaciones = new ArrayList<>();

        if (resultadoMl == null) {
            recomendaciones.add("No se pudo obtener el diagnostico de IA, pero se recomienda mantener hábitos de consumo eficientes.");
            return recomendaciones;
        }

        String categoria = resultadoMl.getCategoria();

        // 1. Regla según la clasificación del Modelo de Inteligencia Artificial
        if ("Ineficiente".equalsIgnoreCase(categoria)) {
            recomendaciones.add("Su nivel de consumo es elevado. Se recomienda auditar los equipos de mayor potencia para reducir el impacto económico.");
        } else if ("Moderado".equalsIgnoreCase(categoria)) {
            recomendaciones.add("Su consumo es aceptable, pero existen oportunidades de optimización en horarios de pico.");
        } else if ("Eficiente".equalsIgnoreCase(categoria)) {
            recomendaciones.add("¡Felicitaciones! Su hogar mantiene un patrón de uso de energía altamente eficiente.");
        } else {
            recomendaciones.add("Consumo clasificado como: " + categoria + ".");
        }

        // 2. Regla por Trabajo Remoto (Home Office y Consumo 'Vampiro')
        if (Boolean.TRUE.equals(request.getHomeOffice())) {
            recomendaciones.add("Al trabajar desde casa, utilice zapatillas con interruptor para apagar computadoras, monitores y cargadores al finalizar su jornada (evite el consumo 'vampiro').");
        }

        // 3. Regla por Climatización (Aire Acondicionado + Pico de consumo alto)
        boolean tieneAc = request.getHasAc() != null && request.getHasAc() == 1;
        boolean usoPicoAlto = request.getPeakUsageLevel() != null && "HIGH".equalsIgnoreCase(request.getPeakUsageLevel().name());

        if (tieneAc && usoPicoAlto) {
            recomendaciones.add("Mantenga el aire acondicionado fijado a 24°C y limpie los filtros mensualmente para evitar sobreexigir el compresor.");
        } else if (tieneAc) {
            recomendaciones.add("Recuerde programar el aire acondicionado en modo ECO o mantener la temperatura recomendada de 24°C.");
        }

        // 4. Regla por Equipamiento y Densidad de Hogar
        boolean altaCantidadEquipos = request.getEquipmentCount() != null && request.getEquipmentCount() >= 5;
        boolean hogarNumeroso = request.getHouseholdSize() != null && request.getHouseholdSize() >= 4;

        if (altaCantidadEquipos || hogarNumeroso) {
            recomendaciones.add("Evite encender simultáneamente electrodomésticos de gran potencia (lavarropas, horno eléctrico, plancha) durante las horas de mayor consumo.");
        }

        return recomendaciones;
    }
}