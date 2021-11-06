package madstodolist;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.model.Equipo;
import madstodolist.model.Tarea;
import madstodolist.model.Usuario;
import madstodolist.model.UsuarioRepository;
import madstodolist.service.TareaService;
import madstodolist.service.UsuarioService;
import madstodolist.service.EquipoService;
import madstodolist.service.UsuarioServiceException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
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
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class EquipoWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private TareaService tareaService;

    @MockBean
    private EquipoService equipoService;

    // Al mocker el manegerUserSession, no lanza la excepción cuando
    // se intenta comprobar si un usuario está logeado
    @MockBean
    private ManagerUserSession managerUserSession;
    private Object UsuarioServiceException;

    @Test
    public void getListaEquipos() throws Exception {
        Usuario usuario = new Usuario("Usuario@ua");
        usuario.setId(1L);
        usuario.setNombre("Usuario");

        Equipo equipo = new Equipo("EQUIPO1");
        equipo.setId(1L);
        Equipo equipo1 = new Equipo("EQUIPO2");
        equipo.setId(2L);

        List<Equipo> equipos = new ArrayList<Equipo>();
        equipos.add(equipo);
        equipos.add(equipo1);

        when(equipoService.findAllOrderedByName()).thenReturn(equipos);
        when(usuarioService.findById(0L)).thenReturn(usuario);


        this.mockMvc.perform(get("/equipos"))
                .andExpect(content().string(allOf(containsString("EQUIPO1"),
                        containsString("EQUIPO2"))));
    }

    @Test
    public void getEquipoDevuelveForm() throws Exception {
        Usuario usuario = new Usuario("domingo@ua.es");
        usuario.setId(1L);
        usuario.setNombre("Usuario");

        Equipo equipo = new Equipo("EQUIPO1");
        equipo.setId(1L);

        when(usuarioService.findById(0L)).thenReturn(usuario);
        when(equipoService.findById(1L)).thenReturn(equipo);

        this.mockMvc.perform(get("/equipos/1/"))
                .andExpect(content().string(containsString("EQUIPO1")));
    }

}
