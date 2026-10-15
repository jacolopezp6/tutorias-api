package co.edu.autonoma.tutoriasapi.config;

import java.util.UUID;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import co.edu.autonoma.tutoriasapi.domain.Area;
import co.edu.autonoma.tutoriasapi.domain.EstadoTutoria;
import co.edu.autonoma.tutoriasapi.repository.AreaRepository;
import co.edu.autonoma.tutoriasapi.repository.TutoriaRepository;

@Component
public class PersistenciaCheck implements CommandLineRunner {

    private final TutoriaRepository tutoriaRepository;
    private final AreaRepository areaRepository;

    public PersistenciaCheck(TutoriaRepository tutoriaRepository, AreaRepository areaRepository) {
        this.tutoriaRepository = tutoriaRepository;
        this.areaRepository = areaRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        Area areaTemporal = areaRepository.saveAndFlush(
                new Area("Chequeo temporal " + UUID.randomUUID()));
        System.out.println("Área guardada: " + areaTemporal.getId());

        System.out.println("Total de tutorías: " + tutoriaRepository.count());
        System.out.println("Tutorías programadas: "
                + tutoriaRepository.countByEstado(EstadoTutoria.PROGRAMADA));
        tutoriaRepository.findAll().forEach(tutoria ->
                System.out.println("Tutoría " + tutoria.getId() + " · " + tutoria.getEstado()));

        areaRepository.delete(areaTemporal);
        areaRepository.flush();
        System.out.println("Área temporal eliminada.");
    }
}
