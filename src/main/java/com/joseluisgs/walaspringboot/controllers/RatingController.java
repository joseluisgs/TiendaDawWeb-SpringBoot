package com.joseluisgs.walaspringboot.controllers;

import com.joseluisgs.walaspringboot.models.Product;
import com.joseluisgs.walaspringboot.models.Rating;
import com.joseluisgs.walaspringboot.models.User;
import com.joseluisgs.walaspringboot.services.ProductService;
import com.joseluisgs.walaspringboot.services.RatingService;
import com.joseluisgs.walaspringboot.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/app")
public class RatingController {

    @Autowired
    private RatingService ratingService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    private User getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.buscarPorEmail(email);
    }

    @PostMapping("/ratings/add")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addRating(@RequestParam Long productoId, 
                                                          @RequestParam int puntuacion,
                                                          @RequestParam(required = false) String comentario) {
        User usuario = getAuthenticatedUser();
        Map<String, Object> response = new HashMap<>();
        
        try {
            Rating rating = ratingService.addRating(usuario, productoId, puntuacion, comentario);
            if (rating != null) {
                response.put("success", true);
                response.put("message", "Valoración añadida correctamente");
                
                // Calcular nuevo promedio
                Product producto = productService.findById(productoId);
                Double avgRating = ratingService.getAverageRating(producto);
                Long count = ratingService.getCountRatings(producto);
                
                response.put("averageRating", avgRating != null ? avgRating : 0.0);
                response.put("ratingCount", count);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Producto no encontrado");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al añadir valoración: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/ratings/producto/{productoId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getProductRatings(@PathVariable Long productoId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Product producto = productService.findById(productoId);
            if (producto != null) {
                List<Rating> ratings = ratingService.findByProducto(producto);
                Double avgRating = ratingService.getAverageRating(producto);
                Long count = ratingService.getCountRatings(producto);
                
                response.put("success", true);
                response.put("ratings", ratings);
                response.put("averageRating", avgRating != null ? avgRating : 0.0);
                response.put("ratingCount", count);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Producto no encontrado");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al obtener valoraciones: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
