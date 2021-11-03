package madstodolist.model;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EquipoRepository extends CrudRepository<Equipo, Long> {

    @Query(value="select * from equipos", nativeQuery=true)
    List<Equipo> findAll();
}
