package co.edu.autonoma.tutoriasapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.autonoma.tutoriasapi.entity.Estudiante;

public interface EstudianteRepository extends JpaRepository<Estudiante, Integer> {
}
