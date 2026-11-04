package co.edu.autonoma.tutoriasapi.config;

import java.util.UUID;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import co.edu.autonoma.tutoriasapi.entity.Area;
import co.edu.autonoma.tutoriasapi.entity.EstadoTutoria;
import co.edu.autonoma.tutoriasapi.repository.AreaRepository;
import co.edu.autonoma.tutoriasapi.repository.TutoriaRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class PersistenciaCheck implements CommandLineRunner {

    private final TutoriaRepository tutoriaRepository;
    private final AreaRepository areaRepository;

    @Override
    @Transactional
    public void run(String... args) {
        Area areaTemporal = areaRepository.saveAndFlush(
                new Area("Chequeo temporal " + UUID.randomUUID()));
        log.info("Área guardada: {}", areaTemporal.getId());

        log.info("Total de tutorías: {}", tutoriaRepository.count());
        log.info("Tutorías programadas: {}", tutoriaRepository.countByEstado(EstadoTutoria.PROGRAMADA));
        tutoriaRepository.findAll().forEach(tutoria ->
                log.info("Tutoría {} · {}", tutoria.getId(), tutoria.getEstado()));

        areaRepository.delete(areaTemporal);
        areaRepository.flush();
        log.info("Área temporal eliminada.");
    }
}
