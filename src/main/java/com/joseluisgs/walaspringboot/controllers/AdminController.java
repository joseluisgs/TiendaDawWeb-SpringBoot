package com.joseluisgs.walaspringboot.controllers;

import com.joseluisgs.walaspringboot.services.PurchaseService;
import com.joseluisgs.walaspringboot.services.ProductService;
import com.joseluisgs.walaspringboot.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String gestionUsuarios(Model model,
                                 @org.springframework.web.bind.annotation.RequestParam(name = "q", required = false) String query,
                                 @org.springframework.web.bind.annotation.RequestParam(name = "rol", required = false) String rol,
                                 @org.springframework.web.bind.annotation.RequestParam(name = "page", defaultValue = "0") int page,
                                 @org.springframework.web.bind.annotation.RequestParam(name = "size", defaultValue = "10") int size) {
        
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, 
            org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "id"));
        
        org.springframework.data.domain.Page<com.joseluisgs.walaspringboot.models.User> usuariosPage;
        
        // Aplicar filtros según parámetros
        if (query != null && !query.trim().isEmpty() && rol != null && !rol.isEmpty()) {
            usuariosPage = usuarioServicio.findBySearchAndRolPaginated(query, rol, pageable);
        } else if (query != null && !query.trim().isEmpty()) {
            usuariosPage = usuarioServicio.findBySearchPaginated(query, pageable);
        } else if (rol != null && !rol.isEmpty()) {
            usuariosPage = usuarioServicio.findByRolPaginated(rol, pageable);
        } else {
            usuariosPage = usuarioServicio.findAllPaginated(pageable);
        }
        
        // Pasar datos a la vista
        model.addAttribute("usuarios", usuariosPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", usuariosPage.getTotalPages());
        model.addAttribute("totalElements", usuariosPage.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("hasNext", usuariosPage.hasNext());
        model.addAttribute("hasPrevious", usuariosPage.hasPrevious());
        
        // Mantener filtros
        model.addAttribute("q", query);
        model.addAttribute("rolActual", rol);
        
        return "admin/usuarios";
    }

    @GetMapping("/productos")
    public String gestionProductos(Model model,
                                  @org.springframework.web.bind.annotation.RequestParam(name = "q", required = false) String query,
                                  @org.springframework.web.bind.annotation.RequestParam(name = "categoria", required = false) String categoria,
                                  @org.springframework.web.bind.annotation.RequestParam(name = "propietarioId", required = false) Long propietarioId,
                                  @org.springframework.web.bind.annotation.RequestParam(name = "page", defaultValue = "0") int page,
                                  @org.springframework.web.bind.annotation.RequestParam(name = "size", defaultValue = "10") int size) {
        
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, 
            org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "id"));
        
        org.springframework.data.domain.Page<com.joseluisgs.walaspringboot.models.Product> productosPage;
        
        // Aplicar filtros según parámetros
        boolean hasQuery = query != null && !query.trim().isEmpty();
        boolean hasCategoria = categoria != null && !categoria.isEmpty();
        boolean hasPropietario = propietarioId != null;
        
        if (hasQuery && hasCategoria && hasPropietario) {
            com.joseluisgs.walaspringboot.models.ProductCategory cat = com.joseluisgs.walaspringboot.models.ProductCategory.valueOf(categoria);
            productosPage = productoServicio.findByAllFiltersActivePaginated(query, cat, propietarioId, pageable);
        } else if (hasQuery && hasCategoria) {
            com.joseluisgs.walaspringboot.models.ProductCategory cat = com.joseluisgs.walaspringboot.models.ProductCategory.valueOf(categoria);
            productosPage = productoServicio.findByNombreAndCategoriaActivePaginated(query, cat, pageable);
        } else if (hasQuery && hasPropietario) {
            productosPage = productoServicio.findByNombreAndPropietarioActivePaginated(query, propietarioId, pageable);
        } else if (hasCategoria && hasPropietario) {
            com.joseluisgs.walaspringboot.models.ProductCategory cat = com.joseluisgs.walaspringboot.models.ProductCategory.valueOf(categoria);
            productosPage = productoServicio.findByCategoriaAndPropietarioActivePaginated(cat, propietarioId, pageable);
        } else if (hasQuery) {
            productosPage = productoServicio.findByNombreContainingActivePaginated(query, pageable);
        } else if (hasCategoria) {
            com.joseluisgs.walaspringboot.models.ProductCategory cat = com.joseluisgs.walaspringboot.models.ProductCategory.valueOf(categoria);
            productosPage = productoServicio.findByCategoriaActivePaginated(cat, pageable);
        } else if (hasPropietario) {
            productosPage = productoServicio.findByPropietarioIdActivePaginated(propietarioId, pageable);
        } else {
            productosPage = productoServicio.findAllActivePaginated(pageable);
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
        model.addAttribute("q", query);
        model.addAttribute("categoriaActual", categoria);
        model.addAttribute("propietarioIdActual", propietarioId);
        
        // Agregar listas para los filtros
        model.addAttribute("categorias", com.joseluisgs.walaspringboot.models.ProductCategory.values());
        model.addAttribute("usuarios", usuarioServicio.findAll());
        
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
            if ("admin@waladaw.com".equals(usuario.getEmail())) {
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
    public String gestionVentas(Model model,
                               @org.springframework.web.bind.annotation.RequestParam(name = "desde", required = false) String desde,
                               @org.springframework.web.bind.annotation.RequestParam(name = "hasta", required = false) String hasta,
                               @org.springframework.web.bind.annotation.RequestParam(name = "propietarioId", required = false) Long propietarioId,
                               @org.springframework.web.bind.annotation.RequestParam(name = "page", defaultValue = "0") int page,
                               @org.springframework.web.bind.annotation.RequestParam(name = "size", defaultValue = "10") int size) {
        
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, 
            org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "fechaCompra"));
        
        org.springframework.data.domain.Page<com.joseluisgs.walaspringboot.models.Purchase> comprasPage;
        
        // Aplicar filtros según parámetros
        boolean hasFechas = desde != null && !desde.isEmpty() && hasta != null && !hasta.isEmpty();
        boolean hasPropietario = propietarioId != null;
        
        if (hasFechas && hasPropietario) {
            java.time.LocalDate fechaDesde = java.time.LocalDate.parse(desde);
            java.time.LocalDate fechaHasta = java.time.LocalDate.parse(hasta);
            comprasPage = compraServicio.findByFechaCompraAndPropietario(fechaDesde, fechaHasta, propietarioId, pageable);
        } else if (hasFechas) {
            java.time.LocalDate fechaDesde = java.time.LocalDate.parse(desde);
            java.time.LocalDate fechaHasta = java.time.LocalDate.parse(hasta);
            comprasPage = compraServicio.findByFechaCompraBetween(fechaDesde, fechaHasta, pageable);
        } else if (hasPropietario) {
            comprasPage = compraServicio.findByPropietarioIdPaginated(propietarioId, pageable);
        } else {
            comprasPage = compraServicio.findAllPaginated(pageable);
        }
        
        // Calcular estadísticas del dashboard (sobre todas las compras, no solo la página actual)
        var todasCompras = compraServicio.findAllWithProducts();
        Double totalVentas = todasCompras.stream()
            .mapToDouble(com.joseluisgs.walaspringboot.models.Purchase::getTotal)
            .sum();
        
        int totalTransacciones = todasCompras.size();
        Double valorPromedio = totalTransacciones > 0 ? totalVentas / totalTransacciones : 0.0;
        
        // Pasar datos a la vista
        model.addAttribute("compras", comprasPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", comprasPage.getTotalPages());
        model.addAttribute("totalElements", comprasPage.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("hasNext", comprasPage.hasNext());
        model.addAttribute("hasPrevious", comprasPage.hasPrevious());
        
        // Mantener filtros
        model.addAttribute("desde", desde);
        model.addAttribute("hasta", hasta);
        model.addAttribute("propietarioIdActual", propietarioId);
        
        // Estadísticas
        model.addAttribute("totalVentas", totalVentas);
        model.addAttribute("totalTransacciones", totalTransacciones);
        model.addAttribute("valorPromedio", valorPromedio);
        
        // Lista de usuarios para el filtro
        model.addAttribute("usuarios", usuarioServicio.findAll());
        
        return "admin/ventas";
    }

    @GetMapping("/usuarios/{id}")
    public String detalleUsuario(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            var usuario = usuarioServicio.findById(id);
            
            if (usuario == null) {
                redirectAttributes.addFlashAttribute("error", "Usuario no encontrado.");
                return "redirect:/admin/usuarios";
            }
            
            // Obtener productos del usuario
            long productosActivos = productoServicio.countByPropietarioActive(usuario);
            long comprasRealizadas = compraServicio.countByPropietario(usuario);
            
            // Obtener lista de productos del usuario
            var productosUsuario = productoServicio.productosDeUnPropietario(usuario);
            
            model.addAttribute("usuario", usuario);
            model.addAttribute("productosActivos", productosActivos);
            model.addAttribute("comprasRealizadas", comprasRealizadas);
            model.addAttribute("productosUsuario", productosUsuario);
            
            return "admin/detalle-usuario";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cargar el usuario: " + e.getMessage());
            return "redirect:/admin/usuarios";
        }
    }

    @GetMapping("/productos/{id}")
    public String detalleProducto(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            var producto = productoServicio.findById(id);
            
            if (producto == null) {
                redirectAttributes.addFlashAttribute("error", "Producto no encontrado.");
                return "redirect:/admin/productos";
            }
            
            // Verificar si ha sido vendido
            boolean vendido = productoServicio.hasBeenSold(producto);
            
            model.addAttribute("producto", producto);
            model.addAttribute("vendido", vendido);
            
            return "admin/detalle-producto";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cargar el producto: " + e.getMessage());
            return "redirect:/admin/productos";
        }
    }
}
