package co.edu.autonoma.tutoriasapi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tutores")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tutor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(nullable = false, unique = true, length = 160)
    private String correo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "area_id", nullable = false)
    private Area area;

    public Tutor(String nombre, String correo, Area area) {
        this.nombre = nombre;
        this.correo = correo;
        this.area = area;
    }

    public void actualizarDatos(String nombre, String correo, Area area) {
        this.nombre = nombre;
        this.correo = correo;
        this.area = area;
    }
}
