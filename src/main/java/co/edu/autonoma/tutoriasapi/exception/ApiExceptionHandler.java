package co.edu.autonoma.tutoriasapi.exception;

import java.time.Instant;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import co.edu.autonoma.tutoriasapi.dto.ApiErrorResponse;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrorResponse> manejarEstado(ResponseStatusException excepcion) {
        HttpStatus status = HttpStatus.valueOf(excepcion.getStatusCode().value());
        return respuesta(status, excepcion.getReason() == null ? status.getReasonPhrase() : excepcion.getReason());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> manejarValidacion(MethodArgumentNotValidException excepcion) {
        String mensaje = excepcion.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .distinct()
                .collect(Collectors.joining("; "));
        return respuesta(HttpStatus.BAD_REQUEST, mensaje);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> manejarJsonInvalido(HttpMessageNotReadableException excepcion) {
        return respuesta(HttpStatus.BAD_REQUEST,
                "El cuerpo de la solicitud no es válido. Revisa el JSON, las fechas y los valores permitidos.");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> manejarConflictoDeDatos(DataIntegrityViolationException excepcion) {
        return respuesta(HttpStatus.CONFLICT,
                "La operación entra en conflicto con datos existentes o relaciones registradas.");
    }

    private ResponseEntity<ApiErrorResponse> respuesta(HttpStatus status, String mensaje) {
        ApiErrorResponse cuerpo = new ApiErrorResponse(Instant.now(), status.value(),
                status.getReasonPhrase(), mensaje);
        return ResponseEntity.status(status).body(cuerpo);
    }
}
