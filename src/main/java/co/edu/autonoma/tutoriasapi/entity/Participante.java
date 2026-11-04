package co.edu.autonoma.tutoriasapi.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "participantes", uniqueConstraints = {
        @UniqueConstraint(name = "uk_participante_tutoria_estudiante",
                columnNames = {"tutoria_id", "estudiante_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Participante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tutoria_id", nullable = false)
    private Tutoria tutoria;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    @Column(name = "fecha_inscripcion", nullable = false, updatable = false)
    private LocalDateTime fechaInscripcion = LocalDateTime.now();

    public Participante(Estudiante estudiante) {
        this.estudiante = estudiante;
    }

    void asignarTutoria(Tutoria tutoria) {
        this.tutoria = tutoria;
    }

    void quitarDeTutoria() {
        this.tutoria = null;
    }
}
