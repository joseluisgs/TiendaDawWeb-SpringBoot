package com.joseluisgs.walaspringboot.controllers;

import com.joseluisgs.walaspringboot.models.Product;
import com.joseluisgs.walaspringboot.models.ProductCategory;
import com.joseluisgs.walaspringboot.services.ProductService;
import com.joseluisgs.walaspringboot.services.RatingService;
import com.joseluisgs.walaspringboot.services.FavoriteService;
import com.joseluisgs.walaspringboot.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
// Fijamos ya de por si una ruta por defecto para escuchar en este controlador
@RequestMapping("/public")
public class ZonaPublicaController {

    // Vamos a usar el servicio de producto
    final
    ProductService productoServicio;

    @Autowired
    RatingService ratingService;

    @Autowired
    FavoriteService favoriteService;

    @Autowired
    UserService userService;

    @Autowired
    public ZonaPublicaController(ProductService productoServicio) {
        this.productoServicio = productoServicio;
    }

    // De esta manera siempre tenemos los productos no vendidos
    @ModelAttribute("productos")
    public List<Product> productosNoVendidos() {
        return productoServicio.productosSinVender();
    }
    
    // Inyectamos las categorías en el modelo para los filtros
    @ModelAttribute("categorias")
    public ProductCategory[] getCategorias() {
        return ProductCategory.values();
    }


    // Escuchamos en las dos rutas por defecto
    // Tenemos una query, del buscador y no es obligatoria
    @GetMapping({"", "/", "/index"})
    public String index(Model model,
                        @RequestParam(name = "q", required = false) String query,
                        @RequestParam(name = "categoria", required = false) String categoria,
                        @RequestParam(name = "minPrecio", required = false) Float minPrecio,
                        @RequestParam(name = "maxPrecio", required = false) Float maxPrecio,
                        @RequestParam(name = "page", defaultValue = "0") int page,
                        @RequestParam(name = "size", defaultValue = "12") int size) {

        // Crear Pageable
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        
        // Buscar con filtros y paginación
        Page<Product> productosPage;
        
        if (query != null && !query.trim().isEmpty()) {
            productosPage = productoServicio.findByNombreContainingIgnoreCase(query, pageable);
        } else if (categoria != null && !categoria.isEmpty()) {
            try {
                ProductCategory cat = ProductCategory.valueOf(categoria);
                productosPage = productoServicio.findByCategoria(cat, pageable);
            } catch (IllegalArgumentException e) {
                productosPage = productoServicio.findAll(pageable);
            }
        } else if (minPrecio != null || maxPrecio != null) {
            Float min = minPrecio != null ? minPrecio : 0f;
            Float max = maxPrecio != null ? maxPrecio : Float.MAX_VALUE;
            productosPage = productoServicio.findByPrecioBetween(min, max, pageable);
        } else {
            productosPage = productoServicio.findAll(pageable);
        }
        
        // Pasar datos a la vista
        model.addAttribute("productos", productosPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productosPage.getTotalPages());
        model.addAttribute("totalElements", productosPage.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("hasNext", productosPage.hasNext());
        model.addAttribute("hasPrevious", productosPage.hasPrevious());
        
        // Mantener filtros
        model.addAttribute("categoriaActual", categoria);
        model.addAttribute("q", query);
        model.addAttribute("minPrecio", minPrecio);
        model.addAttribute("maxPrecio", maxPrecio);

        return "index";
    }

    // Devolvemos el producto con id establecida
    @GetMapping("/producto/{id}")
    public String showProduct(Model model, @PathVariable Long id) {
        //Buscamos pro id
        Product result = productoServicio.findById(id);
        if (result != null) {
            // Incrementar vistas automáticamente
            productoServicio.incrementarVistas(id);
            
            // Si lo encotramos lo añadimos al modelo y se lo pasamos
            model.addAttribute("producto", result);
            
            // Añadir información de rating
            Double avgRating = ratingService.getAverageRating(result);
            Long ratingCount = ratingService.getCountRatings(result);
            model.addAttribute("averageRating", avgRating != null ? avgRating : 0.0);
            model.addAttribute("ratingCount", ratingCount);
            
            // Añadir información de favoritos si está autenticado
            try {
                String email = SecurityContextHolder.getContext().getAuthentication().getName();
                if (email != null && !email.equals("anonymousUser")) {
                    var usuario = userService.buscarPorEmail(email);
                    if (usuario != null) {
                        boolean isFavorite = favoriteService.isFavorite(usuario, id);
                        model.addAttribute("isFavorite", isFavorite);
                    }
                }
            } catch (Exception e) {
                // Usuario no autenticado
            }
            
            return "producto";
        }
        // si no a public
        return "redirect:/public";
    }
}
