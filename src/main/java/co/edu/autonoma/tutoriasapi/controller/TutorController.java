package co.edu.autonoma.tutoriasapi.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.autonoma.tutoriasapi.dto.TutorRequest;
import co.edu.autonoma.tutoriasapi.dto.TutorResponse;
import co.edu.autonoma.tutoriasapi.service.TutorService;

@RestController
@RequestMapping("/api/tutores")
public class TutorController {

    private final TutorService tutorService;

    public TutorController(TutorService tutorService) {
        this.tutorService = tutorService;
    }

    @GetMapping
    public List<TutorResponse> listar() {
        return tutorService.listar();
    }

    @GetMapping("/{id}")
    public TutorResponse buscar(@PathVariable Integer id) {
        return tutorService.buscar(id);
    }

    @PostMapping
    public ResponseEntity<TutorResponse> crear(@RequestBody TutorRequest request) {
        TutorResponse creado = tutorService.crear(request);
        return ResponseEntity.created(URI.create("/api/tutores/" + creado.id())).body(creado);
    }

    @PutMapping("/{id}")
    public TutorResponse actualizar(@PathVariable Integer id, @RequestBody TutorRequest request) {
        return tutorService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        tutorService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
