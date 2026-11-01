package co.edu.autonoma.tutoriasapi.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ApiReadyMessage {

    @EventListener(ApplicationReadyEvent.class)
    public void mostrarDireccion() {
        System.out.println("API lista para probar en Postman: http://localhost:8080");
        System.out.println("Ejemplo: GET http://localhost:8080/api/tutores");
    }
}
