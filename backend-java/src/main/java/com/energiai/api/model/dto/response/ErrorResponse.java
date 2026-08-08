package com.energiai.api.model.dto.response;

import java.time.LocalDateTime;

/**
 * LA TARJETA DORADA DE ERROR (DTO de Respuesta)
 *
 * Contrato estandarizado para comunicar fallas o anomalías al cliente.
 * Garantiza que cualquier error devuelva exactamente la misma estructura de datos JSON.
 */

public class ErrorResponse {

    private LocalDateTime timestamp; // Fecha y hora exacta de la falla
    private int status;              // Código numérico HTTP (ej. 400, 500)
    private String error;            // Nombre descriptivo del estado HTTP (ej. "Bad Request")
    private String message;          // Explicación amigable del error para el usuario
    private String path;             // Ruta de la API que produjo la excepción (ej. /analisis-energetico)

    // Constructor vacío necesario para frameworks de serialización (Jackson)
    public ErrorResponse() {
    }

    // Constructor completo para instanciar la tarjeta de error en una sola línea
    public ErrorResponse(LocalDateTime timestamp, int status, String error, String message, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    // --- GETTERS Y SETTERS ---

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
