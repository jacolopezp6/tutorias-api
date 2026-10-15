package co.edu.autonoma.tutoriasapi.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import co.edu.autonoma.tutoriasapi.domain.EstadoTutoria;
import co.edu.autonoma.tutoriasapi.repository.TutoriaRepository;

@Component
public class PersistenciaCheck implements CommandLineRunner {

    private final TutoriaRepository tutoriaRepository;

    public PersistenciaCheck(TutoriaRepository tutoriaRepository) {
        this.tutoriaRepository = tutoriaRepository;
    }

    @Override
    public void run(String... args) {
        System.out.println("Total de tutorías: " + tutoriaRepository.count());
        System.out.println("Tutorías programadas: "
                + tutoriaRepository.countByEstado(EstadoTutoria.PROGRAMADA));
        tutoriaRepository.findAll().forEach(tutoria ->
                System.out.println("Tutoría " + tutoria.getId() + " · " + tutoria.getEstado()));
    }
}
