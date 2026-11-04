package co.edu.autonoma.tutoriasapi.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import lombok.RequiredArgsConstructor;

import co.edu.autonoma.tutoriasapi.dto.AreaRequest;
import co.edu.autonoma.tutoriasapi.dto.AreaResponse;
import co.edu.autonoma.tutoriasapi.entity.Area;
import co.edu.autonoma.tutoriasapi.repository.AreaRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class AreaService {

    private final AreaRepository areaRepository;

    @Transactional(readOnly = true)
    public List<AreaResponse> listar() {
        return areaRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public AreaResponse buscar(Integer id) {
        return toResponse(obtenerArea(id));
    }

    public AreaResponse crear(AreaRequest request) {
        if (areaRepository.existsByNombreIgnoreCase(request.nombre().trim())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un área con ese nombre");
        }
        return toResponse(areaRepository.save(new Area(request.nombre().trim())));
    }

    public AreaResponse actualizar(Integer id, AreaRequest request) {
        Area area = obtenerArea(id);
        if (areaRepository.existsByNombreIgnoreCaseAndIdNot(request.nombre().trim(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un área con ese nombre");
        }
        area.renombrar(request.nombre().trim());
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
