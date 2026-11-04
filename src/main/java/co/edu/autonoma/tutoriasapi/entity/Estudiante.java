package co.edu.autonoma.tutoriasapi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "estudiantes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Estudiante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(nullable = false, unique = true, length = 160)
    private String correo;

    public Estudiante(String nombre, String correo) {
        this.nombre = nombre;
        this.correo = correo;
    }

    public void actualizarDatos(String nombre, String correo) {
        this.nombre = nombre;
        this.correo = correo;
    }
}
