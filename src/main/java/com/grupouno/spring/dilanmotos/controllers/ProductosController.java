package com.grupouno.spring.dilanmotos.controllers;

import com.grupouno.spring.dilanmotos.models.Productos;
import com.grupouno.spring.dilanmotos.repositories.CategoriaRepository;
import com.grupouno.spring.dilanmotos.repositories.MarcaRepository;
import com.grupouno.spring.dilanmotos.repositories.ProductosRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/productos")
public class ProductosController {

    @Autowired
    private ProductosRepository productosRepository;
    @Autowired
    private CategoriaRepository categoriaRepository;
    @Autowired
    private MarcaRepository marcaRepository;

    @GetMapping
    public String mostrarProducto(@RequestParam(value = "search", required = false) String search, Model model) {
        List<Productos> resultados = (search != null && !search.isEmpty())
                ? productosRepository.findByNombreContainingIgnoreCaseOrDescripcionContainingIgnoreCase(search, search)
                : productosRepository.findAll();

        model.addAttribute("productos", resultados);
        model.addAttribute("nuevoProducto", new Productos());
        model.addAttribute("categorias", categoriaRepository.findAll());
        model.addAttribute("marcas", marcaRepository.findAll());
        return "productos";
    }

    @PostMapping("/guardar")
    public String guardarProducto(@Valid @NonNull @ModelAttribute("nuevoProducto") Productos producto,
            BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("productos", productosRepository.findAll());
            model.addAttribute("categorias", categoriaRepository.findAll());
            model.addAttribute("marcas", marcaRepository.findAll());
            return "productos";
        }
        productosRepository.save(producto);
        return "redirect:/admin/productos?creado";
    }

    @GetMapping("/editar/{id}")
    public String editarProducto(@PathVariable("id") int id, Model model) {
        return productosRepository.findById(id)
                .map(producto -> {
                    model.addAttribute("productoEditada", producto);
                    model.addAttribute("categorias", categoriaRepository.findAll());
                    model.addAttribute("marcas", marcaRepository.findAll());
                    return "editar_productos";
                })
                .orElse("redirect:/admin/productos?error=not_found");
    }

    @PostMapping("/actualizar")
    public String actualizarProducto(@Valid @NonNull @ModelAttribute("productoEditada") Productos producto,
            BindingResult result) {
        if (result.hasErrors()) {
            return "editar_productos";
        }
        productosRepository.save(producto);
        return "redirect:/admin/productos?actualizado";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarProducto(@PathVariable("id") int id) {
        if (productosRepository.existsById(id)) {
            productosRepository.deleteById(id);
            return "redirect:/admin/productos?eliminado";
        }
        return "redirect:/admin/productos?error=not_found";
    }

    @GetMapping("/CatalogoKitArrastreAutenticado")
    public String catalogoKitArrastre(@RequestParam(required = false) String marca, Model model) {
        String categoria = "Kit de arrastre";

        List<Productos> productos = (marca == null || marca.equals("Todas"))
                ? productosRepository.findByCategoriaNombre(categoria)
                : productosRepository.findByCategoriaNombreAndMarcaNombre(categoria, marca);

        model.addAttribute("productos", productos);
        model.addAttribute("marcaSeleccionada", marca != null ? marca : "Todas");

        return "CatalogoKitArrastreAutenticado";
    }

    @GetMapping("/CatalogoAceiteAutenticado")
    public String catalogoAceite(@RequestParam(required = false) String marca, Model model) {
        String categoria = "Aceites";

        List<Productos> productos = (marca == null || marca.equals("Todas"))
                ? productosRepository.findByCategoriaNombre(categoria)
                : productosRepository.findByCategoriaNombreAndMarcaNombre(categoria, marca);

        model.addAttribute("productos", productos);
        model.addAttribute("marcaSeleccionada", marca != null ? marca : "Todas");

        return "CatalogoAceiteAutenticado";
    }

    @GetMapping("/CatalogoLlantaAutenticado")
    public String catalogoLlanta(@RequestParam(required = false) String marca, Model model) {
        String categoria = "Llantas";
        List<Productos> productos;

        if (marca == null || marca.equalsIgnoreCase("Todas")) {
            productos = productosRepository.findByCategoriaNombre(categoria);
        } else {
            productos = productosRepository.findByCategoriaNombreAndNombreContainingIgnoreCase(categoria, marca);
        }

        model.addAttribute("productos", productos);
        model.addAttribute("marcaSeleccionada", marca != null ? marca : "Todas");

        return "CatalogoLlantaAutenticado";
    }
}
