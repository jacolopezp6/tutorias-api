package co.edu.autonoma.tutoriasapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.autonoma.tutoriasapi.domain.Area;

public interface AreaRepository extends JpaRepository<Area, Integer> {
}
