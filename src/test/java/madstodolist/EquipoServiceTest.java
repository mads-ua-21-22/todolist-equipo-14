package madstodolist;

import madstodolist.model.Equipo;
import madstodolist.model.Usuario;
import madstodolist.service.EquipoService;
import madstodolist.service.UsuarioService;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.hamcrest.Matchers.allOf;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class EquipoServiceTest {

    @Autowired
    EquipoService equipoService;
    @Autowired
    UsuarioService usuarioService;

    @Test
    public void obtenerListadoEquipos() {
        // GIVEN
        // En el application.properties se cargan los datos de prueba del fichero datos-test.sql

        // WHEN
        List<Equipo> equipos = equipoService.findAllOrderedByName();

        // THEN
        assertThat(equipos).hasSize(2);
        assertThat(equipos.get(0).getNombre()).isEqualTo("Proyecto A1");
        assertThat(equipos.get(1).getNombre()).isEqualTo("Proyecto P1");
    }

    @Test
    public void obtenerEquipo() {
        // GIVEN
        // En el application.properties se cargan los datos de prueba del fichero datos-test.sql

        // WHEN
        Equipo equipo = equipoService.findById(1L);

        // THEN
        assertThat(equipo.getNombre()).isEqualTo("Proyecto P1");
        // Comprobamos que la relación con Usuarios es lazy: al
        // intentar acceder a la colección de usuarios se debe lanzar una
        // excepción de tipo LazyInitializationException.
        assertThatThrownBy(() -> {
            equipo.getUsuarios().size();
        }).isInstanceOf(LazyInitializationException.class);
    }

    @Test
    public void comprobarRelacionUsuarioEquipos() {
        // GIVEN
        // En el application.properties se cargan los datos de prueba del fichero datos-test.sql

        // WHEN
        Usuario usuario = usuarioService.findById(1L);

        // THEN

        assertThat(usuario.getEquipos()).hasSize(1);
    }

    @Test
    public void obtenerUsuariosEquipo() {
        // GIVEN
        // En el application.properties se cargan los datos de prueba del fichero datos-test.sql

        // WHEN
        List<Usuario> usuarios = equipoService.usuariosEquipo(1L);

        // THEN
        assertThat(usuarios).hasSize(1);
        assertThat(usuarios.get(0).getEmail()).isEqualTo("user@ua");
        // Comprobamos que la relación entre usuarios y equipos es eager
        // Primero comprobamos que la colección de equipos tiene 1 elemento
        assertThat(usuarios.get(0).getEquipos()).hasSize(1);
        // Y después que el elemento es el equipo Proyecto P1
        assertThat(usuarios.get(0).getEquipos().stream().findFirst().get().getNombre()).isEqualTo("Proyecto P1");
    }

    @Test
    @Transactional
    public void comprobarAñadirUsuarioEquipo() {

        equipoService.addUsuarioEquipo(1L, 3L);
        List<Usuario> usuarios = equipoService.usuariosEquipo(1L);

        assertThat(usuarios.contains(usuarioService.findById(3L)));
    }

    @Test
    @Transactional
    public void comprobarBorrarUsuarioEquipo() {

        equipoService.borrarUsuarioEquipo(1L, 1L);
        List<Usuario> usuarios = equipoService.usuariosEquipo(1L);


        // THEN
        assertThat(usuarios).hasSize(0);

    }

    @Test
    @Transactional
    public void comprobarCrearEquipoEquipo() {
        List<Equipo> equipos = equipoService.findAllOrderedByName();
        assertThat(equipos).hasSize(2);
        equipoService.crearEquipo("PRUEBA", "X", "XXXX");
        equipos = equipoService.findAllOrderedByName();
        assertThat(equipos).hasSize(3);
    }

    @Test
    @Transactional
    public void comprobarRenombrarEquipoEquipo() {
        equipoService.renombrarEquipo(1L,"CAMBIO", "X");
        assertThat(equipoService.findById(1l).getNombre().equals("CAMBIO"));
    }

    @Test
    @Transactional
    public void comprobarBorrarEquipo() {

        equipoService.borrarEquipo(1L);
        List<Equipo> equipos = equipoService.findAllOrderedByName();
        assertThat(equipos).hasSize(1);
    }
}
