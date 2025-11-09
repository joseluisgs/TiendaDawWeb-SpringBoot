# 📖 Tutorial de Spring MVC - Aplicaciones Web Dinámicas

## Tabla de Contenidos

- [Introducción](#introducción)
- [Arquitectura MVC](#arquitectura-mvc)
- [Configuración Básica](#configuración-básica)
- [Controllers](#controllers)
- [Modelos y Vista](#modelos-y-vista)
- [Manejo de Formularios](#manejo-de-formularios)
- [Validación](#validación)
- [Seguridad](#seguridad)
- [Internacionalización](#internacionalización)
- [Mejores Prácticas](#mejores-prácticas)

## Introducción

Spring MVC es un framework web basado en el patrón Model-View-Controller que facilita la creación de aplicaciones web robustas y escalables en Java.

### ¿Qué es MVC?

**Model-View-Controller (MVC)** es un patrón de diseño que separa la lógica de negocio de la presentación:

- **Model (Modelo)**: Representa los datos y la lógica de negocio
- **View (Vista)**: Presenta los datos al usuario (HTML, JSON, etc.)
- **Controller (Controlador)**: Maneja las peticiones HTTP y coordina Model y View

### Ventajas de Spring MVC

✅ **Separación de responsabilidades**: Código más mantenible y testeable  
✅ **Configuración flexible**: Basada en anotaciones o XML  
✅ **Integración con Spring**: Acceso a todas las características de Spring Framework  
✅ **Soporte para múltiples vistas**: Pebble, Thymeleaf, JSP, FreeMarker  
✅ **RESTful**: Fácil creación de APIs REST  

## Arquitectura MVC

### Flujo de una Petición HTTP

```
1. Usuario → Navegador
2. Navegador → HTTP Request → DispatcherServlet
3. DispatcherServlet → Handler Mapping (encuentra el Controller)
4. Controller → procesa la petición → devuelve Model y View
5. DispatcherServlet → View Resolver (encuentra la vista)
6. View → renderiza con el Model
7. DispatcherServlet → HTTP Response → Navegador
8. Navegador → Usuario
```

### Componentes Principales

#### 1. DispatcherServlet

El servlet frontal que recibe todas las peticiones HTTP y las delega a los controllers apropiados.

```java
// Configuración automática en Spring Boot
// No requiere configuración manual
```

#### 2. Controllers

Clases anotadas con `@Controller` que manejan peticiones HTTP.

```java
@Controller
@RequestMapping("/productos")
public class ProductoController {
    
    @GetMapping("/lista")
    public String listar(Model model) {
        model.addAttribute("productos", productoServicio.findAll());
        return "productos/lista";
    }
}
```

#### 3. Model

Contenedor de datos que se pasan a la vista.

```java
@GetMapping("/detalle/{id}")
public String detalle(@PathVariable Long id, Model model) {
    Producto producto = productoServicio.findById(id);
    model.addAttribute("producto", producto);
    return "productos/detalle";
}
```

#### 4. View Resolver

Resuelve el nombre lógico de la vista a una plantilla física.

```properties
# application.properties
pebble.suffix=.peb
pebble.cache=false
```

## Configuración Básica

### Estructura del Proyecto

```
src/main/
├── java/
│   └── com.ejemplo.app/
│       ├── configuracion/
│       │   └── WebConfig.java
│       ├── controladores/
│       │   ├── HomeController.java
│       │   └── ProductoController.java
│       ├── modelos/
│       │   └── Producto.java
│       ├── repositorios/
│       │   └── ProductoRepository.java
│       └── servicios/
│           └── ProductoServicio.java
└── resources/
    ├── application.properties
    ├── templates/
    │   ├── index.peb
    │   └── productos/
    │       ├── lista.peb
    │       └── detalle.peb
    └── static/
        ├── css/
        ├── js/
        └── images/
```

### Dependencias Gradle

```kotlin
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("io.pebbletemplates:pebble-spring-boot-starter:3.2.2")
}
```

## Controllers

### Anotaciones Básicas

#### @Controller vs @RestController

```java
// Para aplicaciones web con vistas
@Controller
public class WebController {
    @GetMapping("/home")
    public String home(Model model) {
        return "home"; // Retorna nombre de vista
    }
}

// Para APIs REST que retornan JSON
@RestController
@RequestMapping("/api")
public class ApiController {
    @GetMapping("/productos")
    public List<Producto> productos() {
        return productoServicio.findAll(); // Retorna JSON automáticamente
    }
}
```

#### @RequestMapping

Mapea URLs a métodos del controller:

```java
@Controller
@RequestMapping("/productos") // Base URL
public class ProductoController {
    
    @GetMapping // GET /productos
    public String lista() { }
    
    @GetMapping("/{id}") // GET /productos/123
    public String detalle(@PathVariable Long id) { }
    
    @PostMapping // POST /productos
    public String crear(@ModelAttribute Producto producto) { }
    
    @PutMapping("/{id}") // PUT /productos/123
    public String actualizar(@PathVariable Long id, @ModelAttribute Producto producto) { }
    
    @DeleteMapping("/{id}") // DELETE /productos/123
    public String eliminar(@PathVariable Long id) { }
}
```

### Parámetros de Método

#### @PathVariable

Extrae variables de la URL:

```java
@GetMapping("/producto/{id}")
public String detalle(@PathVariable Long id, Model model) {
    Producto producto = productoServicio.findById(id);
    model.addAttribute("producto", producto);
    return "producto/detalle";
}

// URL: /producto/123 → id = 123
```

#### @RequestParam

Extrae parámetros de query string:

```java
@GetMapping("/buscar")
public String buscar(
    @RequestParam(required = false) String q,
    @RequestParam(defaultValue = "0") int page,
    Model model
) {
    List<Producto> productos = productoServicio.buscar(q, page);
    model.addAttribute("productos", productos);
    return "productos/lista";
}

// URL: /buscar?q=laptop&page=2
// q = "laptop", page = 2
```

#### @ModelAttribute

Vincula datos de formulario a objetos Java:

```java
@PostMapping("/producto/crear")
public String crear(@ModelAttribute Producto producto) {
    productoServicio.guardar(producto);
    return "redirect:/productos";
}
```

#### @RequestBody

Recibe datos JSON (para APIs REST):

```java
@PostMapping("/api/productos")
@ResponseBody
public Producto crearApi(@RequestBody Producto producto) {
    return productoServicio.guardar(producto);
}
```

### Model y ModelAndView - Diferencias y Cuándo Usar Cada Uno

Ambos `Model` y `ModelAndView` sirven para pasar datos del controller a la vista, pero tienen diferencias importantes:

#### Model - Enfoque Moderno y Flexible

**Características:**
- ✅ Interfaz más simple y ligera
- ✅ El nombre de la vista se devuelve como String
- ✅ Permite múltiples return según lógica
- ✅ Más fácil de leer y mantener

**Ejemplo básico:**

```java
@GetMapping("/productos")
public String lista(Model model) {
    model.addAttribute("productos", productoServicio.findAll());
    model.addAttribute("titulo", "Lista de Productos");
    return "productos/lista"; // Nombre de la vista como String
}
```

**Con lógica condicional:**

```java
@GetMapping("/productos/{id}")
public String detalle(@PathVariable Long id, Model model) {
    Producto producto = productoServicio.findById(id);
    
    if (producto == null) {
        model.addAttribute("error", "Producto no encontrado");
        return "error/404"; // Fácil retornar diferentes vistas
    }
    
    model.addAttribute("producto", producto);
    return "productos/detalle";
}
```

#### ModelAndView - Enfoque Clásico Todo-en-Uno

**Características:**
- ✅ Encapsula vista y modelo en un solo objeto
- ✅ Útil cuando se necesita más control sobre la respuesta
- ✅ Permite configurar códigos de estado HTTP
- ✅ Histórico de Spring MVC (anterior a Spring 3.0)

**Ejemplo básico:**

```java
@GetMapping("/productos")
public ModelAndView lista() {
    ModelAndView mav = new ModelAndView("productos/lista");
    mav.addObject("productos", productoServicio.findAll());
    mav.addObject("titulo", "Lista de Productos");
    return mav; // Devuelve el objeto ModelAndView completo
}
```

**Con redirección:**

```java
@PostMapping("/producto/guardar")
public ModelAndView guardar(@ModelAttribute Producto producto) {
    productoServicio.insertar(producto);
    
    // Redirección con ModelAndView
    ModelAndView mav = new ModelAndView("redirect:/productos");
    return mav;
}
```

**Con código de estado:**

```java
@GetMapping("/admin/estadisticas")
public ModelAndView estadisticas(Principal principal) {
    if (principal == null) {
        // Retornar con código 403 Forbidden
        ModelAndView mav = new ModelAndView("error/403");
        mav.setStatus(HttpStatus.FORBIDDEN);
        return mav;
    }
    
    ModelAndView mav = new ModelAndView("admin/estadisticas");
    mav.addObject("datos", estadisticasServicio.obtener());
    return mav;
}
```

#### Comparación Práctica

| Aspecto | Model | ModelAndView |
|---------|-------|--------------|
| **Sintaxis** | `model.addAttribute()` | `mav.addObject()` |
| **Retorno de vista** | String separado | Incluido en objeto |
| **Flexibilidad** | ✅ Alta | ⚙️ Media |
| **Legibilidad** | ✅ Mejor | ⚙️ Aceptable |
| **Caso de uso típico** | Mayoría de casos | Control avanzado |
| **Recomendación** | **Usar por defecto** | Solo casos especiales |

#### ¿Cuándo Usar Cada Uno?

**Usa Model cuando:**
- ✅ Desarrolles código nuevo (práctica moderna)
- ✅ Tengas lógica condicional con múltiples returns
- ✅ Quieras código más limpio y legible
- ✅ Trabajes con formularios simples

**Usa ModelAndView cuando:**
- ⚙️ Necesites configurar códigos de estado HTTP específicos
- ⚙️ Trabajes con código legacy que ya lo usa
- ⚙️ Necesites más control sobre la respuesta HTTP
- ⚙️ Manejes casos de error complejos

#### Ejemplo Completo Comparativo

**Con Model (Recomendado):**

```java
@Controller
@RequestMapping("/productos")
public class ProductoController {
    
    @Autowired
    private ProductoServicio productoServicio;
    
    @GetMapping
    public String listar(
        @RequestParam(required = false) String busqueda,
        Model model
    ) {
        List<Producto> productos;
        
        if (busqueda != null && !busqueda.isEmpty()) {
            productos = productoServicio.buscar(busqueda);
            model.addAttribute("busqueda", busqueda);
        } else {
            productos = productoServicio.findAll();
        }
        
        model.addAttribute("productos", productos);
        model.addAttribute("total", productos.size());
        return "productos/lista";
    }
    
    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        Producto producto = productoServicio.findById(id);
        
        if (producto == null) {
            return "redirect:/productos?error=notfound";
        }
        
        model.addAttribute("producto", producto);
        model.addAttribute("relacionados", 
            productoServicio.findByCategoria(producto.getCategoria()));
        return "productos/detalle";
    }
}
```

**Con ModelAndView:**

```java
@Controller
@RequestMapping("/productos")
public class ProductoControllerMAV {
    
    @Autowired
    private ProductoServicio productoServicio;
    
    @GetMapping
    public ModelAndView listar(
        @RequestParam(required = false) String busqueda
    ) {
        ModelAndView mav = new ModelAndView("productos/lista");
        
        List<Producto> productos;
        if (busqueda != null && !busqueda.isEmpty()) {
            productos = productoServicio.buscar(busqueda);
            mav.addObject("busqueda", busqueda);
        } else {
            productos = productoServicio.findAll();
        }
        
        mav.addObject("productos", productos);
        mav.addObject("total", productos.size());
        return mav;
    }
    
    @GetMapping("/{id}")
    public ModelAndView detalle(@PathVariable Long id) {
        Producto producto = productoServicio.findById(id);
        
        if (producto == null) {
            return new ModelAndView("redirect:/productos?error=notfound");
        }
        
        ModelAndView mav = new ModelAndView("productos/detalle");
        mav.addObject("producto", producto);
        mav.addObject("relacionados", 
            productoServicio.findByCategoria(producto.getCategoria()));
        return mav;
    }
}
```

**Conclusión:** Para el 95% de los casos, usa `Model`. Es más limpio, moderno y fácil de mantener.

### Atributos Globales

Usa `@ModelAttribute` a nivel de método para datos comunes:

```java
@Controller
public class BaseController {
    
    @ModelAttribute("appName")
    public String appName() {
        return "WalaSpringBoot";
    }
    
    @ModelAttribute("authenticated")
    public boolean isAuthenticated(Principal principal) {
        return principal != null;
    }
}
```

## Gestión de Sesiones HTTP

Las sesiones HTTP permiten mantener información del usuario entre diferentes peticiones. Son esenciales para carritos de compra, preferencias de usuario, datos temporales, etc.

### ¿Qué es una Sesión HTTP?

Una **sesión HTTP** es un mecanismo para almacenar información del usuario en el servidor durante múltiples peticiones. Spring crea automáticamente una sesión cuando la aplicación la necesita y la identifica mediante una cookie `JSESSIONID`.

### Formas de Trabajar con Sesiones en Spring MVC

#### 1. Usando HttpSession Directamente

La forma más directa es inyectar `HttpSession` como parámetro en los métodos del controller:

```java
@Controller
@RequestMapping("/carrito")
public class CarritoController {
    
    @GetMapping("/agregar/{id}")
    public String agregarProducto(
        @PathVariable Long id,
        HttpSession session
    ) {
        // Obtener carrito de la sesión (o crear uno nuevo)
        List<Producto> carrito = (List<Producto>) session.getAttribute("carrito");
        
        if (carrito == null) {
            carrito = new ArrayList<>();
            session.setAttribute("carrito", carrito);
        }
        
        // Agregar producto al carrito
        Producto producto = productoServicio.findById(id);
        carrito.add(producto);
        
        // Actualizar la sesión
        session.setAttribute("carrito", carrito);
        
        return "redirect:/productos";
    }
    
    @GetMapping("/ver")
    public String verCarrito(HttpSession session, Model model) {
        List<Producto> carrito = (List<Producto>) session.getAttribute("carrito");
        
        if (carrito == null) {
            carrito = new ArrayList<>();
        }
        
        model.addAttribute("carrito", carrito);
        model.addAttribute("total", calcularTotal(carrito));
        return "carrito/ver";
    }
    
    @GetMapping("/vaciar")
    public String vaciarCarrito(HttpSession session) {
        session.removeAttribute("carrito");
        return "redirect:/carrito/ver";
    }
}
```

#### 2. Usando @SessionAttribute

La anotación `@SessionAttribute` permite acceder directamente a atributos de sesión:

```java
@Controller
@RequestMapping("/carrito")
public class CarritoController {
    
    @GetMapping("/ver")
    public String verCarrito(
        @SessionAttribute(name = "carrito", required = false) List<Producto> carrito,
        Model model
    ) {
        if (carrito == null) {
            carrito = new ArrayList<>();
        }
        
        model.addAttribute("carrito", carrito);
        model.addAttribute("total", calcularTotal(carrito));
        return "carrito/ver";
    }
    
    @GetMapping("/cantidad")
    @ResponseBody
    public int cantidadItems(
        @SessionAttribute(name = "carrito", required = false) List<Producto> carrito
    ) {
        return carrito != null ? carrito.size() : 0;
    }
}
```

**Importante:** `@SessionAttribute` es para **LEER** de la sesión, no para escribir. Para escribir, usa `HttpSession`.

#### 3. Usando @SessionAttributes (A Nivel de Clase)

Para gestionar objetos de sesión automáticamente en formularios multi-paso:

```java
@Controller
@RequestMapping("/registro")
@SessionAttributes("registroForm") // Mantiene este objeto en sesión
public class RegistroController {
    
    @GetMapping("/paso1")
    public String paso1(Model model) {
        RegistroForm form = new RegistroForm();
        model.addAttribute("registroForm", form);
        return "registro/paso1";
    }
    
    @PostMapping("/paso1")
    public String procesarPaso1(@ModelAttribute("registroForm") RegistroForm form) {
        // El form se guarda automáticamente en sesión
        return "redirect:/registro/paso2";
    }
    
    @GetMapping("/paso2")
    public String paso2(@ModelAttribute("registroForm") RegistroForm form, Model model) {
        // El form se recupera automáticamente de sesión
        model.addAttribute("registroForm", form);
        return "registro/paso2";
    }
    
    @PostMapping("/paso2")
    public String procesarPaso2(
        @ModelAttribute("registroForm") RegistroForm form,
        SessionStatus status
    ) {
        // Procesar registro completo
        usuarioServicio.registrar(form);
        
        // Limpiar sesión al finalizar
        status.setComplete();
        
        return "redirect:/login";
    }
}
```

### Pasar Datos de Sesión a las Vistas

#### Método 1: Añadir al Model Explícitamente

```java
@GetMapping("/productos")
public String listar(HttpSession session, Model model) {
    // Obtener datos de sesión
    List<Producto> carrito = (List<Producto>) session.getAttribute("carrito");
    String idioma = (String) session.getAttribute("idioma");
    
    // Pasar a la vista
    model.addAttribute("carritoItems", carrito != null ? carrito.size() : 0);
    model.addAttribute("idiomaActual", idioma);
    
    // Datos normales
    model.addAttribute("productos", productoServicio.findAll());
    return "productos/lista";
}
```

#### Método 2: Usar @ModelAttribute Global

```java
@ControllerAdvice
public class GlobalControllerAdvice {
    
    @ModelAttribute("carritoCount")
    public int carritoCount(HttpSession session) {
        List<Producto> carrito = (List<Producto>) session.getAttribute("carrito");
        return carrito != null ? carrito.size() : 0;
    }
    
    @ModelAttribute("usuarioNombre")
    public String usuarioNombre(Principal principal) {
        return principal != null ? principal.getName() : "Invitado";
    }
}
```

Con esto, `carritoCount` y `usuarioNombre` estarán disponibles en **todas** las vistas automáticamente.

### Ejemplo Práctico Completo: Carrito de Compras

#### Controller del Carrito

```java
@Controller
@RequestMapping("/carrito")
public class CarritoController {
    
    @Autowired
    private ProductoServicio productoServicio;
    
    // Agregar producto al carrito
    @GetMapping("/agregar/{id}")
    public String agregarProducto(
        @PathVariable Long id,
        HttpSession session,
        RedirectAttributes redirectAttributes
    ) {
        // Obtener o crear carrito
        Map<Long, ItemCarrito> carrito = obtenerCarrito(session);
        
        // Buscar producto
        Producto producto = productoServicio.findById(id);
        
        if (producto == null) {
            redirectAttributes.addFlashAttribute("error", "Producto no encontrado");
            return "redirect:/productos";
        }
        
        // Agregar o incrementar cantidad
        if (carrito.containsKey(id)) {
            ItemCarrito item = carrito.get(id);
            item.setCantidad(item.getCantidad() + 1);
        } else {
            carrito.put(id, new ItemCarrito(producto, 1));
        }
        
        // Actualizar sesión
        session.setAttribute("carrito", carrito);
        
        redirectAttributes.addFlashAttribute("mensaje", 
            "Producto agregado al carrito");
        return "redirect:/productos";
    }
    
    // Ver carrito
    @GetMapping("/ver")
    public String verCarrito(HttpSession session, Model model) {
        Map<Long, ItemCarrito> carrito = obtenerCarrito(session);
        
        List<ItemCarrito> items = new ArrayList<>(carrito.values());
        double total = items.stream()
            .mapToDouble(item -> item.getProducto().getPrecio() * item.getCantidad())
            .sum();
        
        model.addAttribute("items", items);
        model.addAttribute("total", total);
        model.addAttribute("cantidadItems", items.size());
        
        return "carrito/ver";
    }
    
    // Actualizar cantidad
    @PostMapping("/actualizar/{id}")
    public String actualizarCantidad(
        @PathVariable Long id,
        @RequestParam int cantidad,
        HttpSession session,
        RedirectAttributes redirectAttributes
    ) {
        Map<Long, ItemCarrito> carrito = obtenerCarrito(session);
        
        if (cantidad <= 0) {
            carrito.remove(id);
            redirectAttributes.addFlashAttribute("mensaje", "Producto eliminado");
        } else if (carrito.containsKey(id)) {
            carrito.get(id).setCantidad(cantidad);
            redirectAttributes.addFlashAttribute("mensaje", "Cantidad actualizada");
        }
        
        session.setAttribute("carrito", carrito);
        return "redirect:/carrito/ver";
    }
    
    // Eliminar del carrito
    @GetMapping("/eliminar/{id}")
    public String eliminarProducto(
        @PathVariable Long id,
        HttpSession session,
        RedirectAttributes redirectAttributes
    ) {
        Map<Long, ItemCarrito> carrito = obtenerCarrito(session);
        carrito.remove(id);
        session.setAttribute("carrito", carrito);
        
        redirectAttributes.addFlashAttribute("mensaje", "Producto eliminado del carrito");
        return "redirect:/carrito/ver";
    }
    
    // Vaciar carrito
    @GetMapping("/vaciar")
    public String vaciarCarrito(
        HttpSession session,
        RedirectAttributes redirectAttributes
    ) {
        session.removeAttribute("carrito");
        redirectAttributes.addFlashAttribute("mensaje", "Carrito vaciado");
        return "redirect:/carrito/ver";
    }
    
    // Método auxiliar para obtener carrito
    @SuppressWarnings("unchecked")
    private Map<Long, ItemCarrito> obtenerCarrito(HttpSession session) {
        Map<Long, ItemCarrito> carrito = 
            (Map<Long, ItemCarrito>) session.getAttribute("carrito");
        
        if (carrito == null) {
            carrito = new HashMap<>();
            session.setAttribute("carrito", carrito);
        }
        
        return carrito;
    }
}
```

#### Clase ItemCarrito

```java
public class ItemCarrito {
    private Producto producto;
    private int cantidad;
    
    public ItemCarrito() {}
    
    public ItemCarrito(Producto producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
    }
    
    public double getSubtotal() {
        return producto.getPrecio() * cantidad;
    }
    
    // Getters y setters
}
```

#### ControllerAdvice para Mostrar Contador en Todas las Páginas

```java
@ControllerAdvice
public class CarritoAdvice {
    
    @ModelAttribute("carritoCount")
    public int carritoCount(HttpSession session) {
        @SuppressWarnings("unchecked")
        Map<Long, ItemCarrito> carrito = 
            (Map<Long, ItemCarrito>) session.getAttribute("carrito");
        
        if (carrito == null) {
            return 0;
        }
        
        // Retornar suma de cantidades
        return carrito.values().stream()
            .mapToInt(ItemCarrito::getCantidad)
            .sum();
    }
}
```

#### Vista: Mostrar Contador en Navbar (Pebble)

```pebble
{# templates/fragments/navbar.peb #}
<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <div class="container-fluid">
        <a class="navbar-brand" href="/">WalaSpringBoot</a>
        
        <ul class="navbar-nav ms-auto">
            <li class="nav-item">
                <a class="nav-link" href="/productos">
                    <i class="bi bi-shop"></i> Productos
                </a>
            </li>
            
            {# Contador de carrito desde sesión #}
            <li class="nav-item">
                <a class="nav-link" href="/carrito/ver">
                    <i class="bi bi-cart"></i> Carrito
                    {% if carritoCount > 0 %}
                    <span class="badge bg-danger">{{ carritoCount }}</span>
                    {% endif %}
                </a>
            </li>
            
            {% if usuario %}
            <li class="nav-item">
                <a class="nav-link" href="/perfil">
                    <i class="bi bi-person"></i> {{ usuario.nombre }}
                </a>
            </li>
            {% else %}
            <li class="nav-item">
                <a class="nav-link" href="/auth/login">
                    <i class="bi bi-box-arrow-in-right"></i> Login
                </a>
            </li>
            {% endif %}
        </ul>
    </div>
</nav>
```

#### Vista: Página del Carrito

```pebble
{# templates/carrito/ver.peb #}
{% extends "layouts/base.peb" %}

{% block title %}Mi Carrito{% endblock %}

{% block content %}
<div class="container mt-4">
    <h1><i class="bi bi-cart"></i> Mi Carrito</h1>
    
    {% if items is empty %}
    <div class="alert alert-info">
        <i class="bi bi-info-circle"></i>
        Tu carrito está vacío. <a href="/productos">Ver productos</a>
    </div>
    {% else %}
    
    <div class="table-responsive">
        <table class="table">
            <thead>
                <tr>
                    <th>Producto</th>
                    <th>Precio</th>
                    <th>Cantidad</th>
                    <th>Subtotal</th>
                    <th>Acciones</th>
                </tr>
            </thead>
            <tbody>
                {% for item in items %}
                <tr>
                    <td>
                        <img src="{{ item.producto.imagen }}" 
                             width="50" class="me-2">
                        {{ item.producto.nombre }}
                    </td>
                    <td>{{ item.producto.precio | numberformat('0.00') }}€</td>
                    <td>
                        <form method="POST" 
                              action="/carrito/actualizar/{{ item.producto.id }}" 
                              class="d-inline">
                            <input type="number" name="cantidad" 
                                   value="{{ item.cantidad }}" 
                                   min="1" max="99" 
                                   class="form-control form-control-sm" 
                                   style="width: 70px;">
                            <button type="submit" class="btn btn-sm btn-primary mt-1">
                                <i class="bi bi-arrow-repeat"></i>
                            </button>
                        </form>
                    </td>
                    <td>{{ item.subtotal | numberformat('0.00') }}€</td>
                    <td>
                        <a href="/carrito/eliminar/{{ item.producto.id }}" 
                           class="btn btn-sm btn-danger">
                            <i class="bi bi-trash"></i>
                        </a>
                    </td>
                </tr>
                {% endfor %}
            </tbody>
            <tfoot>
                <tr>
                    <td colspan="3" class="text-end"><strong>Total:</strong></td>
                    <td colspan="2">
                        <strong>{{ total | numberformat('0.00') }}€</strong>
                    </td>
                </tr>
            </tfoot>
        </table>
    </div>
    
    <div class="d-flex justify-content-between mt-3">
        <a href="/productos" class="btn btn-secondary">
            <i class="bi bi-arrow-left"></i> Seguir Comprando
        </a>
        <div>
            <a href="/carrito/vaciar" class="btn btn-warning me-2">
                <i class="bi bi-trash"></i> Vaciar Carrito
            </a>
            <a href="/compra/finalizar" class="btn btn-success">
                <i class="bi bi-check-circle"></i> Finalizar Compra
            </a>
        </div>
    </div>
    
    {% endif %}
</div>
{% endblock %}
```

### Ciclo de Vida de las Sesiones

#### Creación de Sesión

```java
// Spring crea la sesión automáticamente al primer setAttribute
HttpSession session = request.getSession(); // Obtiene o crea
session.setAttribute("usuario", usuario);
```

#### Timeout de Sesión

**En application.properties:**

```properties
# Timeout de sesión en segundos (30 minutos = 1800 segundos)
server.servlet.session.timeout=1800
```

**O en código:**

```java
@GetMapping("/login")
public String login(HttpSession session) {
    // Configurar timeout: 30 minutos
    session.setMaxInactiveInterval(30 * 60);
    return "login";
}
```

#### Invalidar Sesión (Logout)

```java
@GetMapping("/logout")
public String logout(HttpSession session) {
    // Invalida la sesión completamente
    session.invalidate();
    return "redirect:/";
}
```

#### Renovar ID de Sesión (Seguridad)

```java
@PostMapping("/login")
public String procesarLogin(
    @ModelAttribute LoginForm form,
    HttpServletRequest request
) {
    // Autenticar usuario...
    
    // Cambiar ID de sesión para prevenir session fixation
    HttpSession oldSession = request.getSession(false);
    if (oldSession != null) {
        oldSession.invalidate();
    }
    
    HttpSession newSession = request.getSession(true);
    newSession.setAttribute("usuario", usuario);
    
    return "redirect:/dashboard";
}
```

### Mejores Prácticas con Sesiones

#### 1. Minimizar Datos en Sesión

❌ **Mal:**
```java
// Guardar objetos grandes en sesión
session.setAttribute("todosLosProductos", productoServicio.findAll());
```

✅ **Bien:**
```java
// Solo guardar IDs, recuperar cuando sea necesario
session.setAttribute("productosSeleccionados", listaDeIds);
```

#### 2. Usar Nombres Descriptivos

❌ **Mal:**
```java
session.setAttribute("data", carrito);
session.setAttribute("u", usuario);
```

✅ **Bien:**
```java
session.setAttribute("carrito", carrito);
session.setAttribute("usuarioActual", usuario);
```

#### 3. Verificar Existencia

✅ **Bien:**
```java
@GetMapping("/checkout")
public String checkout(HttpSession session, Model model) {
    List<Producto> carrito = (List<Producto>) session.getAttribute("carrito");
    
    if (carrito == null || carrito.isEmpty()) {
        return "redirect:/carrito/ver?error=vacio";
    }
    
    // Continuar con checkout...
    return "checkout";
}
```

#### 4. Limpiar Sesión Cuando ya no se Necesite

```java
@PostMapping("/compra/finalizar")
public String finalizarCompra(HttpSession session) {
    List<Producto> carrito = (List<Producto>) session.getAttribute("carrito");
    
    // Procesar compra...
    compraServicio.procesar(carrito);
    
    // Limpiar carrito de sesión
    session.removeAttribute("carrito");
    
    return "redirect:/compras/confirmacion";
}
```

### Sesiones en Entornos de Producción

Para aplicaciones con múltiples servidores (load balancing), considera:

#### Sticky Sessions
- El load balancer dirige al usuario siempre al mismo servidor
- Configuración en el balanceador de carga

#### Sesiones Compartidas (Redis/Hazelcast)

```kotlin
// build.gradle.kts
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.session:spring-session-data-redis")
}
```

```properties
# application.properties
spring.session.store-type=redis
spring.redis.host=localhost
spring.redis.port=6379
```

Con esto, las sesiones se almacenan en Redis y están disponibles para todos los servidores.

## Modelos y Vista

### Creando Modelos (Entities)

```java
@Entity
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @NotEmpty(message = "El nombre es obligatorio")
    private String nombre;
    
    @Min(value = 0, message = "El precio debe ser positivo")
    private float precio;
    
    @Column(length = 1000)
    private String descripcion;
    
    private String categoria;
    
    @ManyToOne
    private Usuario propietario;
    
    // Getters y setters
}
```

### Repositories

```java
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByNombreContainsIgnoreCase(String nombre);
    List<Producto> findByCategoria(String categoria);
    List<Producto> findByPrecioBetween(float min, float max);
}
```

### Services

```java
@Service
public class ProductoServicio {
    
    @Autowired
    private ProductoRepository repositorio;
    
    @Cacheable("productos")
    public List<Producto> findAll() {
        return repositorio.findAll();
    }
    
    @Cacheable(value = "productos", key = "#id")
    public Producto findById(Long id) {
        return repositorio.findById(id).orElse(null);
    }
    
    @CacheEvict(value = "productos", allEntries = true)
    public Producto guardar(Producto producto) {
        return repositorio.save(producto);
    }
}
```

## Manejo de Formularios

### Formulario de Creación

**Controller:**

```java
@Controller
@RequestMapping("/productos")
public class ProductoController {
    
    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("producto", new Producto());
        return "productos/form";
    }
    
    @PostMapping("/guardar")
    public String guardar(
        @Valid @ModelAttribute Producto producto,
        BindingResult result,
        RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            return "productos/form";
        }
        
        productoServicio.guardar(producto);
        redirectAttributes.addFlashAttribute("mensaje", "Producto guardado correctamente");
        return "redirect:/productos";
    }
}
```

**Vista (Pebble):**

```html
<form method="POST" action="/productos/guardar">
    <div class="mb-3">
        <label for="nombre" class="form-label">Nombre</label>
        <input type="text" class="form-control" id="nombre" name="nombre" 
               value="{{ producto.nombre }}" required>
    </div>
    
    <div class="mb-3">
        <label for="precio" class="form-label">Precio</label>
        <input type="number" step="0.01" class="form-control" id="precio" 
               name="precio" value="{{ producto.precio }}" required>
    </div>
    
    <div class="mb-3">
        <label for="descripcion" class="form-label">Descripción</label>
        <textarea class="form-control" id="descripcion" name="descripcion" 
                  rows="3">{{ producto.descripcion }}</textarea>
    </div>
    
    <button type="submit" class="btn btn-primary">Guardar</button>
</form>
```

### Upload de Archivos

**Controller:**

```java
@PostMapping("/producto/crear")
public String crear(
    @ModelAttribute Producto producto,
    @RequestParam("file") MultipartFile file
) {
    if (!file.isEmpty()) {
        String filename = storageService.store(file);
        producto.setImagen(filename);
    }
    
    productoServicio.guardar(producto);
    return "redirect:/productos";
}
```

**Vista:**

```html
<form method="POST" action="/producto/crear" enctype="multipart/form-data">
    <div class="mb-3">
        <label for="file" class="form-label">Imagen</label>
        <input type="file" class="form-control" id="file" name="file" 
               accept="image/*" required>
    </div>
    <button type="submit" class="btn btn-primary">Crear</button>
</form>
```

## Validación

### Bean Validation

```java
@Entity
public class Usuario {
    @NotEmpty(message = "{usuario.nombre.vacio}")
    private String nombre;
    
    @Email(message = "{usuario.email.invalido}")
    @NotEmpty
    private String email;
    
    @Size(min = 6, message = "{usuario.password.corta}")
    private String password;
    
    @Min(value = 18, message = "{usuario.edad.minima}")
    private int edad;
}
```

### Custom Validator

**Anotación:**

```java
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidImageValidator.class)
public @interface ValidImage {
    String message() default "Formato de imagen inválido";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
```

**Validator:**

```java
public class ValidImageValidator implements ConstraintValidator<ValidImage, MultipartFile> {
    
    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            return true;
        }
        
        String contentType = file.getContentType();
        return contentType != null && 
               (contentType.equals("image/jpeg") || 
                contentType.equals("image/png") ||
                contentType.equals("image/gif"));
    }
}
```

### Mostrar Errores en la Vista

```html
{% if errors %}
<div class="alert alert-danger">
    <ul>
        {% for error in errors %}
        <li>{{ error }}</li>
        {% endfor %}
    </ul>
</div>
{% endif %}
```

## Seguridad

### Configuración de Spring Security

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SeguridadConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/public/**", "/css/**", "/images/**").permitAll()
                .requestMatchers("/admin/**").hasAuthority("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/auth/login")
                .defaultSuccessUrl("/")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("/")
                .permitAll()
            );
        
        return http.build();
    }
}
```

### Method-Level Security

```java
@Controller
@RequestMapping("/admin")
public class AdminController {
    
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Solo accesible para ADMIN
        return "admin/dashboard";
    }
    
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MODERATOR')")
    @GetMapping("/usuarios")
    public String usuarios(Model model) {
        // Accesible para ADMIN y MODERATOR
        return "admin/usuarios";
    }
}
```

### Obtener Usuario Autenticado

```java
@GetMapping("/perfil")
public String perfil(Principal principal, Model model) {
    String email = principal.getName();
    Usuario usuario = usuarioServicio.buscarPorEmail(email);
    model.addAttribute("usuario", usuario);
    return "perfil";
}
```

## Internacionalización

### Configuración i18n

```java
@Configuration
public class ConfiguracionI18n implements WebMvcConfigurer {
    
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        slr.setDefaultLocale(new Locale("es"));
        return slr;
    }
    
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang");
        return lci;
    }
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }
}
```

### Archivos de Mensajes

**messages_es.properties:**

```properties
producto.nombre=Nombre del Producto
producto.precio=Precio
producto.crear=Crear Producto
usuario.nombre=Nombre de Usuario
```

**messages_en.properties:**

```properties
producto.nombre=Product Name
producto.precio=Price
producto.crear=Create Product
usuario.nombre=Username
```

### Uso en Plantillas

```html
<h1>{{ i18n('producto.crear') }}</h1>

<label>{{ i18n('producto.nombre') }}</label>
<input type="text" name="nombre">

<label>{{ i18n('producto.precio') }}</label>
<input type="number" name="precio">
```

### Cambio de Idioma

```html
<a href="?lang=es">Español</a>
<a href="?lang=en">English</a>
```

## Mejores Prácticas

### 1. Separación de Responsabilidades

❌ **Mal:**

```java
@Controller
public class ProductoController {
    @Autowired
    private ProductoRepository repositorio;
    
    @GetMapping("/productos")
    public String lista(Model model) {
        model.addAttribute("productos", repositorio.findAll());
        return "productos/lista";
    }
}
```

✅ **Bien:**

```java
@Controller
public class ProductoController {
    @Autowired
    private ProductoServicio servicio;
    
    @GetMapping("/productos")
    public String lista(Model model) {
        model.addAttribute("productos", servicio.findAll());
        return "productos/lista";
    }
}
```

### 2. Manejo de Errores

```java
@ExceptionHandler(ProductoNotFoundException.class)
public String handleProductoNotFound(ProductoNotFoundException ex, Model model) {
    model.addAttribute("error", ex.getMessage());
    return "error/404";
}

@ExceptionHandler(Exception.class)
public String handleGenericError(Exception ex, Model model) {
    model.addAttribute("error", "Error interno del servidor");
    return "error/500";
}
```

### 3. Redirect After Post (PRG Pattern)

```java
@PostMapping("/producto/crear")
public String crear(@ModelAttribute Producto producto, RedirectAttributes redirectAttributes) {
    productoServicio.guardar(producto);
    redirectAttributes.addFlashAttribute("mensaje", "Producto creado correctamente");
    return "redirect:/productos"; // Evita reenvío del formulario
}
```

### 4. DTOs para Formularios

```java
public class ProductoFormDTO {
    @NotEmpty
    private String nombre;
    
    @Min(0)
    private float precio;
    
    @ValidImage
    private MultipartFile imagen;
    
    // Getters y setters
    
    public Producto toEntity() {
        Producto producto = new Producto();
        producto.setNombre(this.nombre);
        producto.setPrecio(this.precio);
        return producto;
    }
}
```

### 5. Cache Inteligente

```java
@Service
public class ProductoServicio {
    
    @Cacheable("productos")
    public List<Producto> findAll() {
        return repositorio.findAll();
    }
    
    @CacheEvict(value = "productos", allEntries = true)
    public Producto guardar(Producto producto) {
        return repositorio.save(producto);
    }
}
```

### 6. Logging

```java
@Controller
public class ProductoController {
    private static final Logger logger = LoggerFactory.getLogger(ProductoController.class);
    
    @GetMapping("/productos")
    public String lista(Model model) {
        logger.info("Listando productos");
        List<Producto> productos = productoServicio.findAll();
        logger.debug("Encontrados {} productos", productos.size());
        model.addAttribute("productos", productos);
        return "productos/lista";
    }
}
```

## Recursos Adicionales

- [Spring MVC Documentation](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html)
- [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Pebble Template Engine](https://pebbletemplates.io/)

---

**Última actualización:** Enero 2025  
**Autor:** José Luis González Sánchez

## Casos de Uso Avanzados del Proyecto

### 1. GlobalControllerAdvice - Variables Globales

```java
@ControllerAdvice
public class GlobalControllerAdvice {
    
    @Autowired
    private MessageSource messageSource;
    
    // Inyectar información de autenticación en todas las vistas
    @ModelAttribute("isAuthenticated")
    public boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName());
    }
    
    @ModelAttribute("currentUser")
    public User getCurrentUser() {
        if (isAuthenticated()) {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            return userService.buscarPorEmail(email);
        }
        return null;
    }
    
    @ModelAttribute("isAdmin")
    public boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN"));
    }
    
    // Función de mensajes disponible en todas las vistas
    @ModelAttribute("message")
    public Function<String, String> messageFunction() {
        return key -> messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }
}
```

### 2. ModelAttribute - Datos Compartidos en Controller

```java
@Controller
@RequestMapping("/app")
public class PurchaseController {
    
    @Autowired
    HttpSession session;
    
    @Autowired
    ProductService productoServicio;
    
    // Este método se ejecuta antes de CADA petición del controller
    @ModelAttribute("carrito")
    public List<Product> productosCarrito() {
        List<Long> contenido = (List<Long>) session.getAttribute("carrito");
        return (contenido == null) ? null : productoServicio.variosPorId(contenido);
    }
    
    // Cálculo automático del total
    @ModelAttribute("total_carrito")
    public Double totalCarrito() {
        List<Product> productosCarrito = productosCarrito();
        if (productosCarrito != null) {
            return productosCarrito.stream()
                    .mapToDouble(Product::getPrecio)
                    .sum();
        }
        return 0.0;
    }
    
    // Ahora TODAS las vistas del controller tienen acceso a "carrito" y "total_carrito"
    @GetMapping("/carrito")
    public String verCarrito(Model model) {
        // No necesitamos añadir nada al modelo
        return "app/compra/carrito";
    }
}
```

### 3. Email Asíncrono - Evitar Bloqueos

```java
@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    public void enviarEmailConfirmacionCompra(Purchase compra) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(compra.getPropietario().getEmail());
            helper.setSubject("Confirmación de Compra");
            
            String htmlContent = construirEmailHTML(compra);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
        } catch (Exception e) {
            logger.error("Error al enviar email: " + e.getMessage());
        }
    }
    
    private String construirEmailHTML(Purchase compra) {
        // Construir HTML con estilo inline
        StringBuilder html = new StringBuilder();
        html.append("<html><body style='font-family: Arial;'>");
        html.append("<div style='background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);'>");
        html.append("<h1 style='color: white;'>Confirmación de Compra</h1>");
        html.append("</div>");
        // ... más HTML
        return html.toString();
    }
}

// En el controller - Envío asíncrono
@GetMapping("/carrito/finalizar")
public String checkout() {
    Purchase compra = compraServicio.insertar(new Purchase(), usuario);
    productos.forEach(p -> compraServicio.addProductoCompra(p, compra));
    
    // Enviar email en un thread separado para no bloquear
    new Thread(() -> {
        try {
            emailService.enviarEmailConfirmacionCompra(compra);
        } catch (Exception e) {
            // Log pero no fallar la compra
            logger.error("Error enviando email: " + e.getMessage());
        }
    }).start();
    
    return "redirect:/app/miscompras/factura/" + compra.id;
}
```

### 4. Gestión de Archivos - Upload y Storage

```java
@Service
public class FileSystemStorageService implements StorageService {
    
    private final Path rootLocation;
    
    public String store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Archivo vacío");
            }
            
            String filename = StringUtils.cleanPath(file.getOriginalFilename());
            String extension = filename.substring(filename.lastIndexOf("."));
            String storedFilename = UUID.randomUUID().toString() + extension;
            
            Path destinationFile = this.rootLocation.resolve(storedFilename);
            Files.copy(file.getInputStream(), destinationFile);
            
            return storedFilename;
        } catch (IOException e) {
            throw new StorageException("Error al guardar archivo", e);
        }
    }
    
    public void delete(String filename) {
        try {
            Path file = rootLocation.resolve(filename);
            Files.deleteIfExists(file);
        } catch (IOException e) {
            logger.error("Error al eliminar archivo: " + filename);
        }
    }
}

// En el controller
@PostMapping("/misproducto/nuevo/submit")
public String nuevoProductoSubmit(
        @Valid @ModelAttribute Product producto,
        @RequestParam("file") MultipartFile file,
        BindingResult bindingResult) {
    
    if (bindingResult.hasErrors()) {
        return "app/producto/ficha";
    }
    
    if (!file.isEmpty()) {
        String imagen = storageService.store(file);
        producto.setImagen(MvcUriComponentsBuilder
            .fromMethodName(FilesController.class, "serveFile", imagen)
            .build().toUriString());
    }
    
    producto.setPropietario(usuario);
    productoServicio.insertar(producto);
    return "redirect:/app/misproductos";
}
```

### 5. Generación de PDF al Vuelo

```java
@Controller
@RequestMapping("/app")
public class PurchaseController {
    
    @Autowired
    Html2PdfService documentGeneratorService;
    
    @RequestMapping(
        value = "/miscompras/factura/{id}/pdf",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_PDF_VALUE
    )
    public ResponseEntity<InputStreamResource> facturaPDF(@PathVariable Long id) {
        Purchase compra = compraServicio.buscarPorId(id);
        List<Product> productos = productoServicio.productosDeUnaCompra(compra);
        Double total = productos.stream().mapToDouble(Product::getPrecio).sum();
        
        ByteArrayInputStream bis = GeneradorPDF.factura2PDF(compra, productos, total);
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=factura_" + compra.getId() + ".pdf");
        
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }
}
```

### 6. API REST con AJAX - Favoritos y Ratings

```java
@Controller
@RequestMapping("/app")
public class FavoriteController {
    
    @Autowired
    private FavoriteService favoriteService;
    
    // Endpoint REST que retorna JSON
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
}

// JavaScript en la vista para consumir la API
/*
function toggleFavorite(productoId) {
    const isFavorite = document.getElementById('favoriteIcon').classList.contains('bi-heart-fill');
    const method = isFavorite ? 'DELETE' : 'POST';
    const url = isFavorite ? `/app/favoritos/remove/${productoId}` : `/app/favoritos/add/${productoId}`;
    
    fetch(url, {
        method: method,
        headers: {
            'Content-Type': 'application/json',
        }
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            // Actualizar UI
        }
    });
}
*/
```

### 7. Custom Error Controller

```java
@Controller
public class CustomErrorController implements ErrorController {
    
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                model.addAttribute("errorCode", "404");
                model.addAttribute("errorMessage", "Página no encontrada");
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                model.addAttribute("errorCode", "403");
                model.addAttribute("errorMessage", "Acceso denegado");
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                model.addAttribute("errorCode", "500");
                model.addAttribute("errorMessage", "Error interno del servidor");
            }
        }
        
        return "error";
    }
}
```

### 8. Validaciones Personalizadas

```java
// Anotación personalizada
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidImageValidator.class)
public @interface ValidImage {
    String message() default "Imagen no válida";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

// Validador
public class ValidImageValidator implements ConstraintValidator<ValidImage, MultipartFile> {
    
    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            return true; // Permitir vacío, usar @NotNull por separado
        }
        
        String contentType = file.getContentType();
        return contentType != null && 
               (contentType.equals("image/jpeg") || 
                contentType.equals("image/png") || 
                contentType.equals("image/gif"));
    }
}

// Uso en el modelo
@Entity
public class Product {
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String nombre;
    
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor que 0")
    private Float precio;
    
    @ValidImage(message = "Solo se permiten imágenes JPG, PNG o GIF")
    private MultipartFile imagen;
}
```

### 9. Manejo de Sesiones

```java
@Controller
@RequestMapping("/app")
public class PurchaseController {
    
    @Autowired
    HttpSession session;
    
    // Añadir al carrito usando sesión
    @GetMapping("/carrito/add/{id}")
    public String addCarrito(@PathVariable Long id) {
        List<Long> contenido = (List<Long>) session.getAttribute("carrito");
        
        if (contenido == null) {
            contenido = new ArrayList<>();
        }
        
        if (!contenido.contains(id)) {
            contenido.add(id);
        }
        
        session.setAttribute("carrito", contenido);
        session.setAttribute("items_carrito", contenido.size());
        
        return "redirect:/app/carrito";
    }
    
    // Limpiar carrito
    @GetMapping("/carrito/finalizar")
    public String checkout() {
        // Procesar compra...
        
        // Limpiar sesión
        session.removeAttribute("carrito");
        session.removeAttribute("items_carrito");
        
        return "redirect:/app/miscompras/factura/" + compra.getId();
    }
}
```

### 10. Filtros y Búsqueda Avanzada

```java
@Controller
@RequestMapping("/public")
public class ZonaPublicaController {
    
    @GetMapping({"", "/", "/index"})
    public String index(
            Model model,
            @RequestParam(name = "q", required = false) String query,
            @RequestParam(name = "categoria", required = false) String categoria,
            @RequestParam(name = "minPrecio", required = false) Float minPrecio,
            @RequestParam(name = "maxPrecio", required = false) Float maxPrecio) {
        
        List<Product> productos = productoServicio.productosSinVender();
        
        // Aplicar filtro de búsqueda
        if (query != null && !query.trim().isEmpty()) {
            productos = productoServicio.buscar(query);
        }
        
        // Filtrar por categoría
        if (categoria != null && !categoria.trim().isEmpty()) {
            productos = productos.stream()
                    .filter(p -> categoria.equals(p.getCategoria()))
                    .toList();
        }
        
        // Filtrar por rango de precios
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
        model.addAttribute("categoria", categoria);
        model.addAttribute("minPrecio", minPrecio);
        model.addAttribute("maxPrecio", maxPrecio);
        
        return "index";
    }
}
```

## Patrones de Diseño Implementados

### 1. Repository Pattern
```java
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCompraIsNull();
    List<Product> findByCompra(Purchase compra);
    List<Product> findByPropietario(User propietario);
}
```

### 2. Service Layer Pattern
```java
@Service
public class ProductService {
    @Autowired
    private ProductRepository repositorio;
    
    @Cacheable("productos")
    public List<Product> findAll() {
        return repositorio.findAll();
    }
    
    @CacheEvict(value = "productos", allEntries = true)
    public Product insertar(Product producto) {
        return repositorio.save(producto);
    }
}
```

### 3. DTO Pattern (Data Transfer Object)
```java
public class ProductoDTO {
    private Long id;
    private String nombre;
    private Double precio;
    private String imagen;
    private Double averageRating;
    private Long ratingCount;
    private boolean isFavorite;
    
    // Constructor desde entidad
    public ProductoDTO(Product producto, Double rating, Long count, boolean fav) {
        this.id = producto.getId();
        this.nombre = producto.getNombre();
        this.precio = producto.getPrecio();
        this.averageRating = rating;
        this.ratingCount = count;
        this.isFavorite = fav;
    }
}
```

## CSRF Protection Patterns

La protección CSRF (Cross-Site Request Forgery) es fundamental en aplicaciones web. Spring Security la habilita por defecto.

### Patrón LoginController (Funcional)

Este patrón añade manualmente el token CSRF al modelo en cada petición GET:

```java
@Controller
@RequestMapping("/auth")
public class LoginController {
    
    @GetMapping("/login")
    public String login(Model model, HttpServletRequest request) {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrfToken != null) {
            model.addAttribute("csrfToken", csrfToken.getToken());
            model.addAttribute("csrfParamName", csrfToken.getParameterName());
        }
        return "login";
    }
}
```

**Uso en template:**
```html
<form method="post" action="/auth/login">
    <input type="hidden" name="{{ csrfParamName }}" value="{{ csrfToken }}"/>
    <input type="email" name="username" required/>
    <input type="password" name="password" required/>
    <button type="submit">Login</button>
</form>
```

### Patrón GlobalControllerAdvice (Universal)

Este patrón es más elegante y DRY - añade el token CSRF automáticamente a todas las vistas:

```java
@ControllerAdvice
public class GlobalControllerAdvice {
    
    @ModelAttribute
    public void addCsrfToModel(Model model, HttpServletRequest request) {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrfToken != null) {
            model.addAttribute("csrfToken", csrfToken.getToken());
            model.addAttribute("csrfParamName", csrfToken.getParameterName());
        }
    }
}
```

**Ventajas:**
- ✅ Centralizado - no necesitas añadirlo en cada controller
- ✅ DRY - evita duplicación de código
- ✅ Automático - todas las vistas tienen acceso al token

**Uso en templates (igual que antes):**
```html
<form method="post" action="/app/producto/nuevo">
    <input type="hidden" name="{{ csrfParamName }}" value="{{ csrfToken }}"/>
    <!-- campos del formulario -->
</form>
```

### CSRF en JavaScript/AJAX

Para peticiones AJAX, es recomendable usar meta tags en el `<head>`:

```html
<head>
    <meta name="_csrf" content="{{ csrfToken }}"/>
    <meta name="_csrf_header" content="{{ csrfParamName }}"/>
</head>
```

Luego en JavaScript:

```javascript
function getCsrfToken() {
    return document.querySelector('meta[name="_csrf"]').getAttribute('content');
}

function getCsrfHeader() {
    return document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
}

// Uso con fetch
fetch('/api/productos', {
    method: 'POST',
    headers: {
        'Content-Type': 'application/json',
        [getCsrfHeader()]: getCsrfToken()
    },
    body: JSON.stringify(data)
});

// Uso con FormData
const formData = new FormData();
formData.append('nombre', 'Producto');
formData.append(getCsrfHeader(), getCsrfToken());

fetch('/app/producto/nuevo', {
    method: 'POST',
    body: formData
});
```

## File Upload Security

La subida de archivos requiere validaciones de seguridad estrictas para prevenir ataques.

### Configuración Básica

```java
@Configuration
public class StorageConfig {
    
    @Bean
    public CommandLineRunner initStorage(StorageService storageService) {
        return (args) -> {
            storageService.init();
        };
    }
}
```

### FileSystemStorageService Seguro

```java
@Service
public class FileSystemStorageService implements StorageService {
    
    private final Path rootLocation;
    
    @Autowired
    public FileSystemStorageService(StorageProperties properties) {
        this.rootLocation = Paths.get(properties.getLocation());
    }
    
    @Override
    public String store(MultipartFile file) {
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = StringUtils.getFilenameExtension(filename);
        String justFilename = filename.replace("." + extension, "");
        
        // ⚠️ SEGURIDAD: Eliminar separadores de ruta del nombre
        justFilename = justFilename.replaceAll("[/\\\\]", "_");
        
        String storedFilename = System.currentTimeMillis() + "_" + justFilename + "." + extension;
        
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file " + filename);
            }
            
            // ⚠️ SEGURIDAD: Prevenir path traversal attacks
            if (filename.contains("..")) {
                throw new StorageException(
                    "Cannot store file with relative path outside current directory " + filename);
            }
            
            // ⚠️ SEGURIDAD: Validar que la ruta resuelta está dentro del directorio root
            Path destinationFile = this.rootLocation.resolve(storedFilename)
                .normalize()
                .toAbsolutePath();
            
            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath().normalize())) {
                throw new StorageException("Cannot store file outside current directory");
            }
            
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
                return storedFilename;
            }
        } catch (IOException e) {
            throw new StorageException("Failed to store file " + filename, e);
        }
    }
    
    @Override
    public Path load(String filename) {
        // ⚠️ SEGURIDAD: Validar que el nombre de archivo no contiene intentos de path traversal
        if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            throw new StorageException("Cannot load file with relative or absolute path: " + filename);
        }
        return rootLocation.resolve(filename);
    }
}
```

### Validaciones de Archivo

```java
@Controller
public class FileUploadController {
    
    @Autowired
    private StorageService storageService;
    
    // Tamaño máximo de archivo (Spring Boot)
    // application.properties:
    // spring.servlet.multipart.max-file-size=10MB
    // spring.servlet.multipart.max-request-size=10MB
    
    @PostMapping("/app/producto/imagen/upload")
    public ResponseEntity<?> handleFileUpload(@RequestParam("file") MultipartFile file) {
        
        // ⚠️ Validar que el archivo no está vacío
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "El archivo está vacío"
            ));
        }
        
        // ⚠️ Validar el tipo de archivo (extensión)
        String filename = file.getOriginalFilename();
        String extension = StringUtils.getFilenameExtension(filename);
        List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png", "gif");
        
        if (!allowedExtensions.contains(extension.toLowerCase())) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Tipo de archivo no permitido. Usa: " + String.join(", ", allowedExtensions)
            ));
        }
        
        // ⚠️ Validar el tipo MIME (más seguro que solo la extensión)
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "El archivo no es una imagen válida"
            ));
        }
        
        // ⚠️ Validar el tamaño del archivo (adicional a la config de Spring)
        long maxSize = 5 * 1024 * 1024; // 5MB
        if (file.getSize() > maxSize) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "El archivo es demasiado grande. Máximo: 5MB"
            ));
        }
        
        try {
            String storedFilename = storageService.store(file);
            String fileUrl = "/files/" + storedFilename;
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "filename", storedFilename,
                "url", fileUrl
            ));
        } catch (StorageException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error al guardar el archivo: " + e.getMessage()
            ));
        }
    }
}
```

## Error Handling Custom

Spring MVC permite personalizar el manejo de errores a nivel global.

### CustomErrorController

```java
@Controller
public class CustomErrorController implements ErrorController {
    
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            
            model.addAttribute("statusCode", statusCode);
            
            switch (statusCode) {
                case 404:
                    model.addAttribute("errorTitle", "Página no encontrada");
                    model.addAttribute("errorMessage", "La página que buscas no existe.");
                    break;
                case 403:
                    model.addAttribute("errorTitle", "Acceso denegado");
                    model.addAttribute("errorMessage", "No tienes permisos para acceder a esta página.");
                    break;
                case 500:
                    model.addAttribute("errorTitle", "Error del servidor");
                    model.addAttribute("errorMessage", "Algo salió mal. Estamos trabajando en ello.");
                    break;
                default:
                    model.addAttribute("errorTitle", "Error " + statusCode);
                    model.addAttribute("errorMessage", "Ha ocurrido un error inesperado.");
            }
        }
        
        return "error";
    }
}
```

### GlobalExceptionHandler

```java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(StorageException.class)
    public String handleStorageException(StorageException ex, Model model) {
        model.addAttribute("errorTitle", "Error de almacenamiento");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }
    
    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException ex) {
        return ResponseEntity.notFound().build();
    }
    
    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception ex, Model model) {
        model.addAttribute("errorTitle", "Error inesperado");
        model.addAttribute("errorMessage", "Ha ocurrido un error. Por favor, inténtalo de nuevo.");
        // En producción, no mostrar detalles del error:
        // model.addAttribute("errorDetails", ex.getMessage());
        return "error";
    }
}
```

### Template de Error (error.peb.html)

```html
<!DOCTYPE html>
<html lang="es">
{% include "fragments/head" %}

<body class="d-flex flex-column min-vh-100">
{% include "fragments/navbar" %}

<div class="container mt-5 pt-5">
    <div class="row justify-content-center">
        <div class="col-md-6 text-center">
            <h1 class="display-1">{{ statusCode | default('Error') }}</h1>
            <h2 class="mb-4">{{ errorTitle | default('Algo salió mal') }}</h2>
            <p class="lead">{{ errorMessage | default('Ha ocurrido un error inesperado.') }}</p>
            
            <div class="mt-4">
                <a href="/" class="btn btn-primary">
                    <i class="bi bi-house"></i> Volver al inicio
                </a>
                <a href="javascript:history.back()" class="btn btn-outline-secondary">
                    <i class="bi bi-arrow-left"></i> Volver atrás
                </a>
            </div>
        </div>
    </div>
</div>

{% include "fragments/footer" %}
</body>
</html>
```

## Marketplace Patterns

Patrones específicos implementados en la aplicación de marketplace WalaSpringBoot.

### Shopping Cart Session Pattern

```java
@Controller
@RequestMapping("/app")
public class PurchaseController {
    
    @Autowired
    HttpSession session;
    
    // Obtener productos del carrito desde la sesión
    @ModelAttribute("carrito")
    public List<Product> productosCarrito() {
        List<Long> contenido = (List<Long>) session.getAttribute("carrito");
        return (contenido == null) ? null : productoServicio.variosPorId(contenido);
    }
    
    // Calcular total del carrito
    @ModelAttribute("total_carrito")
    public Double totalCarrito() {
        List<Product> productos = productosCarrito();
        if (productos != null) {
            return productos.stream()
                .mapToDouble(Product::getPrecio)
                .sum();
        }
        return 0.0;
    }
    
    // Añadir producto al carrito
    @GetMapping("/carrito/add/{id}")
    public String addCarrito(@PathVariable Long id) {
        List<Long> contenido = (List<Long>) session.getAttribute("carrito");
        
        if (contenido == null) {
            contenido = new ArrayList<>();
        }
        
        if (!contenido.contains(id)) {
            contenido.add(id);
        }
        
        session.setAttribute("carrito", contenido);
        session.setAttribute("items_carrito", contenido.size());
        
        return "redirect:/app/carrito";
    }
    
    // Eliminar del carrito
    @GetMapping("/carrito/eliminar/{id}")
    public String borrarDeCarrito(@PathVariable Long id) {
        List<Long> contenido = (List<Long>) session.getAttribute("carrito");
        
        if (contenido == null) {
            return "redirect:/public";
        }
        
        contenido.remove(id);
        
        if (contenido.isEmpty()) {
            session.removeAttribute("carrito");
            session.removeAttribute("items_carrito");
        } else {
            session.setAttribute("carrito", contenido);
            session.setAttribute("items_carrito", contenido.size());
        }
        
        return "redirect:/app/carrito";
    }
    
    // Finalizar compra
    @GetMapping("/carrito/finalizar")
    public String checkout() {
        List<Long> contenido = (List<Long>) session.getAttribute("carrito");
        
        if (contenido == null) {
            return "redirect:/public";
        }
        
        List<Product> productos = productosCarrito();
        Purchase compra = compraServicio.insertar(new Purchase(), usuario);
        productos.forEach(p -> compraServicio.addProductoCompra(p, compra));
        
        // Limpiar carrito
        session.removeAttribute("carrito");
        session.removeAttribute("items_carrito");
        
        // Enviar email de confirmación (asíncrono)
        new Thread(() -> {
            try {
                emailService.enviarEmailConfirmacionCompra(compra);
            } catch (Exception e) {
                System.err.println("Error enviando email: " + e.getMessage());
            }
        }).start();
        
        return "redirect:/app/miscompras/factura/" + compra.getId();
    }
}
```

### Favorites Pattern

```java
@RestController
@RequestMapping("/app/favoritos")
public class FavoriteController {
    
    @Autowired
    private FavoriteService favoritoServicio;
    
    @Autowired
    private UserService usuarioServicio;
    
    @Autowired
    private ProductService productoServicio;
    
    // Añadir a favoritos
    @PostMapping("/add/{productoId}")
    public ResponseEntity<?> addFavorite(@PathVariable Long productoId) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User usuario = usuarioServicio.buscarPorEmail(email);
            Product producto = productoServicio.buscarPorId(productoId);
            
            if (producto == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Producto no encontrado"
                ));
            }
            
            // Verificar que no sea favorito ya
            if (favoritoServicio.existeFavorito(usuario, producto)) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Ya está en favoritos",
                    "isFavorite", true
                ));
            }
            
            favoritoServicio.agregar(usuario, producto);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Añadido a favoritos",
                "isFavorite", true
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error al añadir a favoritos"
            ));
        }
    }
    
    // Eliminar de favoritos
    @DeleteMapping("/remove/{productoId}")
    public ResponseEntity<?> removeFavorite(@PathVariable Long productoId) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User usuario = usuarioServicio.buscarPorEmail(email);
            Product producto = productoServicio.buscarPorId(productoId);
            
            if (producto == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Producto no encontrado"
                ));
            }
            
            favoritoServicio.eliminar(usuario, producto);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Eliminado de favoritos",
                "isFavorite", false
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error al eliminar de favoritos"
            ));
        }
    }
}
```

### PDF Generation Pattern

```java
@Service
public class Html2PdfServiceImpl implements Html2PdfService {
    
    @Autowired
    PebbleEngine pebbleEngine;
    
    @Override
    public InputStreamResource html2PdfGenerator(Map<String, Object> data) {
        try {
            // Cargar template de Pebble
            PebbleTemplate compiledTemplate = pebbleEngine.getTemplate("app/pdf/facturapdf.peb.html");
            
            // Renderizar HTML
            Writer writer = new StringWriter();
            compiledTemplate.evaluate(writer, data);
            String html = writer.toString();
            
            // Convertir a PDF
            String destPath = "target/factura.pdf";
            HtmlConverter.convertToPdf(html, new FileOutputStream(destPath));
            
            return new InputStreamResource(new FileInputStream(destPath));
        } catch (IOException e) {
            System.err.println("Error generando PDF: " + e.getMessage());
            return null;
        }
    }
}
```

### Rating System Pattern

```java
@RestController
@RequestMapping("/app/ratings")
public class RatingController {
    
    @Autowired
    private RatingService ratingService;
    
    // Añadir valoración
    @PostMapping("/add")
    public ResponseEntity<?> addRating(
            @RequestParam Long productoId,
            @RequestParam Integer puntuacion,
            @RequestParam(required = false) String comentario) {
        
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User usuario = usuarioServicio.buscarPorEmail(email);
            Product producto = productoServicio.buscarPorId(productoId);
            
            // Validar puntuación
            if (puntuacion < 1 || puntuacion > 5) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "La puntuación debe estar entre 1 y 5"
                ));
            }
            
            // Crear o actualizar rating
            Rating rating = new Rating();
            rating.setUsuario(usuario);
            rating.setProducto(producto);
            rating.setPuntuacion(puntuacion);
            rating.setComentario(comentario);
            
            ratingService.save(rating);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Valoración guardada correctamente"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error al guardar la valoración"
            ));
        }
    }
    
    // Obtener valoraciones de un producto
    @GetMapping("/producto/{productoId}")
    public ResponseEntity<?> getProductRatings(@PathVariable Long productoId) {
        try {
            Product producto = productoServicio.buscarPorId(productoId);
            
            if (producto == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Producto no encontrado"
                ));
            }
            
            List<Rating> ratings = ratingService.findByProducto(producto);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "ratings", ratings
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error al obtener las valoraciones"
            ));
        }
    }
}
```

## Testing de Controllers

### Test de Controller con MockMvc

```java
@WebMvcTest(ProductController.class)
class ProductControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private ProductService productService;
    
    @Test
    void testListarProductos() throws Exception {
        List<Product> productos = Arrays.asList(
            new Product("Producto 1", 10.0),
            new Product("Producto 2", 20.0)
        );
        
        when(productService.findAll()).thenReturn(productos);
        
        mockMvc.perform(get("/app/misproductos"))
                .andExpect(status().isOk())
                .andExpect(view().name("app/producto/lista"))
                .andExpect(model().attributeExists("productos"))
                .andExpect(model().attribute("productos", hasSize(2)));
    }
    
    @Test
    void testCrearProducto() throws Exception {
        mockMvc.perform(post("/app/misproducto/nuevo/submit")
                .param("nombre", "Nuevo Producto")
                .param("precio", "25.50")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/app/misproductos"));
    }
}
```

## Troubleshooting Común

### Problema: Error 403 en formularios
**Causa:** Falta el token CSRF  
**Solución:**
```html
<form method="post">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    <!-- resto del formulario -->
</form>
```

### Problema: NullPointerException en templates
**Causa:** Variable no existe en el modelo  
**Solución:**
```pebble
{{ variable | default('valor_por_defecto') }}
{% if variable is not null %}{{ variable }}{% endif %}
```

### Problema: 404 en rutas
**Causa:** Mapeo incorrecto entre controller y template  
**Solución:** Verificar que coincidan:
- Controller: `return "app/producto/lista";`
- Template: `src/main/resources/templates/app/producto/lista.peb.html`

### Problema: Sesión no persiste
**Causa:** No configurar correctamente el session timeout  
**Solución:**
```properties
server.servlet.session.timeout=30m
```

---

**Actualizado con casos reales:** Noviembre 2025  
**Proyecto:** WalaSpringBoot Marketplace
