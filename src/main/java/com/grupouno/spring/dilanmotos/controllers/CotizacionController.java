package com.grupouno.spring.dilanmotos.controllers;

import com.grupouno.spring.dilanmotos.models.Cotizacion;
import com.grupouno.spring.dilanmotos.repositories.CotizacionRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/cotizacion")
public class CotizacionController {

    @Autowired
    private CotizacionRepository cotizacionRepository;

    // Mostrar listado y formulario
    @GetMapping
    public String mostrarCotizaciones(@RequestParam(value = "search", required = false) String search, Model model) {
        List<Cotizacion> cotizaciones = (search != null && !search.isEmpty())
                ? cotizacionRepository.findByProductoContainingIgnoreCase(search)
                : cotizacionRepository.findAll();

        model.addAttribute("cotizaciones", cotizaciones);
        model.addAttribute("nuevaCotizacion", new Cotizacion());
        return "cotizacion";
    }

    // Guardar nueva cotización
    @PostMapping
    public String guardarCotizacion(
            @Valid @NonNull @ModelAttribute("nuevaCotizacion") Cotizacion cotizacion,
            BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("cotizaciones", cotizacionRepository.findAll());
            return "cotizacion";
        }

        cotizacionRepository.save(cotizacion);
        return "redirect:/admin/cotizacion?creado";
    }

    // Mostrar formulario de edición
    @GetMapping("/editar/{id}")
    public String editarCotizacion(@PathVariable("id") int id, Model model) {
        return cotizacionRepository.findById(id)
                .map(cotizacion -> {
                    model.addAttribute("cotizacionEditada", cotizacion);
                    return "editar_cotizacion";
                })
                .orElse("redirect:/admin/cotizacion?error=not_found");
    }

    // Actualizar cotización
    @PostMapping("/actualizar/{id}")
    public String actualizarCotizacion(
            @PathVariable("id") int id,
            @Valid @NonNull @ModelAttribute("cotizacionEditada") Cotizacion cotizacion,
            BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            return "editar_cotizacion";
        }

        return cotizacionRepository.findById(id)
                .map(existingCotizacion -> {
                    existingCotizacion.setProducto(cotizacion.getProducto());
                    existingCotizacion.setFecha(cotizacion.getFecha());
                    existingCotizacion.setCantidad(cotizacion.getCantidad());
                    existingCotizacion.setPrecioUnitario(cotizacion.getPrecioUnitario());
                    existingCotizacion.setProductoAgregado(cotizacion.isProductoAgregado());
                    cotizacionRepository.save(existingCotizacion);
                    return "redirect:/admin/cotizacion?actualizado";
                })
                .orElse("redirect:/admin/cotizacion?error=not_found");
    }

    // Eliminar cotización
    @PostMapping("/eliminar/{id}")
    public String eliminarCotizacion(@PathVariable("id") int id) {
        if (cotizacionRepository.existsById(id)) {
            cotizacionRepository.deleteById(id);
            return "redirect:/admin/cotizacion?eliminado";
        }
        return "redirect:/admin/cotizacion?error=not_found";
    }
}
