package com.energiai.api.controller;

import com.energiai.api.model.dto.request.ConsumoEnergeticoRequest;
import com.energiai.api.model.dto.response.AnalisisEnergeticoResponse;
import com.energiai.api.service.AnalisisEnergeticoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


/**
 * EL MOZO DEL SALÓN (REST Controller)
 *
 * En la penumbra elegante del gran salón, el comensal —que bien puede ser un navegante
 * en su explorador web, una aplicación móvil o un curioso viajero como Postman— toma asiento.
 * El Mozo acude con presteza: no cocina, no altera los ingredientes, pero escucha con atención
 * la Comanda (ConsumoEnergeticoRequest).
 *
 * Su misión es sagrada: recibir la petición del mundo exterior, llevarla intacta a las puertas
 * de la gran cocina y, finalmente, retornar a la mesa con el Plato Servido (AnalisisEnergeticoResponse)
 * presentado con impecable maestría.
 */


@RestController                    // ← etiqueta de la CLASE (una sola vez, arriba de todo)
public class AnalisisEnergeticoController {

    private final AnalisisEnergeticoService service;   // ← acá "guardo" la referencia la chapita o el bordado en el delantal
                                                       //   la que simpre va a decir el nombre del cocinero!! hasta que finalice el turno
                                                       //   o en caso de que existan muchos cocineros cada cual es una entidad propia y
                                                       //   distinta de la otra

    public AnalisisEnergeticoController(AnalisisEnergeticoService service) {  // ← el constructor
        this.service = service;    // ← Spring me la pasa acá, yo la guardo
    }

    @PostMapping("/analisis-energetico")
    public AnalisisEnergeticoResponse recibirConsumo(
            @Valid @RequestBody ConsumoEnergeticoRequest request // ← ¡ACÁ SUMAMOS @Valid!
    ) {
        System.out.println("householdSize: " + request.getHouseholdSize());
        System.out.println("hasAc: " + request.getHasAc());
        System.out.println("housingType: " + request.getHousingType());
        System.out.println("consumoTotalMesAnterior: " + request.getConsumoTotalMesAnterior());
        System.out.println("peakUsageLevel: " + request.getPeakUsageLevel());

        return service.analizar(request);
    }
}