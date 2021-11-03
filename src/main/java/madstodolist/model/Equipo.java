package madstodolist.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table(name = "equipos")
public class Equipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String nombre;

    private Equipo() {}

    public Equipo(String nombre) {
        this.nombre = nombre;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getNombre() {
        return nombre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Equipo equipo = (Equipo) o;
        if (id != null && equipo.id != null)
            // Si tenemos los ID, comparamos por ID
            return Objects.equals(id, equipo.id);
        // sino comparamos por campos obligatorios
        return nombre.equals(equipo.nombre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre);
    }
}
