package co.edu.autonoma.tutoriasapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.autonoma.tutoriasapi.domain.Estudiante;

public interface EstudianteRepository extends JpaRepository<Estudiante, Integer> {
}
