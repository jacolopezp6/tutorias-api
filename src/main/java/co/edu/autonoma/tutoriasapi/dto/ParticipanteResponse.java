package co.edu.autonoma.tutoriasapi.dto;

import java.time.LocalDateTime;

public record ParticipanteResponse(Integer id, Integer tutoriaId, Integer estudianteId,
                                   String estudianteNombre, String estudianteCorreo,
                                   LocalDateTime fechaInscripcion) {
}
