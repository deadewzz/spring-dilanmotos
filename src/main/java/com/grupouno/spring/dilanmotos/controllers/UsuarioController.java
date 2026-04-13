package com.grupouno.spring.dilanmotos.controllers;

import com.grupouno.spring.dilanmotos.models.Usuarios;
import com.grupouno.spring.dilanmotos.repositories.UsuarioRepository;
import com.grupouno.spring.dilanmotos.services.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Controller
@Tag(name = "Usuarios", description = "Operaciones de gestión de usuarios y perfiles")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Operation(summary = "Listar usuarios", description = "Muestra la vista con todos los usuarios registrados o filtrados por búsqueda.")
    @GetMapping("/usuario")
    public String mostrarUsuarios(@RequestParam(value = "search", required = false) String search, Model model) {
        List<Usuarios> usuarios = (search != null && !search.isEmpty())
                ? usuarioRepository.findByNombreContainingIgnoreCaseOrCorreoContainingIgnoreCase(search, search)
                : usuarioRepository.findAll();

        model.addAttribute("usuarios", usuarios);
        model.addAttribute("nuevoUsuario", new Usuarios());
        return "usuario";
    }

    @Operation(summary = "Registrar usuario", description = "Guarda un nuevo usuario en la base de datos con la contraseña encriptada.")
    @PostMapping("/usuario")
    public String guardarUsuario(
            @Valid @NonNull @ModelAttribute("nuevoUsuario") Usuarios usuario,
            BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("usuarios", usuarioRepository.findAll());
            return "usuario";
        }
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        usuarioRepository.save(usuario);
        return "redirect:/usuario?creado";
    }

    @Operation(summary = "Formulario de edición", description = "Busca un usuario por ID y carga la vista para editar sus datos.")
    @GetMapping("/usuario/editar/{id}")
    public String editarUsuario(@PathVariable("id") int id, Model model) {
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    model.addAttribute("usuarioEditado", usuario);
                    return "editar_usuario";
                })
                .orElse("redirect:/usuario?error=not_found");
    }

    @Operation(summary = "Actualizar usuario", description = "Procesa la actualización de los datos de un usuario existente.")
    @PostMapping("/usuario/actualizar")
    public String actualizarUsuario(
            @Valid @NonNull @ModelAttribute("usuarioEditado") Usuarios usuario,
            BindingResult result) {
        if (result.hasErrors()) {
            return "editar_usuario";
        }
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        usuarioRepository.save(usuario);
        return "redirect:/usuario?actualizado";
    }

    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario de forma permanente por su ID.")
    @GetMapping("/usuario/eliminar/{id}")
    public String eliminarUsuario(@PathVariable("id") int id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            return "redirect:/usuario?eliminado";
        }
        return "redirect:/usuario?error=not_found";
    }

    public final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Operation(summary = "Mi Cuenta", description = "Muestra el perfil del usuario que ha iniciado sesión actualmente.")
    @GetMapping("/CuentaUsuario")
    public String miCuenta(@AuthenticationPrincipal User principal, Model model) {
        String correo = principal.getUsername();
        Usuarios usuarioActual = usuarioRepository.findByCorreoConMotos(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        model.addAttribute("usuario", usuarioActual);
        return "CuentaUsuario";
    }
}