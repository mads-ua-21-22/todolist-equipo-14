package madstodolist.model;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TareaRepository extends CrudRepository<Tarea, Long> {
    @Query("select t from Tarea t where t.usuario = ?2 and upper(t.titulo) like concat('%', upper(?1), '%')")
    List<Tarea> buscar(String s, Usuario u);
}
