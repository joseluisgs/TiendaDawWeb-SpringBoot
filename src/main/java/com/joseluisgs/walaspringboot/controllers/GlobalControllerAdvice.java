package com.joseluisgs.walaspringboot.controllers;

import com.joseluisgs.walaspringboot.models.Product;
import com.joseluisgs.walaspringboot.models.User;
import com.joseluisgs.walaspringboot.services.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

// Importamos seguridad
// Aqui exponemos atributos globales para las vistas
@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private ProductService productService;

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

    @ModelAttribute("csrfToken")
    public String getCsrfToken(HttpServletRequest request) {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        return csrfToken != null ? csrfToken.getToken() : "";
    }

    @ModelAttribute("csrfParamName")
    public String getCsrfParamName(HttpServletRequest request) {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        return csrfToken != null ? csrfToken.getParameterName() : "_csrf";
    }

    // ⭐ SHOPPING CART INFORMATION - FOR ALL PAGES ⭐
    @ModelAttribute("cartItemCount")
    public int getCartItemCount(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            List<Long> carrito = (List<Long>) session.getAttribute("carrito");
            return (carrito != null) ? carrito.size() : 0;
        }
        return 0;
    }

    @ModelAttribute("cartTotal")
    public Double getCartTotal(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            List<Long> carrito = (List<Long>) session.getAttribute("carrito");
            if (carrito != null && !carrito.isEmpty()) {
                List<Product> productos = productService.variosPorId(carrito);
                return productos.stream()
                        .mapToDouble(Product::getPrecio)
                        .sum();
            }
        }
        return 0.0;
    }

    @ModelAttribute("hasCartItems")
    public boolean hasCartItems(HttpServletRequest request) {
        return getCartItemCount(request) > 0;
    }

    @ModelAttribute("items_carrito")
    public String itemsCarrito(HttpServletRequest request) {
        int count = getCartItemCount(request);
        return count > 0 ? Integer.toString(count) : "";
    }
}