package dev.joseluisgs.walaspringboot.controllers;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        String errorTitle = "Error";
        String errorMessage = "Ha ocurrido un error";
        String errorCode = "500";
        String errorIcon = "bi-exclamation-triangle";

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            errorCode = String.valueOf(statusCode);

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                errorTitle = "Página no encontrada";
                errorMessage = "Lo sentimos, la página que buscas no existe o ha sido movida.";
                errorIcon = "bi-search";
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                errorTitle = "Acceso denegado";
                errorMessage = "No tienes permisos para acceder a esta página.";
                errorIcon = "bi-shield-exclamation";
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                errorTitle = "Error interno del servidor";
                errorMessage = "Algo salió mal en nuestro servidor. Estamos trabajando para solucionarlo.";
                errorIcon = "bi-exclamation-triangle";
            }
        }

        model.addAttribute("errorCode", errorCode);
        model.addAttribute("errorTitle", errorTitle);
        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("errorIcon", errorIcon);

        return "error";
    }
}
