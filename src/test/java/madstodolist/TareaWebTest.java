package madstodolist;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.model.Equipo;
import madstodolist.model.Tarea;
import madstodolist.model.Usuario;
import madstodolist.model.UsuarioRepository;
import madstodolist.service.TareaService;
import madstodolist.service.UsuarioService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TareaWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private TareaService tareaService;

    // Al mocker el manegerUserSession, no lanza la excepción cuando
    // se intenta comprobar si un usuario está logeado
    @MockBean
    private ManagerUserSession managerUserSession;
    private Object UsuarioServiceException;

    @Test
    public void getNuevaTareaDevuelveForm() throws Exception {
        Usuario usuario = new Usuario("domingo@ua.es");
        usuario.setId(1L);

        when(usuarioService.findById(1L)).thenReturn(usuario);

        this.mockMvc.perform(get("/usuarios/1/tareas/nueva"))
                .andExpect(content().string(containsString("action=\"/usuarios/1/tareas/nueva\"")));
    }

    // En este test usamos los datos cargados en el fichero de prueba
    @Test
    public void postNuevaTareaDevuelveListaConTarea() throws Exception {
        Usuario usuario = new Usuario("domingo@ua.es");
        usuario.setId(1L);

        when(usuarioService.findById(1L)).thenReturn(usuario);

        this.mockMvc.perform(post("/usuarios/1/tareas/nueva")
                    .param("titulo", "Estudiar examen MADS"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/usuarios/1/tareas"));
    }

    @Test
    public void getNuevaTareaDevuelveNotFoundCuandoNoExisteUsuario() throws Exception {

        when(usuarioService.findById(1L)).thenReturn(null);

        this.mockMvc.perform(get("/usuarios/1/tareas/nueva"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void editarTareaDevuelveForm() throws Exception {
        Tarea tarea = new Tarea(new Usuario("domingo@ua.es"), "Tarea de prueba");
        tarea.setId(1L);
        tarea.getUsuario().setId(1L);

        when(tareaService.findById(1L)).thenReturn(tarea);

        this.mockMvc.perform(get("/tareas/1/editar"))
                .andExpect(content().string(allOf(
                    // Contiene la acción para enviar el post a la URL correcta
                    containsString("action=\"/tareas/1/editar\""),
                    // Contiene el texto de la tarea a editar
                    containsString("Tarea de prueba"),
                    // Contiene enlace a listar tareas del usuario si se cancela la edición
                    containsString("href=\"/usuarios/1/tareas\""))));
    }

    // Usamos el mock para verificar que se ha llamado al método de añadir una tarea
    @Test
    public void postNuevaTareaDevuelveRedirectYAñadeTarea() throws Exception {
        Usuario usuario = new Usuario("Usuario");
        usuario.setId(1L);

        when(usuarioService.findById(1L)).thenReturn(usuario);


        this.mockMvc.perform(post("/usuarios/1/tareas/nueva")
                .param("titulo", "Estudiar examen MADS")
                .param("descripcion", "XX")
                .param("estado", "To Do")
                .param("prioridad", "Media"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usuarios/1/tareas"));

        // Verificamos que se ha llamado al método para
        // añadir una tarea con los parámetros correctos

        verify(tareaService).nuevaTareaUsuario(1L, "Estudiar examen MADS", "XX", "To Do","Media");
    }

    @Test
    public void getListaUsuarios() throws Exception {

        Usuario usuario = new Usuario("Usuario@ua");
        usuario.setId(1L);
        //Añado nombre para que no de error el navbar por usuario.nombre = null;
        usuario.setNombre("Usuario");

        Usuario usuario1 = new Usuario("Usuario1@ua");
        usuario.setId(2L);
        //usuario es admin para poder ver la lista de usuarios.
        usuario.setAdminApproved(true);

        List<Usuario> usuarios = new ArrayList<Usuario>();
        usuarios.add(usuario);
        usuarios.add(usuario1);

        when(usuarioService.getUsers()).thenReturn(usuarios);
        when(usuarioService.findById(0L)).thenReturn(usuario);

        this.mockMvc.perform(get("/usuarios"))
                .andExpect(content().string(allOf(containsString("Usuario1@ua"))));

    }

    @Test
    public void comprobarDescripcionUsuario() throws Exception {

        Usuario usuario = new Usuario("juan.gutierrez@gmail.com");
        Usuario usuario1 = new Usuario("Usuario1@ua");

        usuario.setId(1L);
        usuario.setNombre("Juan Gutiérrez");
        usuario.setPassword("123");
        //usuario es admin para poder ver la descripcion del usuario1.
        usuario.setAdminApproved(true);
        usuario1.setId(2L);
        usuario1.setNombre("Carlos");
        usuario1.setPassword("123");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        usuario.setFechaNacimiento(sdf.parse("1997-02-20"));
        usuario1.setFechaNacimiento(sdf.parse("1994-02-20"));

        when(usuarioService.findById(0L)).thenReturn(usuario);
        when(usuarioService.findById(1L)).thenReturn(usuario1);

        this.mockMvc.perform(get("/usuarios/1"))
                .andExpect(content().string(allOf(containsString("Usuario1@ua"),
                        containsString("Carlos"))));
    }

    @Test
    public void getDescripcionUsuarioDevuelveNotFoundCuandoNoExisteUsuario() throws Exception {

        when(usuarioService.findById(1L)).thenReturn(null);

        this.mockMvc.perform(get("/usuarios/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void comprobarDescripcionUsuarioNoAdmin() throws Exception {

        Usuario usuario = new Usuario("juan.gutierrez@gmail.com");
        Usuario usuario1 = new Usuario("Usuario1@ua");

        usuario.setId(1L);
        usuario.setNombre("Juan Gutiérrez");
        usuario.setPassword("123");
        //Igual que el anterior pero sin que el usuario sea admin (tambien se ponia dejar sin poner, es default false)
        usuario.setAdminApproved(false);
        usuario1.setId(2L);
        usuario1.setNombre("Carlos");
        usuario1.setPassword("123");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        usuario.setFechaNacimiento(sdf.parse("1997-02-20"));
        usuario1.setFechaNacimiento(sdf.parse("1994-02-20"));

        when(usuarioService.findById(0L)).thenReturn(usuario);
        when(usuarioService.findById(1L)).thenReturn(usuario1);

        Assertions.assertThrows(NestedServletException.class, () -> {
            this.mockMvc.perform(get("/usuarios/1"));
        });
    }

    @Test
    public void getListaUsuariosNoAdmin() throws Exception {

        Usuario usuario = new Usuario("Usuario@ua");
        usuario.setId(1L);
        //Añado nombre para que no de error el navbar por usuario.nombre = null;
        usuario.setNombre("Usuario");

        Usuario usuario1 = new Usuario("Usuario1@ua");
        usuario.setId(2L);

        usuario.setAdminApproved(false);

        List<Usuario> usuarios = new ArrayList<Usuario>();
        usuarios.add(usuario);
        usuarios.add(usuario1);

        when(usuarioService.getUsers()).thenReturn(usuarios);
        when(usuarioService.findById(0L)).thenReturn(usuario);

        Assertions.assertThrows(NestedServletException.class, () -> {
            this.mockMvc.perform(get("/usuarios"));
        });

    }

    @Test
    public void getListaUsuariosBotonBloquear() throws Exception {

        Usuario usuario = new Usuario("Usuario@ua");
        usuario.setId(1L);
        //Añado nombre para que no de error el navbar por usuario.nombre = null;
        usuario.setNombre("Usuario");

        Usuario usuario1 = new Usuario("Usuario1@ua");
        usuario.setId(2L);
        //usuario es admin para poder ver la lista de usuarios.
        usuario.setAdminApproved(true);

        List<Usuario> usuarios = new ArrayList<Usuario>();
        usuarios.add(usuario);
        usuarios.add(usuario1);

        when(usuarioService.getUsers()).thenReturn(usuarios);
        when(usuarioService.findById(0L)).thenReturn(usuario);

        this.mockMvc.perform(get("/usuarios"))
                .andExpect(content().string(allOf(containsString("Bloquear"))));

    }

    @Test
    public void getListaUsuariosBotonDesbloquear() throws Exception {

        Usuario usuario = new Usuario("Usuario@ua");
        usuario.setId(1L);
        //Añado nombre para que no de error el navbar por usuario.nombre = null;
        usuario.setNombre("Usuario");

        Usuario usuario1 = new Usuario("Usuario1@ua");
        usuario.setId(2L);
        //usuario es admin para poder ver la lista de usuarios.
        usuario.setAdminApproved(true);
        //Ponemos el acceso en falso para que aparezca el boton desbloquear
        usuario1.setAccess(false);

        List<Usuario> usuarios = new ArrayList<Usuario>();
        usuarios.add(usuario);
        usuarios.add(usuario1);

        when(usuarioService.getUsers()).thenReturn(usuarios);
        when(usuarioService.findById(0L)).thenReturn(usuario);

        this.mockMvc.perform(get("/usuarios"))
                .andExpect(content().string(allOf(containsString("Desbloquear"))));

    }

    @Test
    public void getListaUsuariosBarraMenu() throws Exception {

        Usuario usuario = new Usuario("Usuario@ua");
        usuario.setId(1L);
        //Añado nombre para que no de error el navbar por usuario.nombre = null;
        usuario.setNombre("Usuario");

        Usuario usuario1 = new Usuario("Usuario1@ua");
        usuario.setId(2L);
        //usuario es admin para poder ver la lista de usuarios.
        usuario.setAdminApproved(true);

        List<Usuario> usuarios = new ArrayList<Usuario>();
        usuarios.add(usuario);
        usuarios.add(usuario1);

        when(usuarioService.getUsers()).thenReturn(usuarios);
        when(usuarioService.findById(0L)).thenReturn(usuario);

        this.mockMvc.perform(get("/usuarios"))
                .andExpect(content().string(allOf(containsString("Tareas"),
                        containsString("Usuario"), containsString("Cerrar Sesión Usuario"))));

    }


    @Test
    public void editarTareaDevuelveFormEstado() throws Exception {
        Tarea tarea = new Tarea(new Usuario("domingo@ua.es"), "Tarea de prueba");
        tarea.setId(1L);
        tarea.getUsuario().setId(1L);
        tarea.setDescripcion("Descripción de prueba");
        tarea.setEstado("To Do");

        when(tareaService.findById(1L)).thenReturn(tarea);

        this.mockMvc.perform(get("/tareas/1/editar"))
                .andExpect(content().string(allOf(
                        // Contiene la acción para enviar el post a la URL correcta
                        containsString("action=\"/tareas/1/editar\""),
                        // Contiene el texto de la tarea a editar
                        containsString("Tarea de prueba"),
                        // Contiene enlace a listar tareas del usuario si se cancela la edición
                        containsString("href=\"/usuarios/1/tareas\""),
                        containsString("To Do"))));
    }

    @Test
    public void getNuevaTareaDevuelveFormEstado() throws Exception {
        Usuario usuario = new Usuario("domingo@ua.es");
        usuario.setId(1L);

        when(usuarioService.findById(1L)).thenReturn(usuario);

        this.mockMvc.perform(get("/usuarios/1/tareas/nueva"))
                .andExpect(content().string(allOf(containsString("action=\"/usuarios/1/tareas/nueva\""),
                        containsString("To Do"))));
    }

    @Test
    public void getListaTareasDevuelveEstados() throws Exception {
        Usuario usuario = new Usuario("domingo@ua.es");
        usuario.setId(1L);

        when(usuarioService.findById(1L)).thenReturn(usuario);

        this.mockMvc.perform(get("/usuarios/1/tareas"))
                .andExpect(content().string(allOf(containsString("To Do"),
                        containsString("In Progress"),
                        containsString("Done"))));
    }

   @Test
    public void PerfilUsuario() throws Exception {
        Usuario usuario = new Usuario("pedro@ua.es");
        usuario.setNombre("pedro");
        //usuario.setId(5L);
        when(usuarioService.findById(any())).thenReturn(usuario);
        this.mockMvc.perform(get("/perfil"))
                .andExpect(content().string(allOf(containsString("pedro@ua.es"),
                        containsString("Perfil"))));
    }

/*    @Test
    public void postModificarPerfil() throws Exception {
        Usuario usuario = new Usuario("domingo@ua.es");
        usuario.setId(1L);
        usuario.setNombre("Usuario");


        when(usuarioService.findById(0L)).thenReturn(usuario);


        this.mockMvc.perform(post("/perfil")
                        .param("nombre", "Pedro")
                        .param("password", "123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/perfil"));

        verify(usuarioService).editar_perfil(usuario);
    }*/

}
