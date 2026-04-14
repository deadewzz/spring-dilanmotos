package com.grupouno.spring.dilanmotos.controllers;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error/403")
    public String accesoDenegado(Model model) {
        model.addAttribute("errorTitle", "Acceso Denegado (403)");
        model.addAttribute("errorMessage", "No tienes los permisos necesarios para acceder a este módulo de Dilan Motos.");
        return "error/403"; // Esto busca tu archivo en src/main/resources/templates/error/403.html
    }
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                model.addAttribute("errorTitle", "Página No Encontrada (404)");
                model.addAttribute("errorMessage", "Lo sentimos, la ruta que buscas no existe en el sistema.");
                return "error/404";
            }
            else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                model.addAttribute("errorTitle", "Error Interno (500)");
                model.addAttribute("errorMessage", "Ocurrió un problema en nuestro servidor. Por favor, intenta más tarde.");
                return "error/500";
            }
        }
        model.addAttribute("errorTitle", "Error Inesperado");
        model.addAttribute("errorMessage", "Ha ocurrido un error desconocido.");
        return "error/generico";
    }
}