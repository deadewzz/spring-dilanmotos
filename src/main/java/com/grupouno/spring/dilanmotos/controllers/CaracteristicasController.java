package com.grupouno.spring.dilanmotos.controllers;

import com.grupouno.spring.dilanmotos.models.Caracteristicas;
import com.grupouno.spring.dilanmotos.models.Moto;
import com.grupouno.spring.dilanmotos.repositories.CaracteristicasRepository;
import com.grupouno.spring.dilanmotos.repositories.MotoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/caracteristicas")
public class CaracteristicasController {

    @Autowired
    private CaracteristicasRepository caracteristicasRepository;

    @Autowired
    private MotoRepository motoRepository;

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
        return "redirect:/admin/caracteristicas?creado";
    }

    @GetMapping("/editar/{id}")
    public String editarCaracteristica(@PathVariable("id") int id, Model model) {
        return caracteristicasRepository.findById(id)
                .map(caracteristica -> {
                    model.addAttribute("caracteristicaEditada", caracteristica);
                    model.addAttribute("motos", motoRepository.findAll());
                    return "editar_caracteristicas";
                })
                .orElse("redirect:/admin/caracteristicas?error=not_found");
    }

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
        return "redirect:/admin/caracteristicas?actualizado";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarCaracteristica(@PathVariable("id") int id) {
        if (caracteristicasRepository.existsById(id)) {
            caracteristicasRepository.deleteById(id);
            return "redirect:/admin/caracteristicas?eliminado";
        }
        return "redirect:/admin/caracteristicas?error=not_found";
    }
}
