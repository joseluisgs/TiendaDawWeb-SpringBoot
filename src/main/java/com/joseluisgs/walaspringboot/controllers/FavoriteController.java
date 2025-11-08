package com.joseluisgs.walaspringboot.controllers;

import com.joseluisgs.walaspringboot.models.Product;
import com.joseluisgs.walaspringboot.models.User;
import com.joseluisgs.walaspringboot.services.FavoriteService;
import com.joseluisgs.walaspringboot.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/app")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private UserService userService;

    private User getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.buscarPorEmail(email);
    }

    @GetMapping("/favoritos")
    public String verFavoritos(Model model) {
        User usuario = getAuthenticatedUser();
        List<Product> productosFavoritos = favoriteService.getFavoriteProducts(usuario);
        model.addAttribute("productos", productosFavoritos);
        return "app/favoritos/lista";
    }

    @PostMapping("/favoritos/add/{productoId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addFavorito(@PathVariable Long productoId) {
        User usuario = getAuthenticatedUser();
        Map<String, Object> response = new HashMap<>();
        
        try {
            favoriteService.addFavorite(usuario, productoId);
            response.put("success", true);
            response.put("message", "Producto añadido a favoritos");
            response.put("isFavorite", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al añadir a favoritos");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/favoritos/remove/{productoId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> removeFavorito(@PathVariable Long productoId) {
        User usuario = getAuthenticatedUser();
        Map<String, Object> response = new HashMap<>();
        
        try {
            favoriteService.removeFavorite(usuario, productoId);
            response.put("success", true);
            response.put("message", "Producto eliminado de favoritos");
            response.put("isFavorite", false);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al eliminar de favoritos");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/favoritos/check/{productoId}")
    @ResponseBody
    public ResponseEntity<Map<String, Boolean>> checkFavorito(@PathVariable Long productoId) {
        User usuario = getAuthenticatedUser();
        Map<String, Boolean> response = new HashMap<>();
        boolean isFavorite = favoriteService.isFavorite(usuario, productoId);
        response.put("isFavorite", isFavorite);
        return ResponseEntity.ok(response);
    }
}
