package co.edu.autonoma.tutoriasapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.autonoma.tutoriasapi.service.ParticipanteService;

@RestController
@RequestMapping("/api/participantes")
public class ParticipanteResourceController {

    private final ParticipanteService participanteService;

    public ParticipanteResourceController(ParticipanteService participanteService) {
        this.participanteService = participanteService;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> retirar(@PathVariable Integer id) {
        participanteService.retirar(id);
        return ResponseEntity.noContent().build();
    }
}
