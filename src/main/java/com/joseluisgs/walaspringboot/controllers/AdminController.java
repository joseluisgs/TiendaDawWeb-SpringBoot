package com.joseluisgs.walaspringboot.controllers;

import com.joseluisgs.walaspringboot.services.PurchaseService;
import com.joseluisgs.walaspringboot.services.ProductService;
import com.joseluisgs.walaspringboot.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {

    @Autowired
    private ProductService productoServicio;

    @Autowired
    private UserService usuarioServicio;

    @Autowired
    private PurchaseService compraServicio;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Estadísticas generales
        long totalProductos = productoServicio.findAll().size();
        long totalUsuarios = usuarioServicio.findAll().size();
        long totalCompras = compraServicio.findAll().size();

        // Productos recientes (últimos 5)
        var productosRecientes = productoServicio.findAll().stream()
                .sorted((p1, p2) -> Long.compare(p2.getId(), p1.getId()))
                .limit(5)
                .toList();

        // Usuarios recientes (últimos 5)
        var usuariosRecientes = usuarioServicio.findAll().stream()
                .sorted((u1, u2) -> u2.getFechaAlta().compareTo(u1.getFechaAlta()))
                .limit(5)
                .toList();

        model.addAttribute("totalProductos", totalProductos);
        model.addAttribute("totalUsuarios", totalUsuarios);
        model.addAttribute("totalCompras", totalCompras);
        model.addAttribute("productosRecientes", productosRecientes);
        model.addAttribute("usuariosRecientes", usuariosRecientes);

        return "admin/dashboard";
    }

    @GetMapping("/usuarios")
    public String gestionUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioServicio.findAll());
        return "admin/usuarios";
    }

    @GetMapping("/productos")
    public String gestionProductos(Model model) {
        model.addAttribute("productos", productoServicio.findAll());
        return "admin/productos";
    }

    @GetMapping("/usuarios/eliminar/{id}")
    public String eliminarUsuario(@org.springframework.web.bind.annotation.PathVariable Long id) {
        usuarioServicio.borrar(id);
        return "redirect:/admin/usuarios";
    }

    @GetMapping("/productos/eliminar/{id}")
    public String eliminarProducto(@org.springframework.web.bind.annotation.PathVariable Long id) {
        productoServicio.borrar(productoServicio.findById(id));
        return "redirect:/admin/productos";
    }

    @GetMapping("/ventas")
    public String gestionVentas(Model model) {
        model.addAttribute("compras", compraServicio.findAll());
        return "admin/ventas";
    }
}
