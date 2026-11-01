package co.edu.autonoma.tutoriasapi.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import co.edu.autonoma.tutoriasapi.dto.TutorRequest;
import co.edu.autonoma.tutoriasapi.dto.TutorResponse;
import co.edu.autonoma.tutoriasapi.entity.Area;
import co.edu.autonoma.tutoriasapi.entity.Tutor;
import co.edu.autonoma.tutoriasapi.repository.AreaRepository;
import co.edu.autonoma.tutoriasapi.repository.TutorRepository;

@Service
@Transactional
public class TutorService {

    private final TutorRepository tutorRepository;
    private final AreaRepository areaRepository;

    public TutorService(TutorRepository tutorRepository, AreaRepository areaRepository) {
        this.tutorRepository = tutorRepository;
        this.areaRepository = areaRepository;
    }

    @Transactional(readOnly = true)
    public List<TutorResponse> listar() {
        return tutorRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public TutorResponse buscar(Integer id) {
        return toResponse(obtenerTutor(id));
    }

    public TutorResponse crear(TutorRequest request) {
        validar(request);
        Area area = obtenerArea(request.areaId());
        return toResponse(tutorRepository.save(new Tutor(request.nombre().trim(), request.correo().trim(), area)));
    }

    public TutorResponse actualizar(Integer id, TutorRequest request) {
        validar(request);
        Tutor tutor = obtenerTutor(id);
        tutor.setNombre(request.nombre().trim());
        tutor.setCorreo(request.correo().trim());
        tutor.setArea(obtenerArea(request.areaId()));
        return toResponse(tutorRepository.save(tutor));
    }

    public void eliminar(Integer id) {
        tutorRepository.delete(obtenerTutor(id));
    }

    private Tutor obtenerTutor(Integer id) {
        return tutorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe un tutor con ese id"));
    }

    private Area obtenerArea(Integer id) {
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "areaId es obligatorio");
        }
        return areaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe un área con ese id"));
    }

    private void validar(TutorRequest request) {
        if (request == null || request.nombre() == null || request.nombre().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "nombre es obligatorio");
        }
        if (request.correo() == null || request.correo().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "correo es obligatorio");
        }
        if (request.areaId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "areaId es obligatorio");
        }
    }

    private TutorResponse toResponse(Tutor tutor) {
        return new TutorResponse(tutor.getId(), tutor.getNombre(), tutor.getCorreo(),
                tutor.getArea().getId(), tutor.getArea().getNombre());
    }
}
