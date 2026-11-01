package co.edu.autonoma.tutoriasapi.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;

import jakarta.validation.constraints.NotNull;

public record DisponibilidadRequest(
        @NotNull Integer tutorId,
        @NotNull DayOfWeek diaSemana,
        @NotNull LocalTime horaInicio,
        @NotNull LocalTime horaFin) {
}
