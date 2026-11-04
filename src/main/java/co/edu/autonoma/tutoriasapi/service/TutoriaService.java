package co.edu.autonoma.tutoriasapi.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import lombok.RequiredArgsConstructor;

import co.edu.autonoma.tutoriasapi.dto.EstadoTutoriaRequest;
import co.edu.autonoma.tutoriasapi.dto.TutoriaRequest;
import co.edu.autonoma.tutoriasapi.dto.TutoriaResponse;
import co.edu.autonoma.tutoriasapi.entity.Disponibilidad;
import co.edu.autonoma.tutoriasapi.entity.EstadoTutoria;
import co.edu.autonoma.tutoriasapi.entity.Tutoria;
import co.edu.autonoma.tutoriasapi.entity.Tutor;
import co.edu.autonoma.tutoriasapi.repository.DisponibilidadRepository;
import co.edu.autonoma.tutoriasapi.repository.ParticipanteRepository;
import co.edu.autonoma.tutoriasapi.repository.TutoriaRepository;
import co.edu.autonoma.tutoriasapi.repository.TutorRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class TutoriaService {

    private final TutoriaRepository tutoriaRepository;
    private final TutorRepository tutorRepository;
    private final DisponibilidadRepository disponibilidadRepository;
    private final ParticipanteRepository participanteRepository;

    @Transactional(readOnly = true)
    public List<TutoriaResponse> listar(Integer tutorId, EstadoTutoria estado) {
        if (tutorId != null && estado != null) {
            return tutoriaRepository.findByTutor_IdAndEstado(tutorId, estado).stream()
                    .map(this::toResponse).toList();
        }
        if (tutorId != null) {
            return tutoriaRepository.findByTutor_Id(tutorId).stream().map(this::toResponse).toList();
        }
        if (estado != null) {
            return tutoriaRepository.findAll().stream().filter(tutoria -> tutoria.getEstado() == estado)
                    .map(this::toResponse).toList();
        }
        return tutoriaRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public TutoriaResponse buscar(Integer id) {
        return toResponse(obtener(id));
    }

    public TutoriaResponse crear(TutoriaRequest request) {
        validarDatos(request);
        Tutor tutor = obtenerTutorParaActualizar(request.tutorId());
        validarDisponibilidad(tutor.getId(), request.fechaHoraInicio(), request.fechaHoraFin());
        validarCruce(tutor.getId(), request.fechaHoraInicio(), request.fechaHoraFin(), null);
        Tutoria tutoria = new Tutoria(tutor, request.tema().trim(), request.fechaHoraInicio(),
                request.fechaHoraFin(), request.limiteParticipantes());
        return toResponse(tutoriaRepository.save(tutoria));
    }

    public TutoriaResponse actualizar(Integer id, TutoriaRequest request) {
        validarDatos(request);
        Tutoria tutoria = obtenerForUpdate(id);
        if (participanteRepository.countByTutoria_Id(id) > request.limiteParticipantes()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El nuevo límite no puede ser menor al número de estudiantes inscritos");
        }
        Tutor tutor = obtenerTutorParaActualizar(request.tutorId());
        validarDisponibilidad(tutor.getId(), request.fechaHoraInicio(), request.fechaHoraFin());
        validarCruce(tutor.getId(), request.fechaHoraInicio(), request.fechaHoraFin(), id);
        tutoria.actualizar(tutor, request.tema().trim(), request.fechaHoraInicio(),
                request.fechaHoraFin(), request.limiteParticipantes());
        return toResponse(tutoriaRepository.save(tutoria));
    }

    public TutoriaResponse cambiarEstado(Integer id, EstadoTutoriaRequest request) {
        Tutoria tutoria = obtenerForUpdate(id);
        EstadoTutoria nuevoEstado = request.estado();
        if (tutoria.getEstado() == nuevoEstado) {
            return toResponse(tutoria);
        }
        if (nuevoEstado == EstadoTutoria.REALIZADA && tutoria.getFechaHoraFin().isAfter(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "La tutoría solo puede marcarse realizada después de su hora de finalización");
        }
        if (nuevoEstado == EstadoTutoria.CANCELADA && !tutoria.getFechaHoraInicio().isAfter(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se puede cancelar una tutoría que ya inició");
        }
        if (nuevoEstado == EstadoTutoria.REALIZADA) {
            tutoria.marcarRealizada();
        } else if (nuevoEstado == EstadoTutoria.CANCELADA) {
            tutoria.cancelar();
        } else {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Una tutoría cancelada o realizada no puede volver a programarse");
        }
        return toResponse(tutoriaRepository.save(tutoria));
    }

    public void cancelar(Integer id) {
        Tutoria tutoria = obtenerForUpdate(id);
        if (tutoria.getEstado() == EstadoTutoria.CANCELADA) {
            return;
        }
        if (!tutoria.getFechaHoraInicio().isAfter(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se puede cancelar una tutoría que ya inició");
        }
        tutoria.cancelar();
    }

    private void validarDatos(TutoriaRequest request) {
        LocalDateTime ahora = LocalDateTime.now();
        if (request.fechaHoraInicio() == null || request.fechaHoraFin() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "fechaHoraInicio y fechaHoraFin son obligatorias");
        }
        if (!request.fechaHoraInicio().isBefore(request.fechaHoraFin())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "fechaHoraInicio debe ser anterior a fechaHoraFin");
        }
        if (!request.fechaHoraInicio().isAfter(ahora)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La tutoría debe programarse para una fecha futura");
        }
        if (request.limiteParticipantes() == null || request.limiteParticipantes() < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "limiteParticipantes debe ser al menos 1");
        }
        if (request.tema() == null || request.tema().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "tema es obligatorio");
        }
    }

    private void validarDisponibilidad(Integer tutorId, LocalDateTime inicio, LocalDateTime fin) {
        boolean cubierta = disponibilidadRepository.findByTutor_IdAndActivaTrue(tutorId).stream()
                .filter(disponibilidad -> disponibilidad.getDiaSemana() == inicio.getDayOfWeek())
                .anyMatch(disponibilidad -> dentroDeFranja(disponibilidad, inicio, fin));
        if (!cubierta) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El horario de la tutoría no está dentro de una disponibilidad activa del tutor");
        }
    }

    private boolean dentroDeFranja(Disponibilidad disponibilidad, LocalDateTime inicio, LocalDateTime fin) {
        return !inicio.toLocalTime().isBefore(disponibilidad.getHoraInicio())
                && !fin.toLocalTime().isAfter(disponibilidad.getHoraFin())
                && inicio.toLocalDate().equals(fin.toLocalDate());
    }

    private void validarCruce(Integer tutorId, LocalDateTime inicio, LocalDateTime fin, Integer idIgnorado) {
        boolean seCruza = idIgnorado == null
                ? tutoriaRepository.existsByTutor_IdAndEstadoNotAndFechaHoraInicioLessThanAndFechaHoraFinGreaterThan(
                        tutorId, EstadoTutoria.CANCELADA, fin, inicio)
                : tutoriaRepository.existsByIdNotAndTutor_IdAndEstadoNotAndFechaHoraInicioLessThanAndFechaHoraFinGreaterThan(
                        idIgnorado, tutorId, EstadoTutoria.CANCELADA, fin, inicio);
        if (seCruza) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El tutor ya tiene otra tutoría programada que se cruza con ese horario");
        }
    }

    private Tutoria obtener(Integer id) {
        return tutoriaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No existe una tutoría con ese id"));
    }

    private Tutoria obtenerForUpdate(Integer id) {
        return tutoriaRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No existe una tutoría con ese id"));
    }

    private Tutor obtenerTutorParaActualizar(Integer id) {
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "tutorId es obligatorio");
        }
        return tutorRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No existe un tutor con ese id"));
    }

    private TutoriaResponse toResponse(Tutoria tutoria) {
        return new TutoriaResponse(tutoria.getId(), tutoria.getTutor().getId(), tutoria.getTutor().getNombre(),
                tutoria.getTema(), tutoria.getFechaHoraInicio(), tutoria.getFechaHoraFin(),
                tutoria.getLimiteParticipantes(), participanteRepository.countByTutoria_Id(tutoria.getId()),
                tutoria.getEstado());
    }
}
