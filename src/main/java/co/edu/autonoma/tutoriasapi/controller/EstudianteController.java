package co.edu.autonoma.tutoriasapi.controller;

import java.net.URI;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.autonoma.tutoriasapi.dto.EstudianteRequest;
import co.edu.autonoma.tutoriasapi.dto.EstudianteResponse;
import co.edu.autonoma.tutoriasapi.service.EstudianteService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/estudiantes")
@RequiredArgsConstructor
public class EstudianteController {

    private final EstudianteService estudianteService;

    @GetMapping
    public List<EstudianteResponse> listar() {
        return estudianteService.listar();
    }

    @GetMapping("/{id}")
    public EstudianteResponse buscar(@PathVariable Integer id) {
        return estudianteService.buscar(id);
    }

    @PostMapping
    public ResponseEntity<EstudianteResponse> crear(@Valid @RequestBody EstudianteRequest request) {
        EstudianteResponse creado = estudianteService.crear(request);
        return ResponseEntity.created(URI.create("/api/estudiantes/" + creado.id())).body(creado);
    }

    @PutMapping("/{id}")
    public EstudianteResponse actualizar(@PathVariable Integer id, @Valid @RequestBody EstudianteRequest request) {
        return estudianteService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        estudianteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
