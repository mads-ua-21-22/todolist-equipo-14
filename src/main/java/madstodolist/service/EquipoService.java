package madstodolist.service;

import madstodolist.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class EquipoService {

    private EquipoRepository equipoRepository;
    private UsuarioRepository usuarioRepository;
    private TareaRepository tareaRepository;

    @Autowired
    public EquipoService(UsuarioRepository usuarioRepository, EquipoRepository equipoRepository, TareaRepository tareaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.equipoRepository = equipoRepository;
        this.tareaRepository = tareaRepository;
    }

    @Transactional(readOnly = true)
    public Equipo findById(Long EquipoId) {
        return equipoRepository.findById(EquipoId).orElse(null);
    }

    @Transactional
    public List<Equipo> findAllOrderedByName() {
        List<Equipo> equipos = new ArrayList(equipoRepository.findAllByOrderByNombre());
        return equipos;
    }

    @Transactional
    public List<Usuario> usuariosEquipo(Long idEquipo) {
        Equipo equipo = findById(idEquipo);
        List<Usuario> usuarios = new ArrayList(equipo.getUsuarios());
        return usuarios;
    }

    @Transactional
    public Equipo addUsuarioEquipo(Long idEquipo, Long idUser) {
        Equipo equipo = findById(idEquipo);
        Usuario user = usuarioRepository.findById(idUser).orElse(null);
        Set <Usuario> usuarios = new HashSet<Usuario>(equipo.getUsuarios());
        usuarios.add(user);
        equipo.setUsuarios(usuarios);
        Set <Equipo> equipos = new HashSet<Equipo>(user.getEquipos());
        equipos.add(equipo);
        user.setEquipos(equipos);
        equipoRepository.save(equipo);
        usuarioRepository.save(user);
        return equipo;
    }

    @Transactional
    public Equipo borrarUsuarioEquipo(Long idEquipo, Long idUser) {
        Equipo equipo = findById(idEquipo);
        Usuario user = usuarioRepository.findById(idUser).orElse(null);
        Set <Usuario> usuarios = new HashSet<Usuario>(equipo.getUsuarios());
        usuarios.remove(user);
        equipo.setUsuarios(usuarios);
        Set <Equipo> equipos = new HashSet<Equipo>(user.getEquipos());
        equipos.remove(equipo);
        user.setEquipos(equipos);
        equipoRepository.save(equipo);
        usuarioRepository.save(user);
        return equipo;
    }

    @Transactional
    public Equipo crearEquipo(String nombre, String descripcion) {
        Equipo equipo = new Equipo(nombre);
        equipo.setDescripcion(descripcion);
        equipoRepository.save(equipo);
        return equipo;
    }

    @Transactional
    public Equipo renombrarEquipo(Long id, String nombre, String descripcion) {
        Equipo equipo = equipoRepository.findById(id).orElse(null);
        equipo.setNombre(nombre);
        equipo.setDescripcion(descripcion);
        equipoRepository.save(equipo);
        return equipo;
    }

    @Transactional
    public void borrarEquipo(Long id) {
        Equipo equipo = equipoRepository.findById(id).orElse(null);
        equipoRepository.delete(equipo);
    }
    @Transactional
    public Tarea nuevaTareaEquipo(Long idEquipo, String tituloTarea, Long idUsuario, String descripcion){
        Equipo equipo = equipoRepository.findById(idEquipo).orElse(null);
        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
        if(equipo == null){
            throw new EquipoServiceException("Equipo" + idEquipo + " no existe al crear tarea " + tituloTarea);
        }
        if (usuario == null) {
            throw new TareaServiceException("Usuario " + idUsuario + " no existe al crear tarea " + tituloTarea);
        }
        Tarea tarea = new Tarea(equipo, tituloTarea, usuario, descripcion);

        tareaRepository.save(tarea);
        return tarea;
    }

}
