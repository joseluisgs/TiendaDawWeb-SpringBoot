package com.joseluisgs.walaspringboot.controllers;

import com.joseluisgs.walaspringboot.models.User;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

// Importamos seguridad
// Aqui exponemos atributos globales para las vistas
@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute("currentUser")
    public User getCurrentUser(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication.getPrincipal() instanceof String)) {
            return (User) authentication.getPrincipal();
        }
        return null;
    }

    @ModelAttribute("isAuthenticated")
    public boolean isAuthenticated(Authentication authentication) {
        return authentication != null && authentication.isAuthenticated()
                && !(authentication.getPrincipal() instanceof String);
    }

    // ⭐ AÑADIR MÉTODO HELPER PARA ADMIN ⭐
    @ModelAttribute("isAdmin")
    public boolean isAdmin(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication.getPrincipal() instanceof String)) {
            User user = (User) authentication.getPrincipal();
            return "ADMIN".equals(user.getRol());
        }
        return false;
    }

    @ModelAttribute("username")
    public String getUsername(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication.getPrincipal() instanceof String)) {
            User user = (User) authentication.getPrincipal();
            return user.getNombre() + " " + user.getApellidos();
        }
        return null;
    }

    @ModelAttribute("userRole")
    public String getUserRole(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication.getPrincipal() instanceof String)) {
            User user = (User) authentication.getPrincipal();
            return user.getRol();
        }
        return null;
    }
}