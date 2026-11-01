package co.edu.autonoma.tutoriasapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import co.edu.autonoma.tutoriasapi.dto.DisponibilidadRequest;
import co.edu.autonoma.tutoriasapi.dto.DisponibilidadResponse;
import co.edu.autonoma.tutoriasapi.dto.AreaRequest;
import co.edu.autonoma.tutoriasapi.dto.AreaResponse;
import co.edu.autonoma.tutoriasapi.dto.EstudianteRequest;
import co.edu.autonoma.tutoriasapi.dto.EstudianteResponse;
import co.edu.autonoma.tutoriasapi.dto.EstadoTutoriaRequest;
import co.edu.autonoma.tutoriasapi.dto.ParticipanteRequest;
import co.edu.autonoma.tutoriasapi.dto.ParticipanteResponse;
import co.edu.autonoma.tutoriasapi.dto.TutorRequest;
import co.edu.autonoma.tutoriasapi.dto.TutorResponse;
import co.edu.autonoma.tutoriasapi.dto.TutoriaRequest;
import co.edu.autonoma.tutoriasapi.dto.TutoriaResponse;
import co.edu.autonoma.tutoriasapi.entity.Area;
import co.edu.autonoma.tutoriasapi.entity.Disponibilidad;
import co.edu.autonoma.tutoriasapi.entity.EstadoTutoria;
import co.edu.autonoma.tutoriasapi.entity.Estudiante;
import co.edu.autonoma.tutoriasapi.entity.Participante;
import co.edu.autonoma.tutoriasapi.entity.Tutor;
import co.edu.autonoma.tutoriasapi.entity.Tutoria;
import co.edu.autonoma.tutoriasapi.repository.AreaRepository;
import co.edu.autonoma.tutoriasapi.repository.DisponibilidadRepository;
import co.edu.autonoma.tutoriasapi.repository.EstudianteRepository;
import co.edu.autonoma.tutoriasapi.repository.ParticipanteRepository;
import co.edu.autonoma.tutoriasapi.repository.TutorRepository;
import co.edu.autonoma.tutoriasapi.repository.TutoriaRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@ActiveProfiles("test")
class TutoriasApiApplicationTests {

    @Autowired
    private TestRestTemplate http;

    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private TutorRepository tutorRepository;

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private DisponibilidadRepository disponibilidadRepository;

    @Autowired
    private ParticipanteRepository participanteRepository;

    @Autowired
    private TutoriaRepository tutoriaRepository;

    @BeforeEach
    void limpiarDatos() {
        participanteRepository.deleteAll();
        tutoriaRepository.deleteAll();
        disponibilidadRepository.deleteAll();
        estudianteRepository.deleteAll();
        tutorRepository.deleteAll();
        areaRepository.deleteAll();
    }

    @Test
    void registraActualizaConsultaYEliminaEstudiantes() {
        ResponseEntity<EstudianteResponse> creado = http.postForEntity("/api/estudiantes",
                new EstudianteRequest("Ana López", "ana@example.com"), EstudianteResponse.class);

        assertEquals(HttpStatus.CREATED, creado.getStatusCode());
        assertNotNull(creado.getBody());
        Integer id = creado.getBody().id();

        ResponseEntity<EstudianteResponse> actualizado = http.exchange("/api/estudiantes/{id}", HttpMethod.PUT,
                new HttpEntity<>(new EstudianteRequest("Ana María López", "ana@example.com")),
                EstudianteResponse.class, id);
        assertEquals("Ana María López", actualizado.getBody().nombre());

        ResponseEntity<EstudianteResponse> consultado = http.getForEntity("/api/estudiantes/{id}",
                EstudianteResponse.class, id);
        assertEquals(HttpStatus.OK, consultado.getStatusCode());
        assertEquals("Ana María López", consultado.getBody().nombre());

        ResponseEntity<Void> eliminado = http.exchange("/api/estudiantes/{id}", HttpMethod.DELETE,
                HttpEntity.EMPTY, Void.class, id);
        assertEquals(HttpStatus.NO_CONTENT, eliminado.getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND,
                http.getForEntity("/api/estudiantes/{id}", EstudianteResponse.class, id).getStatusCode());
    }

    @Test
    void administraAreasYTutores() {
        ResponseEntity<AreaResponse> areaCreada = http.postForEntity("/api/areas",
                new AreaRequest("Matemáticas"), AreaResponse.class);
        assertEquals(HttpStatus.CREATED, areaCreada.getStatusCode());
        Integer areaId = areaCreada.getBody().id();

        ResponseEntity<TutorResponse> tutorCreado = http.postForEntity("/api/tutores",
                new TutorRequest("Carlos Tutor", "carlos@example.com", areaId), TutorResponse.class);
        assertEquals(HttpStatus.CREATED, tutorCreado.getStatusCode());
        Integer tutorId = tutorCreado.getBody().id();

        ResponseEntity<TutorResponse[]> tutores = http.getForEntity("/api/tutores", TutorResponse[].class);
        assertEquals(1, tutores.getBody().length);

        ResponseEntity<TutorResponse> tutorActualizado = http.exchange("/api/tutores/{id}", HttpMethod.PUT,
                new HttpEntity<>(new TutorRequest("Carlos Actualizado", "carlos@example.com", areaId)),
                TutorResponse.class, tutorId);
        assertEquals("Carlos Actualizado", tutorActualizado.getBody().nombre());

        assertEquals(HttpStatus.NO_CONTENT, http.exchange("/api/tutores/{id}", HttpMethod.DELETE,
                HttpEntity.EMPTY, Void.class, tutorId).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, http.getForEntity("/api/tutores/{id}",
                TutorResponse.class, tutorId).getStatusCode());

        http.exchange("/api/areas/{id}", HttpMethod.PUT,
                new HttpEntity<>(new AreaRequest("Ciencias")), AreaResponse.class, areaId);
        assertEquals(HttpStatus.NO_CONTENT, http.exchange("/api/areas/{id}", HttpMethod.DELETE,
                HttpEntity.EMPTY, Void.class, areaId).getStatusCode());
    }

    @Test
    void validaHorarioCupoInscripcionesDuplicadasYCancelacion() {
        DatosTutor datos = prepararTutorConDisponibilidad();
        Estudiante primero = estudianteRepository.save(new Estudiante("Estudiante Uno", "uno@example.com"));
        Estudiante segundo = estudianteRepository.save(new Estudiante("Estudiante Dos", "dos@example.com"));
        LocalDateTime inicio = horaFutura();
        LocalDateTime fin = inicio.plusHours(1);

        ResponseEntity<TutoriaResponse> creada = http.postForEntity("/api/tutorias",
                new TutoriaRequest(datos.tutor().getId(), "Álgebra", inicio, fin, 1), TutoriaResponse.class);
        assertEquals(HttpStatus.CREATED, creada.getStatusCode());
        Integer tutoriaId = creada.getBody().id();
        assertEquals(EstadoTutoria.PROGRAMADA, creada.getBody().estado());

        ResponseEntity<ParticipanteResponse> inscripcion = http.postForEntity(
                "/api/tutorias/{id}/participantes", new ParticipanteRequest(primero.getId()),
                ParticipanteResponse.class, tutoriaId);
        assertEquals(HttpStatus.CREATED, inscripcion.getStatusCode());

        assertEquals(HttpStatus.CONFLICT, http.postForEntity("/api/tutorias/{id}/participantes",
                new ParticipanteRequest(primero.getId()), String.class, tutoriaId).getStatusCode());
        assertEquals(HttpStatus.CONFLICT, http.postForEntity("/api/tutorias/{id}/participantes",
                new ParticipanteRequest(segundo.getId()), String.class, tutoriaId).getStatusCode());
        assertEquals(HttpStatus.CONFLICT, http.exchange("/api/estudiantes/{id}", HttpMethod.DELETE,
                HttpEntity.EMPTY, Void.class, primero.getId()).getStatusCode());

        ResponseEntity<ParticipanteResponse[]> lista = http.getForEntity(
                "/api/tutorias/{id}/participantes", ParticipanteResponse[].class, tutoriaId);
        assertEquals(1, lista.getBody().length);

        assertEquals(HttpStatus.CONFLICT, http.postForEntity("/api/tutorias",
                new TutoriaRequest(datos.tutor().getId(), "Tema que se cruza", inicio.plusMinutes(30),
                        fin.plusMinutes(30), 2), String.class).getStatusCode());

        assertEquals(HttpStatus.NO_CONTENT, http.exchange("/api/tutorias/{tutoriaId}/participantes/{id}",
                HttpMethod.DELETE, HttpEntity.EMPTY, Void.class, tutoriaId, inscripcion.getBody().id()).getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, http.exchange("/api/tutorias/{id}", HttpMethod.DELETE,
                HttpEntity.EMPTY, Void.class, tutoriaId).getStatusCode());
        ResponseEntity<TutoriaResponse> cancelada = http.getForEntity("/api/tutorias/{id}",
                TutoriaResponse.class, tutoriaId);
        assertEquals(EstadoTutoria.CANCELADA, cancelada.getBody().estado());
    }

    @Test
    void noPermiteDisponibilidadesQueSeCruzanNiTutoriasFueraDelHorario() {
        DatosTutor datos = prepararTutorConDisponibilidad();
        ResponseEntity<DisponibilidadResponse[]> consulta = http.getForEntity(
                "/api/tutores/{id}/disponibilidad", DisponibilidadResponse[].class, datos.tutor().getId());
        assertEquals(1, consulta.getBody().length);
        LocalTime inicio = LocalTime.of(8, 0);
        DisponibilidadRequest cruce = new DisponibilidadRequest(datos.tutor().getId(),
                horaFutura().getDayOfWeek(), inicio.plusMinutes(30), inicio.plusHours(2));
        assertEquals(HttpStatus.CONFLICT,
                http.postForEntity("/api/disponibilidades", cruce, String.class).getStatusCode());

        LocalDateTime inicioFuturo = horaFutura();
        ResponseEntity<String> fueraDeHorario = http.postForEntity("/api/tutorias",
                new TutoriaRequest(datos.tutor().getId(), "Fuera de horario",
                        inicioFuturo.withHour(20), inicioFuturo.withHour(21), 2), String.class);
        assertEquals(HttpStatus.CONFLICT, fueraDeHorario.getStatusCode());
    }

    @Test
    void controlaLosCambiosDeEstadoYProtegeInscripcionesAlIniciarLaSesion() {
        DatosTutor datos = prepararTutorConDisponibilidad();
        Estudiante estudiante = estudianteRepository.save(new Estudiante("Estudiante", "sesion@example.com"));
        LocalDateTime ahora = LocalDateTime.now();
        Tutoria iniciada = tutoriaRepository.save(new Tutoria(datos.tutor(), "En curso",
                ahora.minusMinutes(10), ahora.plusMinutes(20), 2));
        Participante participante = new Participante(estudiante);
        iniciada.agregarParticipante(participante);
        participanteRepository.save(participante);

        assertEquals(HttpStatus.CONFLICT, http.exchange("/api/participantes/{id}", HttpMethod.DELETE,
                HttpEntity.EMPTY, Void.class, participante.getId()).getStatusCode());
        assertEquals(HttpStatus.CONFLICT, http.exchange("/api/tutorias/{id}", HttpMethod.DELETE,
                HttpEntity.EMPTY, Void.class, iniciada.getId()).getStatusCode());
        assertEquals(HttpStatus.CONFLICT, http.exchange("/api/tutorias/{id}/estado", HttpMethod.PATCH,
                new HttpEntity<>(new EstadoTutoriaRequest(EstadoTutoria.REALIZADA)), String.class,
                iniciada.getId()).getStatusCode());

        Tutoria finalizada = tutoriaRepository.save(new Tutoria(datos.tutor(), "Terminada",
                ahora.minusHours(2), ahora.minusHours(1), 2));
        ResponseEntity<TutoriaResponse> actualizada = http.exchange("/api/tutorias/{id}/estado", HttpMethod.PATCH,
                new HttpEntity<>(new EstadoTutoriaRequest(EstadoTutoria.REALIZADA)),
                TutoriaResponse.class, finalizada.getId());
        assertEquals(HttpStatus.OK, actualizada.getStatusCode());
        assertEquals(EstadoTutoria.REALIZADA, actualizada.getBody().estado());
    }

    @Test
    void devuelveErroresClarosParaDatosInvalidosYCorreosDuplicados() {
        ResponseEntity<String> invalido = http.postForEntity("/api/estudiantes",
                new EstudianteRequest("Nombre", "esto-no-es-correo"), String.class);
        assertEquals(HttpStatus.BAD_REQUEST, invalido.getStatusCode());

        http.postForEntity("/api/estudiantes", new EstudianteRequest("Ana", "ana@example.com"),
                EstudianteResponse.class);
        assertEquals(HttpStatus.CONFLICT, http.postForEntity("/api/estudiantes",
                new EstudianteRequest("Otra Ana", "ANA@example.com"), String.class).getStatusCode());
    }

    private DatosTutor prepararTutorConDisponibilidad() {
        Area area = areaRepository.save(new Area("Ciencias"));
        Tutor tutor = tutorRepository.save(new Tutor("Carlos Tutor", "carlos@example.com", area));
        LocalDateTime futura = horaFutura();
        disponibilidadRepository.save(new Disponibilidad(tutor, futura.getDayOfWeek(),
                LocalTime.of(8, 0), LocalTime.of(18, 0)));
        return new DatosTutor(tutor);
    }

    private LocalDateTime horaFutura() {
        return LocalDateTime.now().plusDays(7).withHour(10).withMinute(0).withSecond(0).withNano(0);
    }

    private record DatosTutor(Tutor tutor) {
    }
}
