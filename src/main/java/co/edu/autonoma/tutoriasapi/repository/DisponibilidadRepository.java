package co.edu.autonoma.tutoriasapi.repository;

import java.util.List;
import java.time.DayOfWeek;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.autonoma.tutoriasapi.entity.Disponibilidad;

public interface DisponibilidadRepository extends JpaRepository<Disponibilidad, Integer> {

    List<Disponibilidad> findByTutor_IdAndActivaTrue(Integer tutorId);
    List<Disponibilidad> findByTutor_IdAndDiaSemanaAndActivaTrue(Integer tutorId, DayOfWeek diaSemana);
    boolean existsByTutor_Id(Integer tutorId);
}
