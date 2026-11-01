package co.edu.autonoma.tutoriasapi.dto;

import co.edu.autonoma.tutoriasapi.entity.EstadoTutoria;
import jakarta.validation.constraints.NotNull;

public record EstadoTutoriaRequest(@NotNull EstadoTutoria estado) {
}
