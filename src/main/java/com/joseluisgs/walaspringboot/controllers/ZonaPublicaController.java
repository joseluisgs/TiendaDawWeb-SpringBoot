package com.joseluisgs.walaspringboot.controllers;

import com.joseluisgs.walaspringboot.models.Product;
import com.joseluisgs.walaspringboot.models.ProductCategory;
import com.joseluisgs.walaspringboot.services.ProductService;
import com.joseluisgs.walaspringboot.services.RatingService;
import com.joseluisgs.walaspringboot.services.FavoriteService;
import com.joseluisgs.walaspringboot.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
                        @RequestParam(name = "maxPrecio", required = false) Float maxPrecio) {

        List<Product> productos;

        // Aplicar filtro de búsqueda por texto
        if (query != null && !query.trim().isEmpty()) {
            productos = productoServicio.buscar(query);
        } 
        // Aplicar filtro por categoría
        else if (categoria != null && !categoria.trim().isEmpty()) {
            try {
                ProductCategory cat = ProductCategory.valueOf(categoria);
                productos = productoServicio.findByCategoria(cat);
            } catch (IllegalArgumentException e) {
                // Si la categoría no es válida, mostrar todos los productos
                productos = productoServicio.productosSinVender();
            }
        } else {
            productos = productoServicio.productosSinVender();
        }

        // Aplicar filtros de precio
        if (minPrecio != null && maxPrecio != null) {
            productos = productos.stream()
                    .filter(p -> p.getPrecio() >= minPrecio && p.getPrecio() <= maxPrecio)
                    .toList();
        } else if (minPrecio != null) {
            productos = productos.stream()
                    .filter(p -> p.getPrecio() >= minPrecio)
                    .toList();
        } else if (maxPrecio != null) {
            productos = productos.stream()
                    .filter(p -> p.getPrecio() <= maxPrecio)
                    .toList();
        }

        model.addAttribute("productos", productos);
        model.addAttribute("q", query);
        model.addAttribute("categoriaActual", categoria);
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
