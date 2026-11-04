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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.autonoma.tutoriasapi.dto.DisponibilidadRequest;
import co.edu.autonoma.tutoriasapi.dto.DisponibilidadResponse;
import co.edu.autonoma.tutoriasapi.service.DisponibilidadService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/disponibilidades")
@RequiredArgsConstructor
public class DisponibilidadController {

    private final DisponibilidadService disponibilidadService;

    @GetMapping
    public List<DisponibilidadResponse> listarPorTutor(@RequestParam Integer tutorId) {
        return disponibilidadService.listarPorTutor(tutorId);
    }

    @PostMapping
    public ResponseEntity<DisponibilidadResponse> crear(@Valid @RequestBody DisponibilidadRequest request) {
        DisponibilidadResponse creada = disponibilidadService.crear(request);
        return ResponseEntity.created(URI.create("/api/disponibilidades/" + creada.id())).body(creada);
    }

    @PutMapping("/{id}")
    public DisponibilidadResponse actualizar(@PathVariable Integer id,
                                             @Valid @RequestBody DisponibilidadRequest request) {
        return disponibilidadService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        disponibilidadService.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}
