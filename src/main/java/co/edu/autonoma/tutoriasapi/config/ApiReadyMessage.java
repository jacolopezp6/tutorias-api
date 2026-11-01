package co.edu.autonoma.tutoriasapi.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ApiReadyMessage {

    @EventListener(ApplicationReadyEvent.class)
    public void mostrarDireccion(ApplicationReadyEvent evento) {
        ApplicationContext contexto = evento.getApplicationContext();
        String puerto = contexto.getEnvironment().getProperty("local.server.port",
                contexto.getEnvironment().getProperty("server.port", "8080"));
        String baseUrl = "http://localhost:" + puerto;
        System.out.println("API lista para probar en Postman: " + baseUrl);
        System.out.println("Ejemplo: GET " + baseUrl + "/api/tutores");
    }
}
