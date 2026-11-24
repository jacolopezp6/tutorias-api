package co.edu.autonoma.tutoriasapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import co.edu.autonoma.tutoriasapi.dto.EstadoTutoriaRequest;
import co.edu.autonoma.tutoriasapi.entity.Area;
import co.edu.autonoma.tutoriasapi.entity.EstadoTutoria;
import co.edu.autonoma.tutoriasapi.entity.Tutoria;
import co.edu.autonoma.tutoriasapi.entity.Tutor;
import co.edu.autonoma.tutoriasapi.repository.DisponibilidadRepository;
import co.edu.autonoma.tutoriasapi.repository.ParticipanteRepository;
import co.edu.autonoma.tutoriasapi.repository.TutoriaRepository;
import co.edu.autonoma.tutoriasapi.repository.TutorRepository;

@ExtendWith(MockitoExtension.class)
class TutoriaServiceTest {

    @Mock
    private TutoriaRepository tutoriaRepository;

    @Mock
    private TutorRepository tutorRepository;

    @Mock
    private DisponibilidadRepository disponibilidadRepository;

    @Mock
    private ParticipanteRepository participanteRepository;

    @InjectMocks
    private TutoriaService service;

    @Test
    void buscar_existente_devuelveLaTutoria() {
        // Arrange
        Tutoria tutoria = crearTutoria(LocalDateTime.now().plusDays(1));
        when(tutoriaRepository.findById(3)).thenReturn(Optional.of(tutoria));
        when(participanteRepository.countByTutoria_Id(3)).thenReturn(2L);

        // Act
        var respuesta = service.buscar(3);

        // Assert
        assertThat(respuesta.tema()).isEqualTo("Álgebra");
        assertThat(respuesta.estado()).isEqualTo(EstadoTutoria.PROGRAMADA);
        assertThat(respuesta.participantesInscritos()).isEqualTo(2);
    }

    @Test
    void buscar_inexistente_lanza404() {
        // Arrange
        when(tutoriaRepository.findById(99)).thenReturn(Optional.empty());

        // Act
        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> service.buscar(99));

        // Assert
        assertThat(error.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void cambiarEstado_tutoriaFuturaNoFinalizada_lanzaConflict() {
        // Arrange
        Tutoria tutoria = crearTutoria(LocalDateTime.now().plusDays(1));
        when(tutoriaRepository.findByIdForUpdate(3)).thenReturn(Optional.of(tutoria));

        // Act
        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> service.cambiarEstado(3, new EstadoTutoriaRequest(EstadoTutoria.REALIZADA)));

        // Assert
        assertThat(error.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        verify(tutoriaRepository, never()).save(any(Tutoria.class));
    }

    @Test
    void cambiarEstado_tutoriaFinalizada_guardaEstadoRealizada() {
        // Arrange
        Tutoria tutoria = crearTutoria(LocalDateTime.now().minusDays(1));
        when(tutoriaRepository.findByIdForUpdate(3)).thenReturn(Optional.of(tutoria));
        when(tutoriaRepository.save(any(Tutoria.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));
        when(participanteRepository.countByTutoria_Id(3)).thenReturn(0L);

        // Act
        var respuesta = service.cambiarEstado(3,
                new EstadoTutoriaRequest(EstadoTutoria.REALIZADA));

        // Assert
        assertThat(respuesta.estado()).isEqualTo(EstadoTutoria.REALIZADA);
        verify(tutoriaRepository).save(tutoria);
    }

    private Tutoria crearTutoria(LocalDateTime inicio) {
        Area area = new Area("Matemáticas");
        Tutor tutor = new Tutor("Ana Pérez", "ana@example.com", area);
        Tutoria tutoria = new Tutoria(tutor, "Álgebra", inicio, inicio.plusHours(1), 10);
        ReflectionTestUtils.setField(tutoria, "id", 3);
        return tutoria;
    }
}
