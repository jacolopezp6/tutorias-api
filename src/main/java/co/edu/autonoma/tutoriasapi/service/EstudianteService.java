package co.edu.autonoma.tutoriasapi.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import lombok.RequiredArgsConstructor;

import co.edu.autonoma.tutoriasapi.dto.EstudianteRequest;
import co.edu.autonoma.tutoriasapi.dto.EstudianteResponse;
import co.edu.autonoma.tutoriasapi.entity.Estudiante;
import co.edu.autonoma.tutoriasapi.repository.EstudianteRepository;
import co.edu.autonoma.tutoriasapi.repository.ParticipanteRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class EstudianteService {

    private final EstudianteRepository estudianteRepository;
    private final ParticipanteRepository participanteRepository;

    @Transactional(readOnly = true)
    public List<EstudianteResponse> listar() {
        return estudianteRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public EstudianteResponse buscar(Integer id) {
        return toResponse(obtener(id));
    }

    public EstudianteResponse crear(EstudianteRequest request) {
        String correo = request.correo().trim();
        if (estudianteRepository.existsByCorreoIgnoreCase(correo)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un estudiante con ese correo");
        }
        return toResponse(estudianteRepository.save(new Estudiante(request.nombre().trim(), correo)));
    }

    public EstudianteResponse actualizar(Integer id, EstudianteRequest request) {
        Estudiante estudiante = obtener(id);
        String correo = request.correo().trim();
        if (estudianteRepository.existsByCorreoIgnoreCaseAndIdNot(correo, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un estudiante con ese correo");
        }
        estudiante.actualizarDatos(request.nombre().trim(), correo);
        return toResponse(estudianteRepository.save(estudiante));
    }

    public void eliminar(Integer id) {
        Estudiante estudiante = obtener(id);
        if (participanteRepository.existsByEstudiante_Id(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se puede eliminar un estudiante que tiene inscripciones registradas");
        }
        estudianteRepository.delete(estudiante);
    }

    private Estudiante obtener(Integer id) {
        return estudianteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No existe un estudiante con ese id"));
    }

    private EstudianteResponse toResponse(Estudiante estudiante) {
        return new EstudianteResponse(estudiante.getId(), estudiante.getNombre(), estudiante.getCorreo());
    }
}
