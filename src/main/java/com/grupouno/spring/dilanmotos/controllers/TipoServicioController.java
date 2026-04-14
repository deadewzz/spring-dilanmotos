package com.grupouno.spring.dilanmotos.controllers;

import com.grupouno.spring.dilanmotos.models.TipoServicio;
import com.grupouno.spring.dilanmotos.repositories.TipoServicioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/tipoServicio")
public class TipoServicioController {

    @Autowired
    private TipoServicioRepository tipoServicioRepository;

    // Mostrar listado y formulario
    @GetMapping
    public String mostrarTipoServicios(@RequestParam(value = "search", required = false) String search, Model model) {
        List<TipoServicio> tipoServicios = (search != null && !search.isEmpty())
                ? tipoServicioRepository.findByNombreContainingIgnoreCase(search)
                : tipoServicioRepository.findAll();

        model.addAttribute("listaTipoServicio", tipoServicios);
        model.addAttribute("nuevoTipoServicio", new TipoServicio());
        return "tipoServicio";
    }

    // Guardar nuevo tipo de servicio
    @PostMapping
    public String guardarTipoServicio(
            @Valid @NonNull @ModelAttribute("nuevoTipoServicio") TipoServicio tipoServicio,
            BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("listaTipoServicio", tipoServicioRepository.findAll());
            return "tipoServicio";
        }

        tipoServicioRepository.save(tipoServicio);
        return "redirect:/admin/tipoServicio?creado";
    }

    // Mostrar formulario de edición
    @GetMapping("/editar/{id}")
    public String editarTipoServicio(@PathVariable("id") int id, Model model) {
        return tipoServicioRepository.findById(id)
                .map(tipoServicio -> {
                    model.addAttribute("tipoServicioEditado", tipoServicio);
                    return "editar_tipoServicio";
                })
                .orElse("redirect:/admin/tipoServicio?error=not_found");
    }

    // Actualizar tipo de servicio
    @PostMapping("/actualizar")
    public String actualizarTipoServicio(
            @Valid @NonNull @ModelAttribute("tipoServicioEditado") TipoServicio tipoServicio,
            BindingResult result) {
        if (result.hasErrors()) {
            return "editar_tipoServicio";
        }

        tipoServicioRepository.save(tipoServicio);
        return "redirect:/admin/tipoServicio?actualizado";
    }

    // Eliminar tipo de servicio
    @GetMapping("/eliminar/{id}")
    public String eliminarTipoServicio(@PathVariable("id") int id) {
        if (tipoServicioRepository.existsById(id)) {
            tipoServicioRepository.deleteById(id);
            return "redirect:/admin/tipoServicio?eliminado";
        }
        return "redirect:/admin/tipoServicio?error=not_found";
    }
}