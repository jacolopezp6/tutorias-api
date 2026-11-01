package co.edu.autonoma.tutoriasapi.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record DisponibilidadResponse(Integer id, Integer tutorId, DayOfWeek diaSemana,
                                     LocalTime horaInicio, LocalTime horaFin, boolean activa) {
}
