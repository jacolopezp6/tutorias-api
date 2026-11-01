package co.edu.autonoma.tutoriasapi.dto;

import java.time.LocalDateTime;

import co.edu.autonoma.tutoriasapi.entity.EstadoTutoria;

public record TutoriaResponse(Integer id, Integer tutorId, String tutorNombre, String tema,
                              LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraFin,
                              Integer limiteParticipantes, long participantesInscritos,
                              EstadoTutoria estado) {
}
