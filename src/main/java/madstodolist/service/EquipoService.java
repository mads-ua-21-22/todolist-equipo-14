package madstodolist.service;

import madstodolist.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class EquipoService {

    private EquipoRepository equipoRepository;

    @Autowired
    public EquipoService(EquipoRepository equipoRepository) {
        this.equipoRepository = equipoRepository;
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
}
