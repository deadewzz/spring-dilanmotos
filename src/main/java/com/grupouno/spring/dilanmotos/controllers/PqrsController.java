package com.grupouno.spring.dilanmotos.controllers;

import com.grupouno.spring.dilanmotos.models.PQRS;
import com.grupouno.spring.dilanmotos.repositories.PqrsRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin/pqrs")
@Tag(name = "PQRS", description = "Operaciones de Peticiones, Quejas, Reclamos y Sugerencias")
public class PqrsController {

    @Autowired
    private PqrsRepository pqrsRepository;

    @Operation(summary = "Listar PQRS", description = "Obtiene todas las PQRS con filtro de búsqueda")
    @GetMapping
    public String mostrarPqrs(@RequestParam(value = "search", required = false) String search, Model model) {
        List<PQRS> pqrs = (search != null && !search.isEmpty())
                ? pqrsRepository.findByTipoContainingIgnoreCaseOrAsuntoContainingIgnoreCase(search, search)
                : pqrsRepository.findAll();

        model.addAttribute("pqrs", pqrs);
        model.addAttribute("nuevoPqrs", new PQRS());
        return "pqrs_menu";
    }

    @Operation(summary = "Crear PQRS", description = "Guarda una nueva PQRS inicializando estado y fechas")
    @PostMapping
    public String guardarPqrs(@Valid @NonNull @ModelAttribute("nuevoPqrs") PQRS pqrs, BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("pqrs", pqrsRepository.findAll());
            return "pqrs_menu";
        }

        pqrs.setIdUsuario(1);
        pqrs.setFecha(LocalDateTime.now());
        pqrs.setEstado("PENDIENTE");
        pqrs.setRespuesta_admin("Sin respuesta.");
        pqrs.setCalificacion_servicio("-");
        pqrs.setComentario_servicio("-");
        pqrs.setFecha_respuesta(null);

        pqrsRepository.save(pqrs);
        return "redirect:/admin/pqrs?creado";
    }

    @Operation(summary = "Formulario editar PQRS", description = "Carga los datos de una PQRS específica")
    @GetMapping("/editar/{id}")
    public String editarPqrs(@PathVariable("id") int id, Model model) {
        return pqrsRepository.findById(id)
                .map(pqrs -> {
                    model.addAttribute("pqrsEditada", pqrs);
                    return "editar_pqrs";
                })
                .orElse("redirect:/admin/pqrs?error=not_found");
    }

    @Operation(summary = "Actualizar PQRS", description = "Guarda los cambios realizados a una PQRS")
    @PostMapping("/actualizar")
    public String actualizarPqrs(@Valid @NonNull @ModelAttribute("pqrsEditada") PQRS pqrs, BindingResult result) {
        if (result.hasErrors()) {
            return "editar_pqrs";
        }
        pqrsRepository.save(pqrs);
        return "redirect:/admin/pqrs?actualizado";
    }

    @Operation(summary = "Eliminar PQRS", description = "Elimina un registro de PQRS por su ID")
    @GetMapping("/eliminar/{id}")
    public String eliminarPqrs(@PathVariable("id") int id) {
        if (pqrsRepository.existsById(id)) {
            pqrsRepository.deleteById(id);
            return "redirect:/admin/pqrs?eliminado";
        }
        return "redirect:/admin/pqrs?error=not_found";
    }
}