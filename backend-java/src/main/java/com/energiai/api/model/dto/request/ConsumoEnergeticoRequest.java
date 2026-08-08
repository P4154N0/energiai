package com.energiai.api.model.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

/**
 * LA COMANDA TOMADA POR EL MOZO (DTO Request)
 *
 * El registro formal donde el Mozo traslada la selección que el comensal hizo de la carta.
 * No contiene lógica ni secretos de cocina; solo la verdad pura de lo que el cliente dictó
 * sobre su hogar: sus hábitos de consumo, sus equipos y sus rutinas.
 * Es el contrato formal escrito en la libreta del Mozo que viaja directo a los fogones
 * del Cocinero Jefe.
 *
 * Ahora blindado con validaciones de Jakarta en el borde de entrada.
 */

@Getter
@Setter
public class ConsumoEnergeticoRequest {

    @NotNull(message = "El número de integrantes del hogar es obligatorio")
    @Min(value = 1, message = "El tamaño del hogar debe ser de al menos 1 persona")
    private Integer householdSize;

    @NotNull(message = "El indicador de aire acondicionado es obligatorio")
    @Min(value = 0, message = "El indicador de aire acondicionado debe ser 0 (No) o 1 (Sí)")
    @Max(value = 1, message = "El indicador de aire acondicionado debe ser 0 (No) o 1 (Sí)")
    private Integer hasAc;

    @NotNull(message = "Debe especificar si realiza trabajo remoto (home office)")
    private Boolean homeOffice;

    @NotNull(message = "El tipo de vivienda es obligatorio")
    private HousingType housingType;

    @NotNull(message = "La cantidad de equipos es obligatoria")
    @Min(value = 0, message = "La cantidad de equipos no puede ser un valor negativo")
    private Integer equipmentCount;

    @NotNull(message = "El consumo total del mes anterior es obligatorio")
    @PositiveOrZero(message = "El consumo total del mes anterior no puede ser un valor negativo")
    private Double consumoTotalMesAnterior;

    @NotNull(message = "El nivel de consumo pico es obligatorio")
    private PeakUsageLevel peakUsageLevel;

    /**
     * Parámetro tarifario ingresado por el usuario o empresa por kWh.
     * Si no se especifica o llega nulo/inválido, se utiliza el costo por defecto (0.75).
     */
    @Positive(message = "El costo por kWh debe ser un valor estrictamente mayor a cero")
    private Double costoPorKwh;

    /**
     * Getter personalizado para garantizar que nunca viaje un costo nulo o <= 0 al cálculo de negocio.
     */
    public Double getCostoPorKwh() {
        if (costoPorKwh == null || costoPorKwh <= 0.0) {
            return 0.75;
        }
        return costoPorKwh;
    }
}