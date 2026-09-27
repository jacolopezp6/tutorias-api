package co.edu.autonoma.tutoriasapi.service;

import org.springframework.stereotype.Service;
import co.edu.autonoma.tutoriasapi.dto.EstadoResponse;

@Service
public class EstadoService {

    public EstadoResponse consultarEstado() {
        return new EstadoResponse(
                "tutorias-api",
                "disponible"
        );
    }
}