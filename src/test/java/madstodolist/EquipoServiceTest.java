package madstodolist;

import madstodolist.model.Equipo;
import madstodolist.model.EquipoRepository;
import madstodolist.model.Usuario;
import madstodolist.model.UsuarioRepository;
import madstodolist.service.EquipoService;
import madstodolist.service.UsuarioService;
import madstodolist.service.UsuarioServiceException;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.NestedServletException;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
}