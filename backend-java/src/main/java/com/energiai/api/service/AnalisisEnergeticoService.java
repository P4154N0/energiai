package com.energiai.api.service;

import com.energiai.api.model.dto.request.ConsumoEnergeticoRequest;
import com.energiai.api.model.dto.response.AnalisisEnergeticoResponse;

/**
 * LA CARTA PRINCIPAL DE LA CASA (Interfaz de Servicio)
 *
 * Es el menú oficial del restaurante exhibido en la entrada. No cocina ni guarda secretos
 * entre sus páginas; solo declara con absoluta claridad la promesa de la casa:
 * "Aquí se ofrece el servicio de analizar el consumo energético".
 *
 * Es el contrato sagrado que el Cocinero Jefe promete cumplir al pie de la letra.
 */

public interface AnalisisEnergeticoService {
    // TODO: define el contrato — qué operaciones ofrece el servicio

    AnalisisEnergeticoResponse analizar(ConsumoEnergeticoRequest request);

}
