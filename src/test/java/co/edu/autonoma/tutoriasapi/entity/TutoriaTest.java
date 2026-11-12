package co.edu.autonoma.tutoriasapi.entity;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TutoriaTest {

    @Test
    void actualizar_conTutoriaProgramada_cambiaTemaYLimite() {
        // Arrange
        Area area = new Area("Matemáticas");
        Tutor tutor = new Tutor("Ana Pérez", "ana@example.com", area);
        LocalDateTime inicio = LocalDateTime.now().plusDays(1);
        Tutoria tutoria = new Tutoria(tutor, "Álgebra", inicio,
                inicio.plusHours(1), 10);

        // Act
        tutoria.actualizar(tutor, "Cálculo", inicio, inicio.plusHours(2), 15);

        // Assert
        assertThat(tutoria.getTema()).isEqualTo("Cálculo");
        assertThat(tutoria.getLimiteParticipantes()).isEqualTo(15);
    }
}
