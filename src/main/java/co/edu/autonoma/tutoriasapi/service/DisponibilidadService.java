package co.edu.autonoma.tutoriasapi.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import co.edu.autonoma.tutoriasapi.dto.DisponibilidadRequest;
import co.edu.autonoma.tutoriasapi.dto.DisponibilidadResponse;
import co.edu.autonoma.tutoriasapi.entity.Disponibilidad;
import co.edu.autonoma.tutoriasapi.entity.Tutor;
import co.edu.autonoma.tutoriasapi.repository.DisponibilidadRepository;
import co.edu.autonoma.tutoriasapi.repository.TutorRepository;

@Service
@Transactional
public class DisponibilidadService {

    private final DisponibilidadRepository disponibilidadRepository;
    private final TutorRepository tutorRepository;

    public DisponibilidadService(DisponibilidadRepository disponibilidadRepository, TutorRepository tutorRepository) {
        this.disponibilidadRepository = disponibilidadRepository;
        this.tutorRepository = tutorRepository;
    }

    @Transactional(readOnly = true)
    public List<DisponibilidadResponse> listarPorTutor(Integer tutorId) {
        obtenerTutor(tutorId);
        return disponibilidadRepository.findByTutor_IdAndActivaTrue(tutorId).stream().map(this::toResponse).toList();
    }

    public DisponibilidadResponse crear(DisponibilidadRequest request) {
        validarHorario(request);
        Tutor tutor = obtenerTutorParaActualizar(request.tutorId());
        validarCruce(request, tutor.getId(), null);
        return toResponse(disponibilidadRepository.save(new Disponibilidad(tutor, request.diaSemana(),
                request.horaInicio(), request.horaFin())));
    }

    public DisponibilidadResponse actualizar(Integer id, DisponibilidadRequest request) {
        validarHorario(request);
        Disponibilidad disponibilidad = disponibilidadRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No existe una disponibilidad con ese id"));
        Tutor tutor = obtenerTutorParaActualizar(request.tutorId());
        validarCruce(request, tutor.getId(), id);
        disponibilidad.setTutor(tutor);
        disponibilidad.setDiaSemana(request.diaSemana());
        disponibilidad.setHoraInicio(request.horaInicio());
        disponibilidad.setHoraFin(request.horaFin());
        disponibilidad.setActiva(true);
        return toResponse(disponibilidadRepository.save(disponibilidad));
    }

    public void desactivar(Integer id) {
        Disponibilidad disponibilidad = disponibilidadRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No existe una disponibilidad con ese id"));
        disponibilidad.setActiva(false);
    }

    private void validarHorario(DisponibilidadRequest request) {
        if (request.horaInicio() == null || request.horaFin() == null || request.diaSemana() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "diaSemana, horaInicio y horaFin son obligatorios");
        }
        if (!request.horaInicio().isBefore(request.horaFin())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "horaInicio debe ser anterior a horaFin");
        }
    }

    private void validarCruce(DisponibilidadRequest request, Integer tutorId, Integer idIgnorado) {
        boolean seCruza = disponibilidadRepository.findByTutor_IdAndDiaSemanaAndActivaTrue(
                tutorId, request.diaSemana()).stream()
                .filter(actual -> idIgnorado == null || !actual.getId().equals(idIgnorado))
                .anyMatch(actual -> request.horaInicio().isBefore(actual.getHoraFin())
                        && request.horaFin().isAfter(actual.getHoraInicio()));
        if (seCruza) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El horario se cruza con otra disponibilidad activa del tutor");
        }
    }

    private Tutor obtenerTutor(Integer id) {
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "tutorId es obligatorio");
        }
        return tutorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No existe un tutor con ese id"));
    }

    private Tutor obtenerTutorParaActualizar(Integer id) {
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "tutorId es obligatorio");
        }
        return tutorRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No existe un tutor con ese id"));
    }

    private DisponibilidadResponse toResponse(Disponibilidad disponibilidad) {
        return new DisponibilidadResponse(disponibilidad.getId(), disponibilidad.getTutor().getId(),
                disponibilidad.getDiaSemana(), disponibilidad.getHoraInicio(),
                disponibilidad.getHoraFin(), disponibilidad.isActiva());
    }
}
