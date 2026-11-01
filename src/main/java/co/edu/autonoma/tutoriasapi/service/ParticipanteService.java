package co.edu.autonoma.tutoriasapi.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import co.edu.autonoma.tutoriasapi.dto.ParticipanteRequest;
import co.edu.autonoma.tutoriasapi.dto.ParticipanteResponse;
import co.edu.autonoma.tutoriasapi.entity.EstadoTutoria;
import co.edu.autonoma.tutoriasapi.entity.Estudiante;
import co.edu.autonoma.tutoriasapi.entity.Participante;
import co.edu.autonoma.tutoriasapi.entity.Tutoria;
import co.edu.autonoma.tutoriasapi.repository.EstudianteRepository;
import co.edu.autonoma.tutoriasapi.repository.ParticipanteRepository;
import co.edu.autonoma.tutoriasapi.repository.TutoriaRepository;

@Service
@Transactional
public class ParticipanteService {

    private final ParticipanteRepository participanteRepository;
    private final TutoriaRepository tutoriaRepository;
    private final EstudianteRepository estudianteRepository;

    public ParticipanteService(ParticipanteRepository participanteRepository,
                               TutoriaRepository tutoriaRepository,
                               EstudianteRepository estudianteRepository) {
        this.participanteRepository = participanteRepository;
        this.tutoriaRepository = tutoriaRepository;
        this.estudianteRepository = estudianteRepository;
    }

    @Transactional(readOnly = true)
    public List<ParticipanteResponse> listarPorTutoria(Integer tutoriaId) {
        obtenerTutoria(tutoriaId);
        return participanteRepository.findByTutoria_Id(tutoriaId).stream().map(this::toResponse).toList();
    }

    public ParticipanteResponse inscribir(Integer tutoriaId, ParticipanteRequest request) {
        Tutoria tutoria = tutoriaRepository.findByIdForUpdate(tutoriaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No existe una tutoría con ese id"));
        if (tutoria.getEstado() != EstadoTutoria.PROGRAMADA) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Solo se permiten inscripciones en tutorías programadas");
        }
        if (!tutoria.getFechaHoraInicio().isAfter(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se puede inscribir a una tutoría que ya inició");
        }
        Integer estudianteId = request.estudianteId();
        if (participanteRepository.existsByTutoria_IdAndEstudiante_Id(tutoriaId, estudianteId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El estudiante ya está inscrito en esta tutoría");
        }
        if (participanteRepository.countByTutoria_Id(tutoriaId) >= tutoria.getLimiteParticipantes()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "La tutoría ya alcanzó el límite de participantes");
        }
        Estudiante estudiante = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No existe un estudiante con ese id"));
        Participante participante = new Participante(estudiante);
        tutoria.agregarParticipante(participante);
        return toResponse(participanteRepository.save(participante));
    }

    public void retirar(Integer participanteId) {
        Participante participante = participanteRepository.findById(participanteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No existe una inscripción con ese id"));
        if (!participante.getTutoria().getFechaHoraInicio().isAfter(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Solo se puede retirar al estudiante antes del inicio de la tutoría");
        }
        participante.getTutoria().retirarParticipante(participante);
        participanteRepository.delete(participante);
    }

    private Tutoria obtenerTutoria(Integer id) {
        return tutoriaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No existe una tutoría con ese id"));
    }

    private ParticipanteResponse toResponse(Participante participante) {
        return new ParticipanteResponse(participante.getId(), participante.getTutoria().getId(),
                participante.getEstudiante().getId(), participante.getEstudiante().getNombre(),
                participante.getEstudiante().getCorreo(), participante.getFechaInscripcion());
    }
}
