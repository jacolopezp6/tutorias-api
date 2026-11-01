package co.edu.autonoma.tutoriasapi.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record TutoriaRequest(
        @NotNull Integer tutorId,
        @NotBlank @Size(max = 160) String tema,
        @NotNull LocalDateTime fechaHoraInicio,
        @NotNull LocalDateTime fechaHoraFin,
        @NotNull @Positive Integer limiteParticipantes) {
}
