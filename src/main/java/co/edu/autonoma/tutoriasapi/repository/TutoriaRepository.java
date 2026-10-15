package co.edu.autonoma.tutoriasapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.autonoma.tutoriasapi.entity.EstadoTutoria;
import co.edu.autonoma.tutoriasapi.entity.Tutoria;

public interface TutoriaRepository extends JpaRepository<Tutoria, Integer> {

    List<Tutoria> findByTutor_Id(Integer tutorId);

    List<Tutoria> findByTutor_IdAndEstado(Integer tutorId, EstadoTutoria estado);
}
