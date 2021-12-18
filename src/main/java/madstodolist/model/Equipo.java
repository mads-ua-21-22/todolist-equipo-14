package madstodolist.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "equipos")
public class Equipo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String nombre;
    private Long idAdmin;
    // Declaramos el tipo de recuperación como LAZY.
    // No haría falta porque es el tipo por defecto en una
    // relación a muchos.
    // Al recuperar un equipo NO SE RECUPERA AUTOMÁTICAMENTE
    // la lista de usuarios. Sólo se recupera cuando se accede al
    // atributo 'usuarios'; entonces se genera una query en la
    // BD que devuelve todos los usuarios del equipo y rellena el
    // atributo.
    private String descripcion;

    @Column(nullable = true, length = 64)
    private String image;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "equipo_usuario",
            joinColumns = { @JoinColumn(name = "fk_equipo") },
            inverseJoinColumns = {@JoinColumn(name = "fk_usuario")})
    Set<Usuario> usuarios = new HashSet<>();

    //Tareas de Equipo
    @OneToMany(mappedBy = "equipo", fetch = FetchType.EAGER)
    Set<Tarea> tareas = new HashSet<>();
    public Set<Tarea> getTareas() {
        return tareas;
    }
    public void setTareas(Set<Tarea> tareas) {
        this.tareas = tareas;
    }
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

    public String getNombre() { return nombre; }

    public void setNombre(String nombre) { this.nombre = nombre; }

    public Long getIdAdmin(){
        return idAdmin;
    }

    public void setIdAdmin(Long idAdmin){
        this.idAdmin = idAdmin;
    }

    public Set<Usuario> getUsuarios() { return usuarios; }

    public void setUsuarios(Set<Usuario> usuarios) { this.usuarios = usuarios; }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getImage() {return image;}

    public void setImage(String image) {this.image = image;}

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
