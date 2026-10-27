package co.edu.autonoma.tutoriasapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.autonoma.tutoriasapi.domain.Tutor;

public interface TutorRepository extends JpaRepository<Tutor, Integer> {

    List<Tutor> findByArea_Id(Integer areaId);
}
