package com.energiai.api.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * LA COMANDA INTERNA DE REPOSTERÍA (DTO Request para la IA)
 *
 * La pequeña nota que el Cocinero Jefe escribe de su propio puño y letra
 * para la estación de postres. Traduce los datos generales de la comanda
 * del cliente a las proporciones exactas que el Repostero (sea Titular o Ayudante)
 * necesita para hornear la predicción.
 */

@Getter
@Setter
public class MlPrediccionRequest {

    @JsonProperty("household_size")
    private Integer householdSize;

    @JsonProperty("has_ac")
    private Integer hasAc;

    @JsonProperty("home_office")
    private Boolean homeOffice;

    @JsonProperty("housing_type")
    private String housingType;

    @JsonProperty("equipment_count")
    private Integer equipmentCount;

    @JsonProperty("avg_energy_consumption_kwh")
    private Double avgEnergyConsumptionKwh;

    @JsonProperty("peak_usage_level")
    private String peakUsageLevel;
}