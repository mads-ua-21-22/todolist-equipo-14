package madstodolist;

import madstodolist.model.Equipo;
import madstodolist.model.EquipoRepository;
import madstodolist.model.Tarea;
import madstodolist.model.TareaRepository;
import madstodolist.model.Usuario;
import madstodolist.model.UsuarioRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SpringBootTest
public class EquipoTest {

    @Test
    public void crearEquipo() {
        Equipo equipo = new Equipo("Proyecto P1");
        assertThat(equipo.getNombre()).isEqualTo("Proyecto P1");
    }

    @Autowired
    private EquipoRepository equipoRepository;

    @Test
    @Transactional
    public void grabarEquipo() {
        // GIVEN
        Equipo equipo = new Equipo("Proyecto P1");

        // WHEN
        equipoRepository.save(equipo);

        // THEN
        assertThat(equipo.getId()).isNotNull();
    }


}

