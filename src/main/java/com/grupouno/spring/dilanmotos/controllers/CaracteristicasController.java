package com.grupouno.spring.dilanmotos.controllers;

import com.grupouno.spring.dilanmotos.models.Caracteristicas;
import com.grupouno.spring.dilanmotos.models.Moto;
import com.grupouno.spring.dilanmotos.repositories.CaracteristicasRepository;
import com.grupouno.spring.dilanmotos.repositories.MotoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/caracteristicas")
@Tag(name = "Características", description = "Gestión de especificaciones técnicas vinculadas a las motos")
public class CaracteristicasController {

    @Autowired
    private CaracteristicasRepository caracteristicasRepository;

    @Autowired
    private MotoRepository motoRepository;

    @Operation(summary = "Listar características", description = "Muestra todas las características técnicas registradas, con opción de búsqueda por descripción.")
    @GetMapping
    public String mostrarCaracteristicas(@RequestParam(value = "search", required = false) String search, Model model) {
        List<Caracteristicas> resultados = (search != null && !search.isEmpty())
                ? caracteristicasRepository.findByDescripcionContainingIgnoreCase(search)
                : caracteristicasRepository.findAll();

        model.addAttribute("caracteristicas", resultados);
        model.addAttribute("nuevaCaracteristica", new Caracteristicas());
        model.addAttribute("motos", motoRepository.findAll());
        return "caracteristicas";
    }

    @Operation(summary = "Guardar característica", description = "Crea una nueva especificación técnica y la vincula a una moto específica mediante su ID.")
    @PostMapping
    public String guardarCaracteristica(
            @Valid @NonNull @ModelAttribute("nuevaCaracteristica") Caracteristicas caracteristica,
            BindingResult result,
            @RequestParam("idMoto") Integer idMoto,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("caracteristicas", caracteristicasRepository.findAll());
            model.addAttribute("motos", motoRepository.findAll());
            return "caracteristicas";
        }

        Moto moto = motoRepository.findById(idMoto)
                .orElseThrow(() -> new IllegalArgumentException("Moto no encontrada"));
        caracteristica.setMoto(moto);

        caracteristicasRepository.save(caracteristica);
        return "redirect:/caracteristicas?creado";
    }

    @Operation(summary = "Editar característica", description = "Recupera los datos de una característica para su edición en el formulario.")
    @GetMapping("/editar/{id}")
    public String editarCaracteristica(@PathVariable("id") int id, Model model) {
        return caracteristicasRepository.findById(id)
                .map(caracteristica -> {
                    model.addAttribute("caracteristicaEditada", caracteristica);
                    model.addAttribute("motos", motoRepository.findAll());
                    return "editar_caracteristicas";
                })
                .orElse("redirect:/caracteristicas?error=not_found");
    }

    @Operation(summary = "Actualizar característica", description = "Actualiza la descripción o la moto vinculada de una característica existente.")
    @PostMapping("/actualizar")
    public String actualizarCaracteristica(
            @Valid @NonNull @ModelAttribute("caracteristicaEditada") Caracteristicas caracteristica,
            BindingResult result,
            @RequestParam("idMoto") Integer idMoto) {
        if (result.hasErrors()) {
            return "editar_caracteristicas";
        }

        Moto moto = motoRepository.findById(idMoto)
                .orElseThrow(() -> new IllegalArgumentException("Moto no encontrada"));
        caracteristica.setMoto(moto);

        caracteristicasRepository.save(caracteristica);
        return "redirect:/caracteristicas?actualizado";
    }

    @Operation(summary = "Eliminar característica", description = "Elimina físicamente el registro de la característica técnica por ID.")
    @GetMapping("/eliminar/{id}")
    public String eliminarCaracteristica(@PathVariable("id") int id) {
        if (caracteristicasRepository.existsById(id)) {
            caracteristicasRepository.deleteById(id);
            return "redirect:/caracteristicas?eliminado";
        }
        return "redirect:/caracteristicas?error=not_found";
    }
}