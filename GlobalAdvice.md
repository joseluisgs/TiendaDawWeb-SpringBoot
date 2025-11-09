# 🌐 GlobalControllerAdvice - Variables Globales del Proyecto

## Introducción

`GlobalControllerAdvice` es una clase anotada con `@ControllerAdvice` que permite inyectar automáticamente variables en **todas las vistas** del proyecto sin necesidad de añadirlas manualmente en cada controller. Esto es especialmente útil para información de autenticación, carrito de compras, y configuraciones globales.

## Ubicación

**Archivo:** `src/main/java/com/joseluisgs/walaspringboot/controllers/GlobalControllerAdvice.java`

## ¿Qué hace @ControllerAdvice?

`@ControllerAdvice` es una anotación de Spring que permite:
- ✅ Inyectar atributos globales en todos los modelos
- ✅ Manejar excepciones globalmente
- ✅ Aplicar data binding global
- ✅ Compartir configuración entre controllers

En nuestro proyecto, lo usamos principalmente para **variables globales** mediante `@ModelAttribute`.

## Variables Globales Disponibles

Todas estas variables están disponibles en **todas las plantillas Pebble** del proyecto sin necesidad de configuración adicional.

### 1. Variables de Autenticación

#### `currentUser`
**Tipo:** `User` o `null`  
**Descripción:** Usuario actualmente autenticado

**Uso en Pebble:**
```pebble
{% if currentUser is not null %}
    <p>Bienvenido, {{ currentUser.nombre }} {{ currentUser.apellidos }}</p>
    <p>Email: {{ currentUser.email }}</p>
    <p>Rol: {{ currentUser.rol }}</p>
{% else %}
    <p>No has iniciado sesión</p>
{% endif %}
```

**Implementación:**
```java
@ModelAttribute("currentUser")
public User getCurrentUser(Authentication authentication) {
    if (authentication != null && authentication.isAuthenticated()
            && !(authentication.getPrincipal() instanceof String)) {
        return (User) authentication.getPrincipal();
    }
    return null;
}
```

#### `isAuthenticated`
**Tipo:** `boolean`  
**Descripción:** Indica si el usuario está autenticado

**Uso en Pebble:**
```pebble
{% if isAuthenticated %}
    <a href="/app/perfil">Mi Perfil</a>
    <a href="/auth/logout">Cerrar Sesión</a>
{% else %}
    <a href="/auth/login">Iniciar Sesión</a>
    <a href="/auth/registro">Registrarse</a>
{% endif %}
```

**Implementación:**
```java
@ModelAttribute("isAuthenticated")
public boolean isAuthenticated(Authentication authentication) {
    return authentication != null && authentication.isAuthenticated()
            && !(authentication.getPrincipal() instanceof String);
}
```

#### `isAdmin`
**Tipo:** `boolean`  
**Descripción:** Indica si el usuario tiene rol ADMIN

**Uso en Pebble:**
```pebble
{% if isAdmin %}
    <a href="/admin/dashboard" class="btn btn-danger">
        <i class="bi bi-shield"></i> Panel Admin
    </a>
{% endif %}

{# En el navbar #}
<ul class="navbar-nav">
    {% if isAdmin %}
    <li class="nav-item">
        <a class="nav-link" href="/admin/usuarios">
            <i class="bi bi-people"></i> Gestión Usuarios
        </a>
    </li>
    <li class="nav-item">
        <a class="nav-link" href="/admin/productos">
            <i class="bi bi-box"></i> Gestión Productos
        </a>
    </li>
    {% endif %}
</ul>
```

**Implementación:**
```java
@ModelAttribute("isAdmin")
public boolean isAdmin(Authentication authentication) {
    if (authentication != null && authentication.isAuthenticated()
            && !(authentication.getPrincipal() instanceof String)) {
        User user = (User) authentication.getPrincipal();
        return "ADMIN".equals(user.getRol());
    }
    return false;
}
```

#### `username`
**Tipo:** `String` o `null`  
**Descripción:** Nombre completo del usuario autenticado

**Uso en Pebble:**
```pebble
{% if username %}
    <span class="navbar-text">
        <i class="bi bi-person-circle"></i> {{ username }}
    </span>
{% endif %}
```

**Implementación:**
```java
@ModelAttribute("username")
public String getUsername(Authentication authentication) {
    if (authentication != null && authentication.isAuthenticated()
            && !(authentication.getPrincipal() instanceof String)) {
        User user = (User) authentication.getPrincipal();
        return user.getNombre() + " " + user.getApellidos();
    }
    return null;
}
```

#### `userRole`
**Tipo:** `String` o `null`  
**Descripción:** Rol del usuario autenticado (ADMIN, MODERATOR, USER)

**Uso en Pebble:**
```pebble
{% if userRole == 'ADMIN' %}
    <span class="badge bg-danger">Administrador</span>
{% elseif userRole == 'MODERATOR' %}
    <span class="badge bg-warning">Moderador</span>
{% else %}
    <span class="badge bg-info">Usuario</span>
{% endif %}
```

**Implementación:**
```java
@ModelAttribute("userRole")
public String getUserRole(Authentication authentication) {
    if (authentication != null && authentication.isAuthenticated()
            && !(authentication.getPrincipal() instanceof String)) {
        User user = (User) authentication.getPrincipal();
        return user.getRol();
    }
    return null;
}
```

### 2. Variables del Carrito de Compras

#### `cartItemCount`
**Tipo:** `int`  
**Descripción:** Número de productos en el carrito (cantidad total de items)

**Uso en Pebble:**
```pebble
{# Badge en el navbar #}
<a class="nav-link" href="/app/carrito">
    <i class="bi bi-cart"></i> Carrito
    {% if cartItemCount > 0 %}
    <span class="badge bg-danger">{{ cartItemCount }}</span>
    {% endif %}
</a>

{# Mensaje condicional #}
{% if cartItemCount > 0 %}
    <p>Tienes {{ cartItemCount }} productos en tu carrito</p>
{% else %}
    <p>Tu carrito está vacío</p>
{% endif %}
```

**Implementación:**
```java
@ModelAttribute("cartItemCount")
public int getCartItemCount(HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    if (session != null) {
        List<Long> carrito = (List<Long>) session.getAttribute("carrito");
        return (carrito != null) ? carrito.size() : 0;
    }
    return 0;
}
```

#### `cartTotal`
**Tipo:** `Double`  
**Descripción:** Precio total de todos los productos en el carrito

**Uso en Pebble:**
```pebble
{# Mostrar total en navbar #}
{% if cartTotal > 0 %}
<div class="navbar-text">
    Total: {{ cartTotal | formatPrice }}
</div>
{% endif %}

{# Widget de carrito #}
<div class="cart-widget">
    <h5>Resumen del Carrito</h5>
    <p>Productos: {{ cartItemCount }}</p>
    <p>Total: <strong>{{ cartTotal | formatPrice }}</strong></p>
    <a href="/app/carrito" class="btn btn-primary">Ver Carrito</a>
</div>
```

**Implementación:**
```java
@ModelAttribute("cartTotal")
public Double getCartTotal(HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    if (session != null) {
        List<Long> carrito = (List<Long>) session.getAttribute("carrito");
        if (carrito != null && !carrito.isEmpty()) {
            List<Product> productos = productService.variosPorId(carrito);
            return productos.stream()
                    .mapToDouble(Product::getPrecio)
                    .sum();
        }
    }
    return 0.0;
}
```

#### `hasCartItems`
**Tipo:** `boolean`  
**Descripción:** Indica si hay productos en el carrito

**Uso en Pebble:**
```pebble
{% if hasCartItems %}
    <a href="/app/carrito/finalizar" class="btn btn-success">
        <i class="bi bi-check-circle"></i> Finalizar Compra
    </a>
{% else %}
    <div class="alert alert-info">
        Tu carrito está vacío. <a href="/">Explorar productos</a>
    </div>
{% endif %}
```

**Implementación:**
```java
@ModelAttribute("hasCartItems")
public boolean hasCartItems(HttpServletRequest request) {
    return getCartItemCount(request) > 0;
}
```

#### `items_carrito`
**Tipo:** `String`  
**Descripción:** Versión string del contador del carrito (para compatibilidad)

**Uso en Pebble:**
```pebble
{# Alternativa a cartItemCount #}
<span class="badge">{{ items_carrito }}</span>
```

**Implementación:**
```java
@ModelAttribute("items_carrito")
public String itemsCarrito(HttpServletRequest request) {
    int count = getCartItemCount(request);
    return count > 0 ? Integer.toString(count) : "";
}
```

### 3. Variables de Configuración Global

#### `defaultImage`
**Tipo:** `String`  
**Descripción:** URL de la imagen por defecto para productos sin imagen

**Uso en Pebble:**
```pebble
<img src="{% if producto.imagen %}{{ producto.imagen }}{% else %}{{ defaultImage }}{% endif %}" 
     alt="{{ producto.nombre }}">

{# O usando el getter del modelo #}
<img src="{{ producto.imagenOrDefault }}" alt="{{ producto.nombre }}">
```

**Implementación:**
```java
@ModelAttribute("defaultImage")
public String getDefaultImage() {
    return Product.DEFAULT_IMAGE_URL;
}
```

#### `filesPath`
**Tipo:** `String`  
**Descripción:** Ruta base para archivos subidos

**Uso en Pebble:**
```pebble
<img src="{{ filesPath }}{{ producto.nombreArchivo }}" alt="Producto">
```

**Implementación:**
```java
@ModelAttribute("filesPath")
public String getFilesPath() {
    return "/files/";
}
```

#### `appName`
**Tipo:** `String`  
**Descripción:** Nombre de la aplicación

**Uso en Pebble:**
```pebble
<title>{{ appName }} - {{ pageTitle | default('Marketplace') }}</title>

<footer>
    <p>&copy; 2025 {{ appName }}. Todos los derechos reservados.</p>
</footer>
```

**Implementación:**
```java
@ModelAttribute("appName")
public String getAppName() {
    return "WalaDaw";
}
```

#### `currentDateTime`
**Tipo:** `LocalDateTime`  
**Descripción:** Fecha y hora actual del servidor

**Uso en Pebble:**
```pebble
<footer>
    <p>Generado el {{ currentDateTime | formatDate }}</p>
</footer>
```

**Implementación:**
```java
@ModelAttribute("currentDateTime")
public java.time.LocalDateTime getCurrentDateTime() {
    return java.time.LocalDateTime.now();
}
```

### 4. Variables de CSRF

#### `csrfToken`
**Tipo:** `String`  
**Descripción:** Token CSRF para protección contra ataques

**Uso en Pebble:**
```pebble
<form method="post" action="/app/producto/crear">
    <input type="hidden" name="{{ csrfParamName }}" value="{{ csrfToken }}">
    <!-- resto del formulario -->
</form>
```

**Implementación:**
```java
@ModelAttribute("csrfToken")
public String getCsrfToken(HttpServletRequest request) {
    CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
    return csrfToken != null ? csrfToken.getToken() : "";
}
```

#### `csrfParamName`
**Tipo:** `String`  
**Descripción:** Nombre del parámetro CSRF (normalmente "_csrf")

**Uso en Pebble:**
```pebble
<form method="post">
    <input type="hidden" name="{{ csrfParamName }}" value="{{ csrfToken }}">
</form>
```

**Implementación:**
```java
@ModelAttribute("csrfParamName")
public String getCsrfParamName(HttpServletRequest request) {
    CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
    return csrfToken != null ? csrfToken.getParameterName() : "_csrf";
}
```

## Código Completo de GlobalControllerAdvice

```java
package com.joseluisgs.walaspringboot.controllers;

import com.joseluisgs.walaspringboot.models.Product;
import com.joseluisgs.walaspringboot.models.User;
import com.joseluisgs.walaspringboot.services.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private ProductService productService;

    // ⭐ AUTENTICACIÓN ⭐
    
    @ModelAttribute("currentUser")
    public User getCurrentUser(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication.getPrincipal() instanceof String)) {
            return (User) authentication.getPrincipal();
        }
        return null;
    }

    @ModelAttribute("isAuthenticated")
    public boolean isAuthenticated(Authentication authentication) {
        return authentication != null && authentication.isAuthenticated()
                && !(authentication.getPrincipal() instanceof String);
    }

    @ModelAttribute("isAdmin")
    public boolean isAdmin(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication.getPrincipal() instanceof String)) {
            User user = (User) authentication.getPrincipal();
            return "ADMIN".equals(user.getRol());
        }
        return false;
    }

    @ModelAttribute("username")
    public String getUsername(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication.getPrincipal() instanceof String)) {
            User user = (User) authentication.getPrincipal();
            return user.getNombre() + " " + user.getApellidos();
        }
        return null;
    }

    @ModelAttribute("userRole")
    public String getUserRole(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication.getPrincipal() instanceof String)) {
            User user = (User) authentication.getPrincipal();
            return user.getRol();
        }
        return null;
    }

    // ⭐ CSRF TOKENS ⭐
    
    @ModelAttribute("csrfToken")
    public String getCsrfToken(HttpServletRequest request) {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        return csrfToken != null ? csrfToken.getToken() : "";
    }

    @ModelAttribute("csrfParamName")
    public String getCsrfParamName(HttpServletRequest request) {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        return csrfToken != null ? csrfToken.getParameterName() : "_csrf";
    }

    // ⭐ SHOPPING CART - DISPONIBLE EN TODAS LAS PÁGINAS ⭐
    
    @ModelAttribute("cartItemCount")
    public int getCartItemCount(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            List<Long> carrito = (List<Long>) session.getAttribute("carrito");
            return (carrito != null) ? carrito.size() : 0;
        }
        return 0;
    }

    @ModelAttribute("cartTotal")
    public Double getCartTotal(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            List<Long> carrito = (List<Long>) session.getAttribute("carrito");
            if (carrito != null && !carrito.isEmpty()) {
                List<Product> productos = productService.variosPorId(carrito);
                return productos.stream()
                        .mapToDouble(Product::getPrecio)
                        .sum();
            }
        }
        return 0.0;
    }

    @ModelAttribute("hasCartItems")
    public boolean hasCartItems(HttpServletRequest request) {
        return getCartItemCount(request) > 0;
    }

    @ModelAttribute("items_carrito")
    public String itemsCarrito(HttpServletRequest request) {
        int count = getCartItemCount(request);
        return count > 0 ? Integer.toString(count) : "";
    }

    // ⭐ VARIABLES GLOBALES ⭐
    
    @ModelAttribute("defaultImage")
    public String getDefaultImage() {
        return Product.DEFAULT_IMAGE_URL;
    }

    @ModelAttribute("filesPath")
    public String getFilesPath() {
        return "/files/";
    }

    @ModelAttribute("appName")
    public String getAppName() {
        return "WalaDaw";
    }

    @ModelAttribute("currentDateTime")
    public java.time.LocalDateTime getCurrentDateTime() {
        return java.time.LocalDateTime.now();
    }
}
```

## Casos de Uso Reales

### 1. Navbar con Carrito y Autenticación

```pebble
<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <div class="container-fluid">
        <a class="navbar-brand" href="/">
            <i class="bi bi-shop"></i> {{ appName }}
        </a>
        
        <ul class="navbar-nav ms-auto">
            <!-- Carrito (siempre visible) -->
            <li class="nav-item">
                <a class="nav-link position-relative" href="/app/carrito">
                    <i class="bi bi-cart"></i> Carrito
                    {% if cartItemCount > 0 %}
                    <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger">
                        {{ cartItemCount }}
                    </span>
                    {% endif %}
                </a>
            </li>
            
            <!-- Menú de usuario autenticado -->
            {% if isAuthenticated %}
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle" href="#" data-bs-toggle="dropdown">
                        <i class="bi bi-person-circle"></i> {{ username }}
                    </a>
                    <ul class="dropdown-menu dropdown-menu-end">
                        <li><a class="dropdown-item" href="/app/perfil">Mi Perfil</a></li>
                        <li><a class="dropdown-item" href="/app/misproductos">Mis Productos</a></li>
                        <li><a class="dropdown-item" href="/app/miscompras">Mis Compras</a></li>
                        
                        {% if isAdmin %}
                        <li><hr class="dropdown-divider"></li>
                        <li><a class="dropdown-item text-danger" href="/admin/dashboard">
                            <i class="bi bi-shield"></i> Panel Admin
                        </a></li>
                        {% endif %}
                        
                        <li><hr class="dropdown-divider"></li>
                        <li><a class="dropdown-item" href="/auth/logout">Cerrar Sesión</a></li>
                    </ul>
                </li>
            {% else %}
                <li class="nav-item">
                    <a class="nav-link" href="/auth/login">
                        <i class="bi bi-box-arrow-in-right"></i> Iniciar Sesión
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="/auth/registro">Registrarse</a>
                </li>
            {% endif %}
        </ul>
    </div>
</nav>
```

### 2. Widget de Carrito en Sidebar

```pebble
<div class="card">
    <div class="card-header bg-primary text-white">
        <h5 class="mb-0"><i class="bi bi-cart"></i> Tu Carrito</h5>
    </div>
    <div class="card-body">
        {% if hasCartItems %}
            <p class="mb-2">
                <strong>{{ cartItemCount }}</strong> 
                producto{{ cartItemCount == 1 ? '' : 's' }}
            </p>
            <p class="mb-3">
                <strong>Total:</strong> {{ cartTotal | formatPrice }}
            </p>
            <a href="/app/carrito" class="btn btn-primary btn-sm w-100 mb-2">
                Ver Carrito
            </a>
            <a href="/app/carrito/finalizar" class="btn btn-success btn-sm w-100">
                Finalizar Compra
            </a>
        {% else %}
            <p class="text-muted">Tu carrito está vacío</p>
            <a href="/" class="btn btn-primary btn-sm w-100">
                Explorar Productos
            </a>
        {% endif %}
    </div>
</div>
```

### 3. Protección de Rutas en la Vista

```pebble
{# Solo mostrar si está autenticado #}
{% if isAuthenticated %}
    <a href="/app/favoritos" class="btn btn-warning">
        <i class="bi bi-heart"></i> Mis Favoritos
    </a>
    
    {# Botón de compra solo para usuarios no propietarios #}
    {% if currentUser.id != producto.propietario.id %}
        <a href="/app/carrito/add/{{ producto.id }}" class="btn btn-success">
            <i class="bi bi-cart-plus"></i> Añadir al Carrito
        </a>
    {% endif %}
{% else %}
    <a href="/auth/login" class="btn btn-primary">
        Inicia sesión para comprar
    </a>
{% endif %}

{# Sección admin solo para administradores #}
{% if isAdmin %}
    <div class="admin-section">
        <h3>Panel de Administración</h3>
        <a href="/admin/usuarios" class="btn btn-danger">Gestionar Usuarios</a>
        <a href="/admin/productos" class="btn btn-danger">Gestionar Productos</a>
    </div>
{% endif %}
```

### 4. Formularios con CSRF

```pebble
{# Formulario de creación de producto #}
<form method="post" action="/app/misproducto/nuevo/submit" enctype="multipart/form-data">
    {# CSRF Token - SIEMPRE incluir en formularios POST #}
    <input type="hidden" name="{{ csrfParamName }}" value="{{ csrfToken }}">
    
    <div class="mb-3">
        <label for="nombre" class="form-label">Nombre del Producto</label>
        <input type="text" class="form-control" id="nombre" name="nombre" required>
    </div>
    
    <div class="mb-3">
        <label for="precio" class="form-label">Precio</label>
        <input type="number" step="0.01" class="form-control" id="precio" name="precio" required>
    </div>
    
    <button type="submit" class="btn btn-primary">Crear Producto</button>
</form>
```

## Mejores Prácticas

### 1. No Sobrecargar GlobalControllerAdvice

❌ **MALO: Demasiados @ModelAttribute**
```java
@ModelAttribute("todoLosProductos")
public List<Product> getTodosLosProductos() {
    return productService.findAll(); // ¡Se ejecuta en TODAS las peticiones!
}

@ModelAttribute("estadisticasCompletas")
public EstadisticasDTO getEstadisticas() {
    return estadisticasService.calcular(); // Costoso computacionalmente
}
```

✅ **BUENO: Solo datos livianos y frecuentes**
```java
@ModelAttribute("cartItemCount")
public int getCartItemCount(HttpServletRequest request) {
    // Ligero: solo cuenta elementos
    HttpSession session = request.getSession(false);
    if (session != null) {
        List<Long> carrito = (List<Long>) session.getAttribute("carrito");
        return (carrito != null) ? carrito.size() : 0;
    }
    return 0;
}
```

### 2. Usar @ModelAttribute Solo para Datos Comunes

**✅ Usar GlobalControllerAdvice para:**
- Información de autenticación
- Datos del carrito
- Configuraciones globales
- Tokens CSRF
- Variables usadas en layouts/fragments

**❌ NO usar GlobalControllerAdvice para:**
- Datos específicos de una sola vista
- Queries pesadas a la base de datos
- Cálculos complejos
- Datos que cambian frecuentemente

### 3. Lazy Loading cuando sea Posible

```java
// ✅ BUENO: Solo obtener datos si existen
@ModelAttribute("cartTotal")
public Double getCartTotal(HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    if (session != null) {
        List<Long> carrito = (List<Long>) session.getAttribute("carrito");
        if (carrito != null && !carrito.isEmpty()) {
            // Solo cargar productos si hay items en el carrito
            List<Product> productos = productService.variosPorId(carrito);
            return productos.stream()
                    .mapToDouble(Product::getPrecio)
                    .sum();
        }
    }
    return 0.0;
}
```

### 4. Documentar las Variables

Mantén documentación actualizada de todas las variables globales disponibles para que otros desarrolladores sepan qué pueden usar.

## Performance

Las variables en `GlobalControllerAdvice` se calculan en **cada petición HTTP**. Para optimizar:

1. **Cachear cuando sea apropiado:**
```java
@Cacheable("appConfig")
@ModelAttribute("appName")
public String getAppName() {
    return configService.getAppName();
}
```

2. **Usar sesión para datos del usuario:**
```java
// Los datos de sesión ya están en memoria
@ModelAttribute("cartItemCount")
public int getCartItemCount(HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    // Acceso rápido desde sesión
}
```

3. **Evitar N+1 queries:**
```java
// ❌ MALO
@ModelAttribute("cartProducts")
public List<Product> getCartProducts(HttpServletRequest request) {
    // Múltiples queries si no está optimizado
    return cartService.getProducts();
}

// ✅ BUENO
@ModelAttribute("cartItemCount")
public int getCartItemCount(HttpServletRequest request) {
    // Solo cuenta, sin queries adicionales
    return cartService.getItemCount();
}
```

## Troubleshooting

### Problema: Variable no aparece en la vista
**Solución:** Verificar que:
1. El método está anotado con `@ModelAttribute`
2. La clase tiene `@ControllerAdvice`
3. El nombre del atributo coincide con el usado en la vista
4. No hay excepciones en el método

### Problema: Performance lento
**Solución:** 
- Revisar queries en los `@ModelAttribute`
- Usar lazy loading
- Cachear datos estáticos
- Mover lógica pesada al controller específico

---

**Última actualización:** Noviembre 2025  
**Proyecto:** WalaDaw Marketplace  
**Documentación completa de variables globales disponibles en todas las vistas**
