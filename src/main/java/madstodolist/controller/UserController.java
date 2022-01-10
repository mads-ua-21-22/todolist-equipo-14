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
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
    public String editar_perfil(@Valid RegistroData registroData, BindingResult result, Model model,
                                HttpSession session, @RequestParam("imagen") MultipartFile multipartFile) throws IOException {

        Long idUsuariosession = managerUserSession.usuarioLogeado(session);
        Usuario usuario = usuarioService.findById(idUsuariosession);

        if (result.hasErrors()) {
            return "formEditarPerfil";
        }

        if (usuarioService.findByEmail(registroData.geteMail()) != null && usuarioService.findByEmail(registroData.geteMail()) != usuario) {
            model.addAttribute("registroData", registroData);
            model.addAttribute("error", "El usuario " + registroData.geteMail() + " ya existe");
            return "formEditarPerfil";
        }




        usuario.setPassword(registroData.getPassword());
        usuario.setFechaNacimiento(registroData.getFechaNacimiento());
        usuario.setNombre(registroData.getNombre());
        usuario.setAdminApproved(registroData.getAdminApproved());
        usuario.setEmail(registroData.geteMail());


        model.addAttribute("usuario", usuario);

        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        //usuario.setImage(fileName);

        String uploadDir = "./user-images";
        Path uploadPath = Paths.get(uploadDir);

        if(!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        if(!fileName.equals("")) {
            try (InputStream inputStream = multipartFile.getInputStream()) {
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new IOException("Could not save the uploaded file: " + fileName);
            }
            usuario.setImage(fileName);


        }
        usuarioService.editar_perfil(usuario);
        return "redirect:/perfil";
    }
    @GetMapping("/perfil/editar")
    public String editar_perfilForm(Model model, HttpSession session,
                                    @ModelAttribute RegistroData registroData) {

        Long idUsuariosession = managerUserSession.usuarioLogeado(session);
        managerUserSession.comprobarUsuarioLogeado(session, idUsuariosession);

        Usuario usuario = usuarioService.findById(idUsuariosession);
        if (usuario == null) {
            throw new UsuarioNotFoundException();
        }
        model.addAttribute("usuario", usuario);
        registroData.setNombre(usuario.getNombre());
        registroData.seteMail(usuario.getEmail());
        registroData.setFechaNacimiento(usuario.getFechaNacimiento());
        return "formEditarPerfil";
    }

}
