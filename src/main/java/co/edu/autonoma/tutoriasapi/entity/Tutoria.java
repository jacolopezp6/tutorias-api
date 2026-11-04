package co.edu.autonoma.tutoriasapi.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tutorias")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tutoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tutor_id", nullable = false)
    private Tutor tutor;

    @Column(nullable = false, length = 160)
    private String tema;

    @Column(name = "fecha_hora_inicio", nullable = false)
    private LocalDateTime fechaHoraInicio;

    @Column(name = "fecha_hora_fin", nullable = false)
    private LocalDateTime fechaHoraFin;

    @Column(name = "limite_participantes", nullable = false)
    private Integer limiteParticipantes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoTutoria estado = EstadoTutoria.PROGRAMADA;

    @OneToMany(mappedBy = "tutoria", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter(AccessLevel.NONE)
    private List<Participante> participantes = new ArrayList<>();

    public Tutoria(Tutor tutor, String tema, LocalDateTime fechaHoraInicio,
                   LocalDateTime fechaHoraFin, Integer limiteParticipantes) {
        this.tutor = tutor;
        this.tema = tema;
        this.fechaHoraInicio = fechaHoraInicio;
        this.fechaHoraFin = fechaHoraFin;
        this.limiteParticipantes = limiteParticipantes;
    }

    public void actualizar(Tutor tutor, String tema, LocalDateTime fechaHoraInicio,
                           LocalDateTime fechaHoraFin, Integer limiteParticipantes) {
        exigirProgramada();
        this.tutor = tutor;
        this.tema = tema;
        this.fechaHoraInicio = fechaHoraInicio;
        this.fechaHoraFin = fechaHoraFin;
        this.limiteParticipantes = limiteParticipantes;
    }

    public void cancelar() {
        exigirProgramada();
        this.estado = EstadoTutoria.CANCELADA;
    }

    public void marcarRealizada() {
        exigirProgramada();
        this.estado = EstadoTutoria.REALIZADA;
    }

    public void agregarParticipante(Participante participante) {
        participantes.add(participante);
        participante.asignarTutoria(this);
    }

    public void retirarParticipante(Participante participante) {
        participantes.remove(participante);
        participante.quitarDeTutoria();
    }

    private void exigirProgramada() {
        if (estado != EstadoTutoria.PROGRAMADA) {
            throw new IllegalStateException("Solo se pueden cambiar tutorías que estén programadas");
        }
    }
}
