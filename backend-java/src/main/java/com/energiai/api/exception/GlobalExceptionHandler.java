package com.energiai.api.exception;

import com.energiai.api.model.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

/**
 * EL RECEPCIONISTA ELEGANTE (Manejo Global de Excepciones)
 *
 * Componente centralizado mediante @RestControllerAdvice que escucha
 * todas las excepciones producidas en la capa de transporte (Controllers).
 * Atrapa los errores en el aire, los empaqueta en un ErrorResponse y los
 * entrega en una respuesta HTTP limpia.
 */

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Captura fallas de conexión o errores explícitos con el servicio de IA en Python.
     * Retorna un código HTTP 503 (Service Unavailable).
     */
    @ExceptionHandler(MlServiceUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleMlServiceUnavailableException(
            MlServiceUnavailableException ex,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.SERVICE_UNAVAILABLE;

        ErrorResponse errorDTO = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorDTO, status);
    }

    /**
     * Captura errores de validación de datos en los Requests (ej. cuando usemos @NotNull o @Min).
     * Retorna un código HTTP 400 (Bad Request).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;

        // Extrae el mensaje de validación configurado en el DTO
        String mensajeDetalle = ex.getBindingResult().getAllErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("Datos de la petición inválidos");

        ErrorResponse errorDTO = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                mensajeDetalle,
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorDTO, status);
    }

    /**
     * Red de seguridad final: Captura cualquier otra excepción no prevista en el sistema.
     * Evita que el servidor exponga información técnica sensible y retorna un código HTTP 500.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        ErrorResponse errorDTO = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                "Ocurrió un error interno no esperado en el servidor. Intente nuevamente más tarde.",
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorDTO, status);
    }
}