package madstodolist;

import madstodolist.model.Usuario;
import madstodolist.model.UsuarioRepository;
import madstodolist.service.UsuarioService;
import madstodolist.service.UsuarioServiceException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class UsuarioServiceTest {

    Logger logger = LoggerFactory.getLogger(UsuarioServiceTest.class);

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    public void servicioLoginUsuario() {
        // GIVEN
        // Datos cargados de datos-test.sql

        // WHEN

        UsuarioService.LoginStatus loginStatusOK = usuarioService.login("user@ua", "123");
        UsuarioService.LoginStatus loginStatusErrorPassword = usuarioService.login("user@ua", "000");
        UsuarioService.LoginStatus loginStatusNoUsuario = usuarioService.login("pepito.perez@gmail.com", "12345678");

        // THEN

        assertThat(loginStatusOK).isEqualTo(UsuarioService.LoginStatus.LOGIN_OK);
        assertThat(loginStatusErrorPassword).isEqualTo(UsuarioService.LoginStatus.ERROR_PASSWORD);
        assertThat(loginStatusNoUsuario).isEqualTo(UsuarioService.LoginStatus.USER_NOT_FOUND);
    }

    @Test
    @Transactional
    public void servicioRegistroUsuario() {
        // GIVEN

        Usuario usuario = new Usuario("usuario.prueba2@gmail.com");
        usuario.setPassword("12345678");

        // WHEN

        usuarioService.registrar(usuario);

        // THEN

        Usuario usuarioBaseDatos = usuarioService.findByEmail("usuario.prueba2@gmail.com");
        assertThat(usuarioBaseDatos).isNotNull();
        assertThat(usuarioBaseDatos.getPassword()).isEqualTo(usuario.getPassword());
    }

    public void servicioRegistroUsuarioExcepcionConNullPassword() {
        // Pasamos como argumento un usario sin contraseña
        Usuario usuario =  new Usuario("usuario.prueba@gmail.com");

        // Se produce una excempción de tipo UsuarioServiceException
        Assertions.assertThrows(UsuarioServiceException.class, () -> {
            usuarioService.registrar(usuario);
        });
    }


    @Test
    public void servicioRegistroUsuarioExcepcionConEmailRepetido() {
        // GIVEN
        // Datos cargados de datos-test.sql

        // WHEN - THEN
        // Pasamos como argumento un usario con emaii existente en datos-test.sql
        Usuario usuario =  new Usuario("user@ua");
        usuario.setPassword("12345678");


        // Se produce una excempción de tipo UsuarioServiceException
        Assertions.assertThrows(UsuarioServiceException.class, () -> {
            usuarioService.registrar(usuario);
        });
    }

    @Test
    @Transactional
    public void servicioRegistroUsuarioDevuelveUsuarioConId() {
        // GIVEN

        Usuario usuario = new Usuario("usuario.prueba@gmail.com");
        usuario.setPassword("12345678");

        // WHEN

        usuario = usuarioService.registrar(usuario);

        // THEN

        assertThat(usuario.getId()).isNotNull();
    }

    @Test
    public void servicioConsultaUsuarioDevuelveUsuario() {
        // GIVEN
        // Datos cargados de datos-test.sql

        // WHEN

        Usuario usuario = usuarioService.findByEmail("user@ua");

        // THEN

        assertThat(usuario.getId()).isEqualTo(1L);
    }

    @Test
    public void comprobarUsuariosLista() throws Exception {

        // GIVEN
        Usuario usuario0 = new Usuario("user@ua");
        Usuario usuario = new Usuario("juan.gutierrez@gmail.com");
        Usuario usuario1 = new Usuario("carlos@gmail.com");

        // WHEN
        usuario0.setId(1L);
        usuario0.setNombre("Usuario Ejemplo");
        usuario0.setPassword("123");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        usuario0.setFechaNacimiento(sdf.parse("2001-02-10"));

        usuario.setId(2L);
        usuario.setNombre("Juan Gutiérrez");
        usuario.setPassword("123");

        usuario.setFechaNacimiento(sdf.parse("1997-02-20"));

        usuario1.setId(3L);
        usuario1.setNombre("Carlos");
        usuario1.setPassword("123");

        usuario1.setFechaNacimiento(sdf.parse("1994-09-20"));
        List<Usuario> usuarios = new ArrayList<Usuario>();
        usuarios.add(usuario0);
        usuarios.add(usuario);
        usuarios.add(usuario1);

        List<Usuario> listaUsuarios = usuarioRepository.getUsers();

        assertThat(listaUsuarios.size()).isEqualTo(usuarios.size());
        assertThat(listaUsuarios.get(0).getId()).isEqualTo(usuarios.get(0).getId());
        assertThat(listaUsuarios.get(0).getNombre()).isEqualTo(usuarios.get(0).getNombre());
        assertThat(listaUsuarios.get(1).getId()).isEqualTo(usuarios.get(1).getId());
        assertThat(listaUsuarios.get(1).getNombre()).isEqualTo(usuarios.get(1).getNombre());
        assertThat(listaUsuarios.get(2).getId()).isEqualTo(usuarios.get(2).getId());
        assertThat(listaUsuarios.get(2).getNombre()).isEqualTo(usuarios.get(2).getNombre());

    }
}