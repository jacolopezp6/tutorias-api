package co.edu.autonoma.tutoriasapi.repository;

import java.util.List;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

import co.edu.autonoma.tutoriasapi.entity.EstadoTutoria;
import co.edu.autonoma.tutoriasapi.entity.Tutoria;

public interface TutoriaRepository extends JpaRepository<Tutoria, Integer> {

    List<Tutoria> findByTutor_Id(Integer tutorId);

    List<Tutoria> findByTutor_IdAndEstado(Integer tutorId, EstadoTutoria estado);

    long countByEstado(EstadoTutoria estado);

    boolean existsByTutor_Id(Integer tutorId);

    boolean existsByTutor_IdAndEstadoNotAndFechaHoraInicioLessThanAndFechaHoraFinGreaterThan(
            Integer tutorId, EstadoTutoria estadoExcluido, LocalDateTime fin, LocalDateTime inicio);

    boolean existsByIdNotAndTutor_IdAndEstadoNotAndFechaHoraInicioLessThanAndFechaHoraFinGreaterThan(
            Integer id, Integer tutorId, EstadoTutoria estadoExcluido, LocalDateTime fin, LocalDateTime inicio);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from Tutoria t where t.id = :id")
    Optional<Tutoria> findByIdForUpdate(@Param("id") Integer id);
}
