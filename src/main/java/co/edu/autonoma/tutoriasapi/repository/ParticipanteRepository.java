package co.edu.autonoma.tutoriasapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.autonoma.tutoriasapi.domain.Participante;

public interface ParticipanteRepository extends JpaRepository<Participante, Integer> {

    List<Participante> findByTutoria_Id(Integer tutoriaId);

    boolean existsByTutoria_IdAndEstudiante_Id(Integer tutoriaId, Integer estudianteId);
}
