package co.edu.autonoma.tutoriasapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.autonoma.tutoriasapi.domain.Disponibilidad;

public interface DisponibilidadRepository extends JpaRepository<Disponibilidad, Integer> {

    List<Disponibilidad> findByTutor_IdAndActivaTrue(Integer tutorId);
}
