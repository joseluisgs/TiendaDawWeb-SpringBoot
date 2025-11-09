package com.joseluisgs.walaspringboot.controllers;

import com.joseluisgs.walaspringboot.models.Product;
import com.joseluisgs.walaspringboot.models.ProductCategory;
import com.joseluisgs.walaspringboot.models.User;
import com.joseluisgs.walaspringboot.services.ProductService;
import com.joseluisgs.walaspringboot.services.UserService;
import com.joseluisgs.walaspringboot.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import jakarta.validation.Valid;
import java.nio.file.Files;
import java.util.List;

@Controller
@RequestMapping("/app") // Ruta por defecto
public class ProductController {

    // Servicio de producto
    @Autowired
    ProductService productoServicio;

    // Servicio de usuario
    @Autowired
    UserService usuarioServicio;

    private User usuario;

    // Servicio de almacenamiento
    @Autowired
    StorageService storageService;

    // Inyectamos en el modelo automáticamente la lista de mis productos
    @ModelAttribute("misproductos")
    public List<Product> misProductos() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        usuario = usuarioServicio.buscarPorEmail(email);
        return productoServicio.productosDeUnPropietario(usuario);
    }
    
    // Inyectamos las categorías en el modelo para los formularios
    @ModelAttribute("categorias")
    public ProductCategory[] getCategorias() {
        return ProductCategory.values();
    }

    // Obtenemos la lista de mis productos, de hecho dejamos buscar, recibiendo el modelo
    @GetMapping("/misproductos")
    public String list(Model model, @RequestParam(name = "q", required = false) String query) {
        if (query != null)
            // Asignamos al modelo los productos
            model.addAttribute("misproductos", productoServicio.buscarMisProductos(query, usuario));
        return "app/producto/lista";
    }

    // Eliminamos el producto
    @GetMapping("/misproductos/eliminar/{id}")
    public String eliminar(@PathVariable Long id, 
                          org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes,
                          org.springframework.security.core.Authentication auth) {
        try {
            // Buscamos el producto
            Product producto = productoServicio.findById(id);
            
            if (producto == null) {
                redirectAttributes.addFlashAttribute("error", "Producto no encontrado.");
                return "redirect:/app/misproductos";
            }
            
            // Verificar si el usuario es el propietario
            String email = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
            User currentUser = usuarioServicio.buscarPorEmail(email);
            if (!producto.getPropietario().equals(currentUser)) {
                redirectAttributes.addFlashAttribute("error", 
                    "No tiene permisos para eliminar este producto.");
                return "redirect:/app/misproductos";
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
        
        return "redirect:/app/misproductos";
    }

    // Creamos un nuevo producto
    @GetMapping("/misproducto/nuevo")
    public String nuevoProductoForm(Model model) {
        // Lo insertamos en el modelo
        model.addAttribute("producto", new Product());
        return "app/producto/ficha";
    }

    @PostMapping("/misproducto/nuevo/submit")
    public String nuevoProductoSubmit(@Valid @ModelAttribute Product producto,
                                      @RequestParam("file") MultipartFile file,
                                      @RequestParam(value = "categoria", required = false) String categoria,
                                      BindingResult bindingResult) {

        // Validar y asignar categoría
        if (categoria != null && !categoria.isEmpty()) {
            try {
                producto.setCategoria(ProductCategory.valueOf(categoria));
            } catch (IllegalArgumentException e) {
                bindingResult.rejectValue("categoria", "error.categoria", "Categoría inválida");
                return "app/producto/ficha";
            }
        }
        
        // Si no tiene errores
        if (bindingResult.hasErrors()) {
            return "app/producto/ficha";
        }else {
            // Subimos las imagenes
            if (!file.isEmpty()) {
                String imagen = storageService.store(file);
                producto.setImagen(MvcUriComponentsBuilder
                        .fromMethodName(FilesController.class, "serveFile", imagen).build().toUriString());
            } else {
                // Asignar imagen por defecto si no se sube archivo
                producto.setImagen(Product.DEFAULT_IMAGE);
            }
            // Indicamos el propietario
            producto.setPropietario(usuario);
            //nsertamos
            productoServicio.insertar(producto);
            return "redirect:/app/misproductos";
        }
    }

    // Actualizamos el producto
    @GetMapping("/misproductos/editar/{id}")
    public String eliminar(@PathVariable Long id, Model model) {
        // Buscamos el producto
        Product p = productoServicio.findById(id);
        if (p != null) {
            model.addAttribute("producto", p);
            return "app/producto/ficha";
        } else {
            return "redirect:/app/misproductos";
        }
    }

    @PostMapping("/misproductos/editar/submit")
    public String editarEmpleadoSubmit(@Valid @ModelAttribute("producto") Product actualProducto,
                                       @RequestParam("file") MultipartFile file,
                                       @RequestParam(value = "categoria", required = false) String categoria,
                                        BindingResult bindingResult) {
        
        // Validar y asignar categoría
        if (categoria != null && !categoria.isEmpty()) {
            try {
                actualProducto.setCategoria(ProductCategory.valueOf(categoria));
            } catch (IllegalArgumentException e) {
                bindingResult.rejectValue("categoria", "error.categoria", "Categoría inválida");
                return "app/producto/ficha";
            }
        }
        
        // Si no tiene errores
        if (bindingResult.hasErrors()) {
            return "app/producto/ficha";
        } else {
            // Lo que tenga que hacer, buscamos el antiguo producto para sacar los datos
            Product p = productoServicio.findById(actualProducto.getId());
            // Obtenemos el usuario
            // Porque es el único campo que no le hemos podido pasar al formulario
            actualProducto.setPropietario(p.getPropietario());

            // Procesamos las imagenes
            actualProducto.setImagen(p.getImagen());
            // Si existe que me han enviado el fichero y que la imagen antigua está almacenada

            // Asignamos la nueva
            if (!file.isEmpty()) {
                // Borramos la antigua si se puede si existe en el nuestro directorio (solo si no es una URL externa)
                if (p.getImagen() != null && !p.getImagen().startsWith("http")) {
                    storageService.delete(p.getImagen());
                }
                // Subimos la nueva
                String imagen = storageService.store(file);
                actualProducto.setImagen(MvcUriComponentsBuilder
                        .fromMethodName(FilesController.class, "serveFile", imagen).build().toUriString());
            }
            // Si no se sube archivo, mantener imagen actual (ya establecida en línea 130)
            // Actualizamos el producto
            productoServicio.editar(actualProducto);
            return "redirect:/app/misproductos";
        }
    }


}
