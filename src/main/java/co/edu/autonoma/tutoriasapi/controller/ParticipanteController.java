package co.edu.autonoma.tutoriasapi.controller;

import java.net.URI;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.autonoma.tutoriasapi.dto.ParticipanteRequest;
import co.edu.autonoma.tutoriasapi.dto.ParticipanteResponse;
import co.edu.autonoma.tutoriasapi.service.ParticipanteService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tutorias/{tutoriaId}/participantes")
@RequiredArgsConstructor
public class ParticipanteController {

    private final ParticipanteService participanteService;

    @GetMapping
    public List<ParticipanteResponse> listar(@PathVariable Integer tutoriaId) {
        return participanteService.listarPorTutoria(tutoriaId);
    }

    @PostMapping
    public ResponseEntity<ParticipanteResponse> inscribir(@PathVariable Integer tutoriaId,
                                                          @Valid @RequestBody ParticipanteRequest request) {
        ParticipanteResponse creado = participanteService.inscribir(tutoriaId, request);
        return ResponseEntity.created(URI.create("/api/participantes/" + creado.id())).body(creado);
    }

    @DeleteMapping("/{participanteId}")
    public ResponseEntity<Void> retirar(@PathVariable Integer participanteId) {
        participanteService.retirar(participanteId);
        return ResponseEntity.noContent().build();
    }
}
