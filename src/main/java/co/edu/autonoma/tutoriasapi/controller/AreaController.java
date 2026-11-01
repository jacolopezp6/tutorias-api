package co.edu.autonoma.tutoriasapi.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.autonoma.tutoriasapi.dto.AreaRequest;
import co.edu.autonoma.tutoriasapi.dto.AreaResponse;
import co.edu.autonoma.tutoriasapi.service.AreaService;

@RestController
@RequestMapping("/api/areas")
public class AreaController {

    private final AreaService areaService;

    public AreaController(AreaService areaService) {
        this.areaService = areaService;
    }

    @GetMapping
    public List<AreaResponse> listar() {
        return areaService.listar();
    }

    @GetMapping("/{id}")
    public AreaResponse buscar(@PathVariable Integer id) {
        return areaService.buscar(id);
    }

    @PostMapping
    public ResponseEntity<AreaResponse> crear(@Valid @RequestBody AreaRequest request) {
        AreaResponse creado = areaService.crear(request);
        return ResponseEntity.created(URI.create("/api/areas/" + creado.id())).body(creado);
    }

    @PutMapping("/{id}")
    public AreaResponse actualizar(@PathVariable Integer id, @Valid @RequestBody AreaRequest request) {
        return areaService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        areaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
