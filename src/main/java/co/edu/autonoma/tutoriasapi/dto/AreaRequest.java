package co.edu.autonoma.tutoriasapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AreaRequest(@NotBlank @Size(max = 100) String nombre) {
}
