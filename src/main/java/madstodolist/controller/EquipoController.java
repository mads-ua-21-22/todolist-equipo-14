package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.authentication.UsuarioNoLogeadoException;
import madstodolist.controller.exception.UsuarioNotFoundException;
import madstodolist.model.Equipo;
import madstodolist.model.Tarea;
import madstodolist.model.Usuario;
import madstodolist.model.Usuario;
import madstodolist.service.TareaService;
import madstodolist.service.UsuarioService;
import madstodolist.service.EquipoService;
import madstodolist.service.UsuarioServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class EquipoController {

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    TareaService tareaService;

    @Autowired
    ManagerUserSession managerUserSession;

    @Autowired
    EquipoService equipoService;

    @GetMapping("/equipos")
    public String equipos(Model model, HttpSession session) {
        Long idUsuario = managerUserSession.usuarioLogeado(session);
        Usuario usuario = null;

        if(idUsuario != null) {
            managerUserSession.comprobarUsuarioLogeado(session, idUsuario);
            usuario = usuarioService.findById(idUsuario);

            List <Equipo> equipos = equipoService.findAllOrderedByName();
            model.addAttribute("usuario", usuario);
            model.addAttribute("equipos", equipos);

                return "listaEquipos";
        }
        else {
            throw new UsuarioNoLogeadoException();
        }

    }

    @GetMapping("/equipos/{id}")
    public String descripcionUsuario(@PathVariable(value="id") Long idEquipo,
                                     Model model, HttpSession session) {

        Long idUsuario = managerUserSession.usuarioLogeado(session);
        Usuario usuario = null;

        if(idUsuario != null) {
            managerUserSession.comprobarUsuarioLogeado(session, idUsuario);
            usuario = usuarioService.findById(idUsuario);

            Equipo equipo = equipoService.findById(idEquipo);

            model.addAttribute("usuario", usuario);
            model.addAttribute("equipo", equipo);

            return "descripcionEquipo";

        } else {
            throw new UsuarioNoLogeadoException();
        }

    }

    @GetMapping("/crearEquipo")
    public String crearEquipo(Model model, HttpSession session) {
        Long idUsuario = managerUserSession.usuarioLogeado(session);
        Usuario usuario = null;

        if(idUsuario != null) {
            managerUserSession.comprobarUsuarioLogeado(session, idUsuario);
            usuario = usuarioService.findById(idUsuario);

            model.addAttribute("usuario", usuario);

            return "formNuevoEquipo";
        }
        else {
            throw new UsuarioNoLogeadoException();
        }

    }

    @PostMapping("/equipos")
    public String nuevoEquipo(Model model, @ModelAttribute EquipoData equipoData,
                              RedirectAttributes flash, HttpSession session) {

        Long idUsuario = managerUserSession.usuarioLogeado(session);
        Usuario usuario = null;

        if(idUsuario != null) {
            managerUserSession.comprobarUsuarioLogeado(session, idUsuario);
            usuario = usuarioService.findById(idUsuario);
            equipoService.crearEquipo(equipoData.getNombre());
            flash.addFlashAttribute("mensaje", "Tarea creada correctamente");;
            model.addAttribute("usuario", usuario);
            return "redirect:/equipos";
        }
        else {
            throw new UsuarioNoLogeadoException();
        }
    }

    @PostMapping("/equipos/{id}")
    public String addUserEquipo(@PathVariable(value="id") Long idEquipo,
                                   Model model, @ModelAttribute EquipoData equipoData, HttpSession session) {

        Long idUsuario = managerUserSession.usuarioLogeado(session);
        Usuario usuario = null;

        if(idUsuario != null) {
            managerUserSession.comprobarUsuarioLogeado(session, idUsuario);
            usuario = usuarioService.findById(idUsuario);
            equipoService.addUsuarioEquipo(idEquipo, usuario.getId());
            model.addAttribute("usuario", usuario);
            return "redirect:/equipos/{id}";

        }
        else {
            throw new UsuarioNoLogeadoException();
        }
    }

    @PostMapping("/equiposdel/{id}")
    public String deleteUserEquipo(@PathVariable(value="id") Long idEquipo,
                                Model model, @ModelAttribute EquipoData equipoData, HttpSession session) {

        Long idUsuario = managerUserSession.usuarioLogeado(session);
        Usuario usuario = null;

        if(idUsuario != null) {
            managerUserSession.comprobarUsuarioLogeado(session, idUsuario);
            usuario = usuarioService.findById(idUsuario);
            equipoService.borrarUsuarioEquipo(idEquipo, usuario.getId());
            model.addAttribute("usuario", usuario);
            return "redirect:/equipos/{id}";

        }
        else {
            throw new UsuarioNoLogeadoException();
        }
    }

}
