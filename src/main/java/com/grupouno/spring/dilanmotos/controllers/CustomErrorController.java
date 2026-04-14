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

    // 1. Método específico para el rechazo de Spring Security (El escudo)
    @RequestMapping("/error/403")
    public String accesoDenegado(Model model) {
        model.addAttribute("errorTitle", "Acceso Denegado (403)");
        model.addAttribute("errorMessage", "No tienes los permisos de Administrador necesarios para acceder a este módulo.");
        return "error/403"; 
    }

    // 2. Método general que atrapa todo lo demás (La red)
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());

            // 404 - No Encontrado (La ruta no existe)
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                model.addAttribute("errorTitle", "Página No Encontrada (404)");
                model.addAttribute("errorMessage", "Lo sentimos, la ruta que buscas no existe o fue movida.");
                return "error/404";
            }
            // 500 - Error Interno (Fallo en la BD o en tu lógica de Java)
            else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                model.addAttribute("errorTitle", "Error Interno del Servidor (500)");
                model.addAttribute("errorMessage", "Ocurrió un problema procesando tu solicitud. Nuestro equipo técnico ya fue notificado.");
                return "error/500";
            }
            // 400 - Bad Request (El usuario envió datos malos en un formulario)
            else if (statusCode == HttpStatus.BAD_REQUEST.value()) {
                model.addAttribute("errorTitle", "Solicitud Incorrecta (400)");
                model.addAttribute("errorMessage", "Los datos enviados no son válidos o están incompletos.");
                return "error/400";
            }
        }

        // Si ocurre un error que no está en la lista de arriba (Ej: 408, 502)
        model.addAttribute("errorTitle", "Error Inesperado");
        model.addAttribute("errorMessage", "Ha ocurrido un error desconocido en el sistema.");
        return "error/generico";
    }
}