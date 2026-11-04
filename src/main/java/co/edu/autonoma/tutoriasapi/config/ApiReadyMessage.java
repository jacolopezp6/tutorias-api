package co.edu.autonoma.tutoriasapi.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ApiReadyMessage {

    @EventListener(ApplicationReadyEvent.class)
    public void mostrarDireccion(ApplicationReadyEvent evento) {
        ApplicationContext contexto = evento.getApplicationContext();
        String puerto = contexto.getEnvironment().getProperty("local.server.port",
                contexto.getEnvironment().getProperty("server.port", "8080"));
        String baseUrl = "http://localhost:" + puerto;
        log.info("API lista para probar en Postman: {}", baseUrl);
        log.info("Ejemplo: GET {}/api/tutores", baseUrl);
    }
}
