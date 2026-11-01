package co.edu.autonoma.tutoriasapi.repository;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import co.edu.autonoma.tutoriasapi.entity.Tutor;

public interface TutorRepository extends JpaRepository<Tutor, Integer> {

    List<Tutor> findByArea_Id(Integer areaId);
    boolean existsByCorreoIgnoreCase(String correo);
    boolean existsByCorreoIgnoreCaseAndIdNot(String correo, Integer id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from Tutor t where t.id = :id")
    Optional<Tutor> findByIdForUpdate(@Param("id") Integer id);
}
