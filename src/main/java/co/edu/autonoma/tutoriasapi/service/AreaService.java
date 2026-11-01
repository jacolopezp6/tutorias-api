package co.edu.autonoma.tutoriasapi.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import co.edu.autonoma.tutoriasapi.dto.AreaRequest;
import co.edu.autonoma.tutoriasapi.dto.AreaResponse;
import co.edu.autonoma.tutoriasapi.entity.Area;
import co.edu.autonoma.tutoriasapi.repository.AreaRepository;

@Service
@Transactional
public class AreaService {

    private final AreaRepository areaRepository;

    public AreaService(AreaRepository areaRepository) {
        this.areaRepository = areaRepository;
    }

    @Transactional(readOnly = true)
    public List<AreaResponse> listar() {
        return areaRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public AreaResponse buscar(Integer id) {
        return toResponse(obtenerArea(id));
    }

    public AreaResponse crear(AreaRequest request) {
        if (request == null || request.nombre() == null || request.nombre().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "nombre es obligatorio");
        }
        return toResponse(areaRepository.save(new Area(request.nombre().trim())));
    }

    public AreaResponse actualizar(Integer id, AreaRequest request) {
        if (request == null || request.nombre() == null || request.nombre().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "nombre es obligatorio");
        }
        Area area = obtenerArea(id);
        area.setNombre(request.nombre().trim());
        return toResponse(areaRepository.save(area));
    }

    public void eliminar(Integer id) {
        areaRepository.delete(obtenerArea(id));
    }

    private Area obtenerArea(Integer id) {
        return areaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe un área con ese id"));
    }

    private AreaResponse toResponse(Area area) {
        return new AreaResponse(area.getId(), area.getNombre());
    }
}
