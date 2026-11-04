package co.edu.autonoma.tutoriasapi.controller;

import java.net.URI;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.autonoma.tutoriasapi.dto.EstadoTutoriaRequest;
import co.edu.autonoma.tutoriasapi.dto.TutoriaRequest;
import co.edu.autonoma.tutoriasapi.dto.TutoriaResponse;
import co.edu.autonoma.tutoriasapi.entity.EstadoTutoria;
import co.edu.autonoma.tutoriasapi.service.TutoriaService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tutorias")
@RequiredArgsConstructor
public class TutoriaController {

    private final TutoriaService tutoriaService;

    @GetMapping
    public List<TutoriaResponse> listar(@RequestParam(required = false) Integer tutorId,
                                        @RequestParam(required = false) EstadoTutoria estado) {
        return tutoriaService.listar(tutorId, estado);
    }

    @GetMapping("/{id}")
    public TutoriaResponse buscar(@PathVariable Integer id) {
        return tutoriaService.buscar(id);
    }

    @PostMapping
    public ResponseEntity<TutoriaResponse> crear(@Valid @RequestBody TutoriaRequest request) {
        TutoriaResponse creada = tutoriaService.crear(request);
        return ResponseEntity.created(URI.create("/api/tutorias/" + creada.id())).body(creada);
    }

    @PutMapping("/{id}")
    public TutoriaResponse actualizar(@PathVariable Integer id, @Valid @RequestBody TutoriaRequest request) {
        return tutoriaService.actualizar(id, request);
    }

    @PatchMapping("/{id}/estado")
    public TutoriaResponse cambiarEstado(@PathVariable Integer id,
                                         @Valid @RequestBody EstadoTutoriaRequest request) {
        return tutoriaService.cambiarEstado(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable Integer id) {
        tutoriaService.cancelar(id);
        return ResponseEntity.noContent().build();
    }
}
