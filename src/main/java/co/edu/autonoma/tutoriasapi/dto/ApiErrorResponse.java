package co.edu.autonoma.tutoriasapi.dto;

import java.time.Instant;

public record ApiErrorResponse(Instant fechaHora, int codigo, String error, String mensaje) {
}
