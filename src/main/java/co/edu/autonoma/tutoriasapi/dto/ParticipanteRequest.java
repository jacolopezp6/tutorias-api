package co.edu.autonoma.tutoriasapi.dto;

import jakarta.validation.constraints.NotNull;

public record ParticipanteRequest(@NotNull Integer estudianteId) {
}
