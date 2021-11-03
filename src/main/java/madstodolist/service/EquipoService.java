package madstodolist.service;

import madstodolist.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class EquipoService {

    private EquipoRepository equipoRepository;

    @Autowired
    public EquipoService(EquipoRepository equipoRepository) {
        this.equipoRepository = equipoRepository;
    }

    @Transactional
    public List<Equipo> findAllOrderedByName() {
        List<Equipo> equipos = new ArrayList(equipoRepository.findAll());
        Collections.sort(equipos, (a, b) -> a.getNombre().compareToIgnoreCase(b.getNombre()));

        return equipos;
    }

    @Transactional
    public Equipo findById(Long id) {
        Equipo equipo = null;
        List<Equipo> equipos = new ArrayList(equipoRepository.findAll());
        for ( int i = 0; i < equipos.size(); i++)
        {
            if (equipos.get(i).getId() == id)
                equipo = equipos.get(i);
        }
        return equipo;
    }
}
