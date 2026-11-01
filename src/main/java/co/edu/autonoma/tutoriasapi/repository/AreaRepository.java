package co.edu.autonoma.tutoriasapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.autonoma.tutoriasapi.entity.Area;

public interface AreaRepository extends JpaRepository<Area, Integer> {
    boolean existsByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Integer id);
}
