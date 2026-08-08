package com.energiai.api.exception;

/**
 * EXCEPCIÓN DE DOMINIO: Servicio de IA No Disponible
 *
 * Se dispara cuando la comunicación con el microservicio en Python (FastAPI)
 * falla, genera timeout o responde de forma no esperada.
 */

public class MlServiceUnavailableException extends RuntimeException {

    // Constructor que recibe un mensaje explicativo
    public MlServiceUnavailableException(String message) {
        super(message);
    }

    // Constructor que recibe un mensaje y la causa raíz (excepción original)
    public MlServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
