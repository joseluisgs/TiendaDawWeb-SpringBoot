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
    public String eliminarUsuario(@org.springframework.web.bind.annotation.PathVariable Long id,
                                 org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes,
                                 org.springframework.security.core.Authentication auth) {
        try {
            var usuario = usuarioServicio.findById(id);
            
            if (usuario == null) {
                redirectAttributes.addFlashAttribute("error", "Usuario no encontrado.");
                return "redirect:/admin/usuarios";
            }
            
            // Verificar si es el usuario admin principal
            if ("admin@walaspringboot.com".equals(usuario.getEmail())) {
                redirectAttributes.addFlashAttribute("error", 
                    "No se puede eliminar el usuario administrador principal.");
                return "redirect:/admin/usuarios";
            }
            
            // Verificar productos activos asociados
            long productCount = productoServicio.countByPropietarioActive(usuario);
            if (productCount > 0) {
                redirectAttributes.addFlashAttribute("error", 
                    String.format("No se puede eliminar el usuario '%s %s' porque tiene %d productos asociados. " +
                    "Elimine primero todos sus productos o márquelos como eliminados.", 
                    usuario.getNombre(), usuario.getApellidos(), productCount));
                return "redirect:/admin/usuarios";
            }
            
            // Verificar compras realizadas
            long purchaseCount = compraServicio.countByPropietario(usuario);
            if (purchaseCount > 0) {
                redirectAttributes.addFlashAttribute("warning", 
                    String.format("El usuario '%s %s' tiene %d compras asociadas. Se marcará como eliminado " +
                    "pero se mantendrán sus compras para integridad histórica.", 
                    usuario.getNombre(), usuario.getApellidos(), purchaseCount));
            }
            
            // Soft delete
            usuarioServicio.softDelete(id, auth.getName());
            
            redirectAttributes.addFlashAttribute("success", 
                String.format("Usuario '%s %s' eliminado correctamente.", 
                usuario.getNombre(), usuario.getApellidos()));
                
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error al eliminar el usuario: " + e.getMessage());
        }
        
        return "redirect:/admin/usuarios";
    }

    @GetMapping("/productos/eliminar/{id}")
    public String eliminarProducto(@org.springframework.web.bind.annotation.PathVariable Long id,
                                  org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes,
                                  org.springframework.security.core.Authentication auth) {
        try {
            var producto = productoServicio.findById(id);
            
            if (producto == null) {
                redirectAttributes.addFlashAttribute("error", "Producto no encontrado.");
                return "redirect:/admin/productos";
            }
            
            // Verificar si ha sido vendido
            if (productoServicio.hasBeenSold(producto)) {
                redirectAttributes.addFlashAttribute("warning", 
                    String.format("El producto '%s' ya ha sido vendido y no puede eliminarse. " +
                    "Se ha marcado como no disponible para mantener la integridad de las ventas.", 
                    producto.getNombre()));
                
                // Soft delete para productos vendidos
                productoServicio.softDelete(id, auth.getName());
            } else {
                // Si no se ha vendido, permitir borrado físico
                productoServicio.borrar(producto);
                redirectAttributes.addFlashAttribute("success", 
                    String.format("Producto '%s' eliminado correctamente.", producto.getNombre()));
            }
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error al eliminar el producto: " + e.getMessage());
        }
        
        return "redirect:/admin/productos";
    }

    @GetMapping("/ventas")
    public String gestionVentas(Model model) {
        // Usar join fetch para evitar N+1 queries
        var compras = compraServicio.findAllWithProducts();
        
        // Calcular estadísticas del dashboard
        Double totalVentas = compras.stream()
            .mapToDouble(com.joseluisgs.walaspringboot.models.Purchase::getTotal)
            .sum();
        
        int totalTransacciones = compras.size();
        
        Double valorPromedio = totalTransacciones > 0 ? totalVentas / totalTransacciones : 0.0;
        
        model.addAttribute("compras", compras);
        model.addAttribute("totalVentas", totalVentas);
        model.addAttribute("totalTransacciones", totalTransacciones);
        model.addAttribute("valorPromedio", valorPromedio);
        
        return "admin/ventas";
    }
}
