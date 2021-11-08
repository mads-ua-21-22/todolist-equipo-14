package madstodolist.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;
import java.util.List;

public interface UsuarioRepository extends CrudRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String s);

    @Query(value="select * from usuarios", nativeQuery=true)
    List<Usuario> getUsers();

    @Query(value="select * from usuarios where admin_approved = true ", nativeQuery=true)
    Usuario adminExist();
}
