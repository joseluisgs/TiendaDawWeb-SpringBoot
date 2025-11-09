package com.joseluisgs.walaspringboot.controllers;

import com.joseluisgs.walaspringboot.models.Purchase;
import com.joseluisgs.walaspringboot.models.Product;
import com.joseluisgs.walaspringboot.models.User;
import com.joseluisgs.walaspringboot.services.PurchaseService;
import com.joseluisgs.walaspringboot.services.ProductService;
import com.joseluisgs.walaspringboot.services.UserService;
import com.joseluisgs.walaspringboot.services.EmailService;
import com.joseluisgs.walaspringboot.utils.GeneradorPDF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.io.ByteArrayInputStream;
import java.util.*;

@Controller
@RequestMapping("/app") // Ruta por defecto base donde vamos a escuchar
public class PurchaseController {
    // Necesitamos los siguientes servicios
    // Para manejar la compra
    @Autowired
    PurchaseService compraServicio;

    // Para manejar los productos
    @Autowired
    ProductService productoServicio;

    // Para los usuarios
    @Autowired
    UserService usuarioServicio;

    // Para las sesiones
    @Autowired
    HttpSession session;

    // Para Email
    @Autowired
    EmailService emailService;


    // Para mapear el usuario identificado con lo que tenemos almacenado
    private User usuario;

    // Los metodos etiquetados como ModelAtribute, pornen en el modelo el resultado de realizar esta operación
    // Luego lo podremos recuperar en la vista
    @ModelAttribute("carrito")
    public List<Product> productosCarrito() {
        // Obtengo una lista de id alacenados en la sesión como carrito
        List<Long> contenido = (List<Long>) session.getAttribute("carrito");
        // Devulevo la lista de productos que tienen la id almacenada en la sesion
        return (contenido == null) ? null : productoServicio.variosPorId(contenido);
    }

    // Calcula el total del carrito
    @ModelAttribute("total_carrito")
    public Double totalCarrito() {
        List<Product> productosCarrito = productosCarrito(); // Aqui tenemos un ejemplo de como modelar
        if (productosCarrito != null)
            return productosCarrito.stream()
                    .mapToDouble(p -> p.getPrecio())
                    .sum();
        // Podríamos hacerlo con un foreach y no con una expresión lamda. En el fondo estamos recorriendo el array
        // y sumando los valores
        return 0.0;
    }

    // Devuelve el total de items que tiene el carrito. Podemos hacerlo así o con la sesión
    // Lo mostraré en navbar de las dos maneras
    @ModelAttribute("items_carrito")
    public String itemsCarrito() {
        List<Product> productosCarrito = productosCarrito(); // Aqui tenemos un ejemplo de como modelar
        if (productosCarrito != null)
            return Integer.toString(productosCarrito.size());
        return "";
    }

    // Muestro las compras asociadas al email que se ha registrado en la sesión
    @ModelAttribute("miscompras")
    public List<Purchase> misCompras() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        usuario = usuarioServicio.buscarPorEmail(email);
        return compraServicio.porPropietario(usuario);
    }

    // Como ya tenemos el modelo carrito y total_carrito definido, solo debemos ir a la platilla
    @GetMapping("/carrito")
    public String verCarrito(Model model) {
        return "/app/compra/carrito";
    }

    // AÑadimos un producto al carrito
    @GetMapping("/carrito/add/{id}")
    public String addCarrito(Model model, @PathVariable Long id) {
        // Sacamos el carrito de la sesión
        List<Long> contenido = (List<Long>) session.getAttribute("carrito");
        // Si es nulo, lo creamos
        if (contenido == null)
            contenido = new ArrayList<>();
        // Si no esta el producto lo añadimos. Esto es porque no podemos cambiar la cantidad
        if (!contenido.contains(id))
            contenido.add(id);
        // Almacenamos en la sesión el carrito y el número de items
        session.setAttribute("carrito", contenido);
        session.setAttribute("items_carrito", contenido.size());
        // Volvemos a la página del carrito
        return "redirect:/app/carrito";
    }

    // Elimina un elemento del carrito
    @GetMapping("/carrito/eliminar/{id}")
    public String borrarDeCarrito(Model model, @PathVariable Long id) {
        // Recuperamos el carrito
        List<Long> contenido = (List<Long>) session.getAttribute("carrito");
        // Si es nulo, lo mandamos a public
        if (contenido == null)
            return "redirect:/public";
        // Borramos el contenido
        contenido.remove(id);
        // Si está vacío, elimamos carrito de la sesión
        if (contenido.isEmpty()) {
            session.removeAttribute("carrito");
            session.removeAttribute("items_carrito");
        }

        else {
            // Si no lo añadimos
            session.setAttribute("carrito", contenido);
            session.setAttribute("items_carrito", contenido.size());
        }
        return "redirect:/app/carrito";

    }

    // Mostrar confirmación antes de finalizar compra
    @GetMapping("/carrito/comprar")
    public String confirmarCompra(Model model) {
        // Recuperamos el carrito
        List<Long> contenido = (List<Long>) session.getAttribute("carrito");
        if (contenido == null || contenido.isEmpty())
            return "redirect:/public";
        
        // Los productos ya están en el modelo via @ModelAttribute
        return "/app/compra/confirmar";
    }

    // Finaliza una compra
    @GetMapping("/carrito/finalizar")
    public String checkout() {
        // Recuperamos el carrito
        List<Long> contenido = (List<Long>) session.getAttribute("carrito");
        if (contenido == null) // Es es nulo a public
            return "redirect:/public";

        // Obtenemos la lista de productos
        List<Product> productos = productosCarrito();
        // Los insertamos en la compra
        Purchase c = compraServicio.insertar(new Purchase(), usuario);
        // Sitaxis de java nueva por cada producto p, ejecutamos compra servicio y asociamos p a la compra c
        productos.forEach(p -> compraServicio.addProductoCompra(p, c));
        // Elimanos de la sesión el carrito
        session.removeAttribute("carrito");
        session.removeAttribute("items_carrito");

        // Enviar email de confirmación de manera asíncrona para no bloquear
        new Thread(() -> {
            try {
                emailService.enviarEmailConfirmacionCompra(c);
            } catch (Exception e) {
                // Log error but don't fail the purchase
                System.err.println("Error enviando email: " + e.getMessage());
            }
        }).start();

        // Abrimos la factura
        return "redirect:/app/miscompras/factura/"+c.getId();

    }

    // Mustra las compras e un listado
    @GetMapping("/miscompras")
    public String verMisCompras(Model model) {
        // Obtener todos los productos para poder mostrarlos por compra
        List<Product> todosProductos = productoServicio.findAll();
        model.addAttribute("productos", todosProductos);
        return "/app/compra/listado";
    }

    //Obtiene la factura con un id
    @GetMapping("/miscompras/factura/{id}")
    public String factura(Model model, @PathVariable Long id) {
        // Recupero la compra mediante su ID
        Purchase c = compraServicio.buscarPorId(id);
        // Obtengo la lista de productos por su id asociados a la compra
        List<Product> productos = productoServicio.productosDeUnaCompra(c);
        // Los metemos en el modelo
        model.addAttribute("productos", productos);
        model.addAttribute("compra", c);
        // Calculamos el total de la compra, es un for each
        Double total = productos.stream().mapToDouble(p -> p.getPrecio()).sum();
        model.addAttribute("total_compra", total);
        model.addAttribute("total", total);
        // Devolvemos la factura
        return "/app/compra/factura";
    }

    // Saco una factura en PDF usando itex - Simple and working version
    @RequestMapping(value = "/miscompras/factura/{id}/pdf", method = RequestMethod.GET, produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> facturaPDF(@PathVariable Long id) {
        // Recupero la compra mediante su ID
        Purchase compra = compraServicio.buscarPorId(id);
        // Obtengo la lista de productos por su id asociados a la compra
        List<Product> productos = productoServicio.productosDeUnaCompra(compra);
        // Total de la compra
        Double total = productos.stream().mapToDouble(p -> p.getPrecio()).sum();

        ByteArrayInputStream bis = GeneradorPDF.factura2PDF(compra, productos, total);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=factura_"+ compra.getId()+".pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }


}
