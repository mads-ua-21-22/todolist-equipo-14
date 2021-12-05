package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.authentication.UsuarioNoLogeadoException;
import madstodolist.controller.exception.TareaNotFoundException;
import madstodolist.controller.exception.UsuarioNotFoundException;
import madstodolist.model.Tarea;
import madstodolist.model.Usuario;
import madstodolist.service.TareaService;
import madstodolist.service.UsuarioService;
import madstodolist.service.UsuarioServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

@Controller
public class UserController {

    @Autowired
    UsuarioService usuarioService;
    @Autowired
    ManagerUserSession managerUserSession;

    @GetMapping("/usuarios")
    public String usuarios(Model model, HttpSession session) {
        Long idUsuario = managerUserSession.usuarioLogeado(session);
        Usuario usuario = null;
        
        if(idUsuario != null) {
            managerUserSession.comprobarUsuarioLogeado(session, idUsuario);
            usuario = usuarioService.findById(idUsuario);

            if (usuario.getAdminApproved()){
                List <Usuario> usuarios = usuarioService.getUsers();
                model.addAttribute("usuario", usuario);
                model.addAttribute("usuarios", usuarios);

                return "listaUsuarios";
            } else {
                throw new UsuarioServiceException("Sólo el administrador tiene permiso para ver los usuarios");
            }
        }
        else {
            throw new UsuarioNoLogeadoException();
        }

    }

    @GetMapping("/usuarios/{id}")
    public String descripcionUsuario(@PathVariable(value="id") Long idUsuario,
                                     Model model, HttpSession session) {

        Long idUsuariosession = managerUserSession.usuarioLogeado(session);
        managerUserSession.comprobarUsuarioLogeado(session, idUsuariosession);

        Usuario usuario = usuarioService.findById(idUsuariosession);
        Usuario usuariodesc = usuarioService.findById(idUsuario);
        if (usuario == null) {
            throw new UsuarioNotFoundException();
        }

        if (usuario.getAdminApproved()){
            model.addAttribute("usuario", usuario);
            model.addAttribute("usuariodesc", usuariodesc);

            return "descripcionUsuario";
        } else {
            throw new UsuarioServiceException("Sólo el administrador tiene permiso para ver los usuarios");
        }
    }


    @PostMapping("/usuarios/{id}")
    public String accesoUsuario(@PathVariable(value="id") Long idUsuario,
                                Model model, HttpSession session) {

        Long idUsuariosession = managerUserSession.usuarioLogeado(session);
        managerUserSession.comprobarUsuarioLogeado(session, idUsuariosession);
        Usuario usuario = usuarioService.findById(idUsuariosession);

        Usuario usuariodesc = usuarioService.findById(idUsuario);
        if (usuariodesc == null) {
            throw new UsuarioNotFoundException();
        }

        usuarioService.changeAccess(idUsuario);
        model.addAttribute("usuario", usuario);

        return "redirect:/usuarios";

    }

    @GetMapping("/perfil")
    public String perfil_usuario(Model model, HttpSession session) {

        Long idUsuariosession = managerUserSession.usuarioLogeado(session);
        managerUserSession.comprobarUsuarioLogeado(session, idUsuariosession);

        Usuario usuario = usuarioService.findById(idUsuariosession);
        if (usuario == null) {
            throw new UsuarioNotFoundException();
        }
        model.addAttribute("usuario", usuario);
        return "perfilUsuario";
    }
    @PostMapping("/perfil")
    public String editar_perfil(@Valid RegistroData registroData, BindingResult result, Model model, HttpSession session) {

        if (result.hasErrors()) {
            return "formEditarPerfil";
        }

        if (usuarioService.findByEmail(registroData.geteMail()) != null) {
            model.addAttribute("registroData", registroData);
            model.addAttribute("error", "El usuario " + registroData.geteMail() + " ya existe");
            return "formEditarPerfil";
        }
        Long idUsuariosession = managerUserSession.usuarioLogeado(session);
        Usuario usuario = usuarioService.findById(idUsuariosession);

        usuario.setPassword(registroData.getPassword());
        usuario.setFechaNacimiento(registroData.getFechaNacimiento());
        usuario.setNombre(registroData.getNombre());
        usuario.setAdminApproved(registroData.getAdminApproved());

        usuarioService.editar_perfil(usuario);
        return "redirect:/perfil";
    }
    @GetMapping("/perfil/editar")
    public String editar_perfilForm(Model model) {

        model.addAttribute("registroData", new RegistroData());
        return "formEditarPerfil";
    }

}
