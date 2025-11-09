# 📘 Tutorial Exhaustivo de Pebble Template Engine

## Tabla de Contenidos

- [Introducción](#introducción)
- [Configuración](#configuración)
- [Sintaxis Básica](#sintaxis-básica)
- [Variables y Expresiones](#variables-y-expresiones)
- [Estructuras de Control](#estructuras-de-control)
- [Filtros](#filtros)
- [Funciones](#funciones)
- [Herencia y Layouts](#herencia-y-layouts)
- [Macros](#macros)
- [Includes](#includes)
- [Internacionalización](#internacionalización)
- [Best Practices](#best-practices)

## Introducción

**Pebble** es un motor de plantillas Java inspirado en Twig (PHP) con características modernas, alto rendimiento y sintaxis limpia.

### ¿Por qué Pebble?

✅ **Rápido**: Compilación automática de plantillas  
✅ **Seguro**: Auto-escaping para prevenir XSS  
✅ **Flexible**: Herencia, macros, filtros personalizados  
✅ **Legible**: Sintaxis clara y concisa  
✅ **Moderno**: Integración perfecta con Spring Boot  

### Pebble vs Thymeleaf

| Característica | Pebble | Thymeleaf |
|----------------|--------|-----------|
| **Sintaxis** | `{{ variable }}` | `th:text="${variable}"` |
| **Rendimiento** | Más rápido | Más lento |
| **Curva de aprendizaje** | Baja | Media |
| **Natural templates** | No | Sí |
| **Herencia** | Sí (simple) | Sí (complicada) |

## Configuración

### Dependencias Gradle

```kotlin
dependencies {
    implementation("io.pebbletemplates:pebble-spring-boot-starter:3.2.2")
    implementation("io.pebbletemplates:pebble:3.2.2")
}
```

### application.properties

```properties
# Configuración de Pebble
pebble.cache=false                    # Desactivar cache en desarrollo
pebble.suffix=.peb                    # Extensión de archivos
pebble.defaultLocale=es_ES            # Idioma por defecto
```

### Estructura de Plantillas

```
src/main/resources/templates/
├── fragments/          # Componentes reutilizables
│   ├── head.peb
│   ├── navbar.peb
│   └── footer.peb
├── layouts/            # Layouts base
│   └── base.peb
├── productos/          # Plantillas de productos
│   ├── lista.peb
│   ├── detalle.peb
│   └── form.peb
└── index.peb          # Página principal
```

## Sintaxis Básica

### Delimitadores

Pebble utiliza tres tipos de delimitadores:

```pebble
{# Comentarios - No se renderizan #}
{# Esto es un comentario que no aparece en el HTML final #}

{{ expresión }}
{# Salida de variables - Se renderiza y se escapa automáticamente #}
{{ producto.nombre }}
{{ usuario.email }}

{% etiqueta %}
{# Etiquetas de control - if, for, block, etc. #}
{% if usuario %}
    <p>Bienvenido</p>
{% endif %}
```

### Comentarios

```pebble
{# Comentario de una línea #}

{#
    Comentario
    multilínea
    para documentar
#}
```

## Variables y Expresiones

### Acceso a Variables

```pebble
{# Variable simple #}
{{ nombre }}

{# Propiedad de objeto #}
{{ producto.nombre }}
{{ usuario.email }}

{# Método getter #}
{{ producto.getPrecio() }}
{{ usuario.getNombre() }}

{# Acceso a array/lista #}
{{ productos[0] }}
{{ productos[indice] }}

{# Acceso a map #}
{{ datos['clave'] }}
{{ datos.clave }}
```

### Valores por Defecto

```pebble
{# Si la variable no existe, usar valor por defecto #}
{{ nombre | default('Anónimo') }}
{{ precio | default(0) }}
{{ descripcion | default('Sin descripción') }}
```

### Operadores

#### Aritméticos

```pebble
{{ 5 + 3 }}        {# 8 #}
{{ 10 - 4 }}       {# 6 #}
{{ 6 * 7 }}        {# 42 #}
{{ 15 / 3 }}       {# 5 #}
{{ 17 % 5 }}       {# 2 (módulo) #}
```

#### Comparación

```pebble
{{ precio > 100 }}
{{ edad >= 18 }}
{{ nombre == 'Admin' }}
{{ estado != 'inactivo' }}
{{ precio < 50 }}
{{ cantidad <= 10 }}
```

#### Lógicos

```pebble
{{ true and false }}        {# false #}
{{ true or false }}         {# true #}
{{ not true }}              {# false #}
{{ (precio > 50) and (stock > 0) }}
```

#### Ternario

```pebble
{{ edad >= 18 ? 'Adulto' : 'Menor' }}
{{ stock > 0 ? 'Disponible' : 'Agotado' }}
```

### Concatenación

```pebble
{# Concatenar strings #}
{{ 'Hola ' ~ nombre ~ '!' }}
{{ 'Precio: ' ~ precio ~ '€' }}
```

## Estructuras de Control

### Condicionales - if/elseif/else

```pebble
{% if usuario %}
    <p>Bienvenido, {{ usuario.nombre }}</p>
{% endif %}

{% if rol == 'ADMIN' %}
    <a href="/admin/dashboard">Panel Admin</a>
{% elseif rol == 'MODERATOR' %}
    <a href="/moderator/panel">Panel Moderador</a>
{% else %}
    <a href="/perfil">Mi Perfil</a>
{% endif %}

{# Operadores lógicos #}
{% if usuario and usuario.activo %}
    <p>Usuario activo</p>
{% endif %}

{% if precio > 100 or esOferta %}
    <span class="badge">Promoción</span>
{% endif %}
```

### Bucles - for

#### Iteración Básica

```pebble
<ul>
{% for producto in productos %}
    <li>{{ producto.nombre }} - {{ producto.precio }}€</li>
{% endfor %}
</ul>
```

#### Con Índice

```pebble
<table>
{% for producto in productos %}
    <tr>
        <td>{{ loop.index }}</td>      {# Índice desde 1 #}
        <td>{{ loop.index0 }}</td>     {# Índice desde 0 #}
        <td>{{ producto.nombre }}</td>
    </tr>
{% endfor %}
</table>
```

#### Variables del Loop

```pebble
{% for item in items %}
    <div class="{{ loop.first ? 'primero' : '' }} {{ loop.last ? 'ultimo' : '' }}">
        <p>Item {{ loop.index }} de {{ loop.length }}</p>
        <p>{{ item.nombre }}</p>
    </div>
{% endfor %}

{# Propiedades disponibles:
   - loop.index: índice actual (desde 1)
   - loop.index0: índice actual (desde 0)
   - loop.revindex: índice invertido (desde length hasta 1)
   - loop.revindex0: índice invertido (desde length-1 hasta 0)
   - loop.first: true si es la primera iteración
   - loop.last: true si es la última iteración
   - loop.length: número total de items
#}
```

#### Lista Vacía

```pebble
{% for producto in productos %}
    <div class="producto">{{ producto.nombre }}</div>
{% else %}
    <p>No hay productos disponibles</p>
{% endfor %}
```

#### Iteración con Filtro

```pebble
{% for producto in productos if producto.precio < 100 %}
    <div>{{ producto.nombre }}</div>
{% endfor %}
```

### Set - Asignar Variables

```pebble
{% set total = 0 %}
{% set nombre = 'Juan' %}
{% set precio = producto.precio * 1.21 %}

{# Asignación múltiple #}
{% set x, y, z = 1, 2, 3 %}
```

## Filtros

Los filtros transforman valores. Se aplican con el operador `|`:

### Filtros de Texto

```pebble
{# Mayúsculas/Minúsculas #}
{{ 'hola mundo' | upper }}           {# HOLA MUNDO #}
{{ 'HOLA MUNDO' | lower }}           {# hola mundo #}
{{ 'hola mundo' | capitalize }}      {# Hola mundo #}
{{ 'hola mundo' | title }}           {# Hola Mundo #}

{# Trim - Eliminar espacios #}
{{ '  texto  ' | trim }}             {# texto #}

{# Truncar #}
{{ descripcion | slice(0, 100) }}    {# Primeros 100 caracteres #}
{{ texto | abbreviate(50) }}         {# Trunca a 50 y añade "..." #}

{# Reemplazar #}
{{ texto | replace({'viejo': 'nuevo'}) }}

{# URL encode #}
{{ 'Hola Mundo' | url_encode }}      {# Hola+Mundo #}
```

### Filtros Numéricos

```pebble
{# Formato de números #}
{{ 1234.5678 | numberformat }}                    {# 1,234.57 #}
{{ precio | numberformat('0.00') }}               {# Formato personalizado #}

{# Redondear #}
{{ 3.7 | round }}                                 {# 4 #}
{{ 3.14159 | round(2) }}                          {# 3.14 #}

{# Absoluto #}
{{ -5 | abs }}                                    {# 5 #}
```

### Filtros de Fecha

```pebble
{# Formato de fecha #}
{{ fechaCompra | date('dd/MM/yyyy') }}            {# 15/01/2025 #}
{{ fechaCompra | date('dd MMM yyyy HH:mm') }}     {# 15 Ene 2025 14:30 #}
{{ fechaCompra | date('full') }}                  {# Formato completo #}

{# Formatos disponibles:
   - short: 15/01/25
   - medium: 15-ene-2025
   - long: 15 de enero de 2025
   - full: lunes, 15 de enero de 2025
#}
```

### Filtros de Colecciones

```pebble
{# Longitud #}
{{ productos | length }}                          {# Número de elementos #}

{# Slice - Subconjunto #}
{{ productos | slice(0, 5) }}                     {# Primeros 5 elementos #}
{{ productos | slice(5, 10) }}                    {# Elementos 5 a 10 #}

{# Ordenar #}
{{ productos | sort }}                            {# Orden natural #}
{{ productos | sort('precio') }}                  {# Por propiedad #}

{# Reverso #}
{{ productos | reverse }}

{# Unir #}
{{ tags | join(', ') }}                           {# tag1, tag2, tag3 #}

{# Primero y último #}
{{ productos | first }}
{{ productos | last }}
```

### Filtros de Escape

```pebble
{# HTML escape (por defecto) #}
{{ '<script>alert("XSS")</script>' }}
{# Renderiza: &lt;script&gt;alert("XSS")&lt;/script&gt; #}

{# Sin escape (usar con cuidado) #}
{{ htmlContenido | raw }}

{# JSON #}
{{ objeto | json_encode }}

{# URL #}
{{ parametro | url_encode }}
```

### Filtros Condicionales

```pebble
{# Default - Valor por defecto #}
{{ nombre | default('Anónimo') }}

{# Empty check #}
{% if productos is empty %}
    <p>No hay productos</p>
{% endif %}

{% if usuario is not empty %}
    <p>Hola, {{ usuario.nombre }}</p>
{% endif %}
```

### Encadenar Filtros

```pebble
{{ producto.descripcion | trim | upper | slice(0, 50) }}
{{ precio | numberformat | default('Gratis') }}
{{ texto | lower | capitalize | abbreviate(100) }}
```

## Filtros Personalizados

Pebble permite crear filtros personalizados para necesidades específicas de la aplicación.

### Configuración de Filtros Personalizados

```java
package com.joseluisgs.walaspringboot.config;

import io.pebbletemplates.pebble.extension.AbstractExtension;
import io.pebbletemplates.pebble.extension.Filter;
import io.pebbletemplates.boot.autoconfigure.PebbleAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Configuration
@AutoConfigureBefore(PebbleAutoConfiguration.class)
public class PebbleConfig {

    @Bean
    public io.pebbletemplates.pebble.extension.Extension customPebbleExtension() {
        return new AbstractExtension() {
            @Override
            public Map<String, Filter> getFilters() {
                Map<String, Filter> filters = new HashMap<>();
                filters.put("formatDate", new FormatDateFilter());
                filters.put("formatPrice", new FormatPriceFilter());
                return filters;
            }
        };
    }

    // Filtro para formatear fechas
    private static class FormatDateFilter implements Filter {
        @Override
        public Object apply(Object input, Map<String, Object> args, 
                          io.pebbletemplates.pebble.template.PebbleTemplate self,
                          io.pebbletemplates.pebble.template.EvaluationContext context, 
                          int lineNumber) throws io.pebbletemplates.pebble.error.PebbleException {
            if (input == null) {
                return "";
            }

            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                    "d MMM yyyy, HH:mm", 
                    new Locale("es", "ES")
                );
                
                if (input instanceof LocalDateTime) {
                    return ((LocalDateTime) input).format(formatter);
                } else if (input instanceof Date) {
                    SimpleDateFormat sdf = new SimpleDateFormat(
                        "d MMM yyyy, HH:mm", 
                        new Locale("es", "ES")
                    );
                    return sdf.format((Date) input);
                } else if (input instanceof String) {
                    // Try to parse as LocalDateTime
                    try {
                        LocalDateTime dateTime = LocalDateTime.parse(input.toString());
                        return dateTime.format(formatter);
                    } catch (Exception e) {
                        return input.toString();
                    }
                }
            } catch (Exception e) {
                return input.toString();
            }
            
            return input.toString();
        }

        @Override
        public List<String> getArgumentNames() {
            return null;
        }
    }

    // Filtro para formatear precios
    private static class FormatPriceFilter implements Filter {
        @Override
        public Object apply(Object input, Map<String, Object> args, 
                          io.pebbletemplates.pebble.template.PebbleTemplate self,
                          io.pebbletemplates.pebble.template.EvaluationContext context, 
                          int lineNumber) throws io.pebbletemplates.pebble.error.PebbleException {
            if (input == null) {
                return "0,00 €";
            }

            try {
                double price;
                if (input instanceof Number) {
                    price = ((Number) input).doubleValue();
                } else {
                    price = Double.parseDouble(input.toString());
                }

                DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("es", "ES"));
                symbols.setDecimalSeparator(',');
                symbols.setGroupingSeparator('.');
                
                DecimalFormat df = new DecimalFormat("#,##0.00 €", symbols);
                return df.format(price);
            } catch (Exception e) {
                return input.toString() + " €";
            }
        }

        @Override
        public List<String> getArgumentNames() {
            return null;
        }
    }
}
```

### Uso de Filtros Personalizados

#### formatPrice

Formatea números como precios con el símbolo € y separadores de miles y decimales según la localización española.

```pebble
{# Precio simple #}
{{ 99.99 | formatPrice }}
{# Salida: 99,99 € #}

{# Precio con miles #}
{{ 1599.50 | formatPrice }}
{# Salida: 1.599,50 € #}

{# Variable de precio #}
<span class="badge bg-success">{{ producto.precio | formatPrice }}</span>

{# Total del carrito #}
<h4>Total: {{ total_carrito | formatPrice }}</h4>
```

**Características:**
- ✅ Separador de miles: punto (.)
- ✅ Separador de decimales: coma (,)
- ✅ Siempre muestra 2 decimales
- ✅ Añade símbolo € al final
- ✅ Maneja valores null como "0,00 €"

#### formatDate

Formatea fechas en formato español legible.

```pebble
{# Fecha de compra #}
<span>Fecha: {{ compra.fechaCompra | formatDate }}</span>
{# Salida: 15 nov 2025, 14:30 #}

{# Fecha de registro #}
<small>Registrado: {{ usuario.fechaRegistro | formatDate }}</small>
{# Salida: 1 ene 2025, 09:15 #}

{# En tarjeta #}
<div class="card-header">
    <span>Compra #{{ compra.id }}</span>
    <span>{{ compra.fechaCompra | formatDate }}</span>
</div>
```

**Características:**
- ✅ Formato: "d MMM yyyy, HH:mm"
- ✅ Meses en español abreviados (ene, feb, mar...)
- ✅ Soporta LocalDateTime y Date
- ✅ Maneja valores null devolviendo string vacío

### Creando Filtros Adicionales

#### Ejemplo: Filtro para Números (formatNumber)

Si necesitas formatear números sin el símbolo €:

```java
private static class FormatNumberFilter implements Filter {
    @Override
    public Object apply(Object input, Map<String, Object> args, 
                      io.pebbletemplates.pebble.template.PebbleTemplate self,
                      io.pebbletemplates.pebble.template.EvaluationContext context, 
                      int lineNumber) throws io.pebbletemplates.pebble.error.PebbleException {
        if (input == null) {
            return "0";
        }

        try {
            // Obtener decimales del argumento (por defecto 0)
            int decimals = 0;
            if (args.containsKey("decimals")) {
                decimals = ((Number) args.get("decimals")).intValue();
            }
            
            double number;
            if (input instanceof Number) {
                number = ((Number) input).doubleValue();
            } else {
                number = Double.parseDouble(input.toString());
            }

            DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("es", "ES"));
            symbols.setDecimalSeparator(',');
            symbols.setGroupingSeparator('.');
            
            String pattern = decimals > 0 ? 
                "#,##0." + "0".repeat(decimals) : 
                "#,##0";
            
            DecimalFormat df = new DecimalFormat(pattern, symbols);
            return df.format(number);
        } catch (Exception e) {
            return input.toString();
        }
    }

    @Override
    public List<String> getArgumentNames() {
        return List.of("decimals");
    }
}
```

**Uso:**
```pebble
{# Sin decimales #}
{{ cantidad | formatNumber }}
{# Salida: 1.500 #}

{# Con 1 decimal #}
{{ averageRating | formatNumber(decimals=1) }}
{# Salida: 4,5 #}

{# Con 2 decimales #}
{{ precio | formatNumber(decimals=2) }}
{# Salida: 99,95 #}
```

#### Ejemplo: Filtro Truncate con Elipsis

```java
private static class TruncateFilter implements Filter {
    @Override
    public Object apply(Object input, Map<String, Object> args, 
                      io.pebbletemplates.pebble.template.PebbleTemplate self,
                      io.pebbletemplates.pebble.template.EvaluationContext context, 
                      int lineNumber) throws io.pebbletemplates.pebble.error.PebbleException {
        if (input == null) {
            return "";
        }

        String text = input.toString();
        int length = 100; // Por defecto
        
        if (args.containsKey("length")) {
            length = ((Number) args.get("length")).intValue();
        }
        
        if (text.length() <= length) {
            return text;
        }
        
        return text.substring(0, length) + "...";
    }

    @Override
    public List<String> getArgumentNames() {
        return List.of("length");
    }
}
```

**Uso:**
```pebble
{# Descripción truncada #}
<p>{{ producto.descripcion | truncate(60) }}</p>
{# Salida: "Este es un producto muy interesante que tiene muchas ca..." #}

{# Sin parámetros (usa 100 por defecto) #}
<p>{{ texto | truncate }}</p>
```

### Registrar Múltiples Filtros

Para añadir el nuevo filtro al `PebbleConfig`:

```java
@Bean
public io.pebbletemplates.pebble.extension.Extension customPebbleExtension() {
    return new AbstractExtension() {
        @Override
        public Map<String, Filter> getFilters() {
            Map<String, Filter> filters = new HashMap<>();
            filters.put("formatDate", new FormatDateFilter());
            filters.put("formatPrice", new FormatPriceFilter());
            filters.put("formatNumber", new FormatNumberFilter());  // Nuevo
            filters.put("truncate", new TruncateFilter());          // Nuevo
            return filters;
        }
    };
}
```

### Best Practices para Filtros Personalizados

#### 1. Manejo de Null
```java
if (input == null) {
    return ""; // o valor por defecto apropiado
}
```

#### 2. Validación de Tipos
```java
try {
    if (input instanceof Number) {
        // manejar número
    } else {
        // intentar parsear
    }
} catch (Exception e) {
    return input.toString(); // fallback seguro
}
```

#### 3. Parámetros Opcionales
```java
@Override
public List<String> getArgumentNames() {
    return List.of("param1", "param2"); // o null si no hay parámetros
}
```

#### 4. Documentación Clara
```pebble
{# ✅ Bien documentado #}
{# Formato: formatDate(fecha) -> "15 nov 2025, 14:30" #}
{{ compra.fechaCompra | formatDate }}

{# ✅ Con parámetros #}
{# Formato: truncate(texto, length=100) #}
{{ descripcion | truncate(60) }}
```

#### 5. Nombres Descriptivos
```java
// ✅ Bueno
filters.put("formatPrice", new FormatPriceFilter());
filters.put("formatDate", new FormatDateFilter());

// ❌ Malo
filters.put("fp", new FormatPriceFilter());
filters.put("fd", new FormatDateFilter());
```

### Filtros vs Funciones

| Aspecto | Filtros | Funciones |
|---------|---------|-----------|
| **Sintaxis** | `{{ valor \| filtro }}` | `{{ funcion(valor) }}` |
| **Uso** | Transformar valores existentes | Generar nuevos valores |
| **Encadenado** | `{{ valor \| filtro1 \| filtro2 }}` | No natural |
| **Ejemplo** | `{{ precio \| formatPrice }}` | `{{ range(1, 10) }}` |

**Cuando usar filtros:**
- Transformar datos de salida (formato, escape)
- Operaciones sobre valores del modelo
- Presentación de datos

**Cuando usar funciones:**
- Generar datos nuevos
- Operaciones complejas
- Utilidades reutilizables

## Funciones

### Funciones Incorporadas

```pebble
{# Range - Secuencia de números #}
{% for i in range(1, 10) %}
    <div>Item {{ i }}</div>
{% endfor %}

{# Min/Max #}
{{ min(10, 20, 5) }}                              {# 5 #}
{{ max(10, 20, 5) }}                              {# 20 #}

{# Parent - En herencia #}
{% block content %}
    {{ parent() }}
    {# Incluye el contenido del bloque padre #}
{% endblock %}
```

### Funciones Personalizadas (i18n)

```pebble
{# Internacionalización #}
{{ i18n('producto.nombre') }}
{{ i18n('mensaje.bienvenida', usuario.nombre) }}
```

## Herencia y Layouts

### Layout Base

**templates/layouts/base.peb:**

```pebble
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>{% block title %}WalaSpringBoot{% endblock %}</title>
    
    {% block styles %}
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    {% endblock %}
</head>
<body>
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
        {% block navbar %}
        {# Navbar por defecto #}
        {% endblock %}
    </nav>
    
    <main class="container mt-4">
        {% block content %}
        {# Contenido principal - debe ser sobrescrito #}
        {% endblock %}
    </main>
    
    <footer class="mt-5 py-3 bg-light">
        {% block footer %}
        <p class="text-center">&copy; 2025 WalaSpringBoot</p>
        {% endblock %}
    </footer>
    
    {% block scripts %}
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    {% endblock %}
</body>
</html>
```

### Plantilla Hija

**templates/productos/lista.peb:**

```pebble
{% extends "layouts/base.peb" %}

{% block title %}Lista de Productos - {{ parent() }}{% endblock %}

{% block content %}
<h1>Productos Disponibles</h1>

<div class="row">
    {% for producto in productos %}
    <div class="col-md-4 mb-3">
        <div class="card">
            <img src="{{ producto.imagen }}" class="card-img-top" alt="{{ producto.nombre }}">
            <div class="card-body">
                <h5 class="card-title">{{ producto.nombre }}</h5>
                <p class="card-text">{{ producto.descripcion | abbreviate(100) }}</p>
                <p class="text-primary"><strong>{{ producto.precio }}€</strong></p>
                <a href="/producto/{{ producto.id }}" class="btn btn-primary">Ver Detalle</a>
            </div>
        </div>
    </div>
    {% endfor %}
</div>
{% endblock %}

{% block scripts %}
{{ parent() }}
<script>
    console.log('Lista de productos cargada');
</script>
{% endblock %}
```

### Bloques Múltiples

```pebble
{% extends "layouts/base.peb" %}

{% block title %}Mi Página{% endblock %}

{% block styles %}
{{ parent() }}
<link rel="stylesheet" href="/css/custom.css">
{% endblock %}

{% block content %}
<h1>Contenido Principal</h1>
{% endblock %}

{% block scripts %}
{{ parent() }}
<script src="/js/custom.js"></script>
{% endblock %}
```

## Macros

Las macros son funciones reutilizables que generan HTML.

### Definir Macros

**templates/macros/forms.peb:**

```pebble
{# Macro para input de texto #}
{% macro input(name, label, value='', type='text', required=false) %}
<div class="mb-3">
    <label for="{{ name }}" class="form-label">{{ label }}</label>
    <input type="{{ type }}" 
           class="form-control" 
           id="{{ name }}" 
           name="{{ name }}" 
           value="{{ value }}"
           {% if required %}required{% endif %}>
</div>
{% endmacro %}

{# Macro para textarea #}
{% macro textarea(name, label, value='', rows=3) %}
<div class="mb-3">
    <label for="{{ name }}" class="form-label">{{ label }}</label>
    <textarea class="form-control" 
              id="{{ name }}" 
              name="{{ name }}" 
              rows="{{ rows }}">{{ value }}</textarea>
</div>
{% endmacro %}

{# Macro para botón #}
{% macro button(text, type='primary', submit=true) %}
<button type="{% if submit %}submit{% else %}button{% endif %}" 
        class="btn btn-{{ type }}">
    {{ text }}
</button>
{% endmacro %}
```

### Usar Macros

```pebble
{% import "macros/forms.peb" %}

<form method="POST" action="/producto/guardar">
    {{ input('nombre', 'Nombre del Producto', producto.nombre, required=true) }}
    {{ input('precio', 'Precio', producto.precio, type='number') }}
    {{ textarea('descripcion', 'Descripción', producto.descripcion, rows=5) }}
    {{ button('Guardar Producto') }}
    {{ button('Cancelar', type='secondary', submit=false) }}
</form>
```

### Macro con Contenido

```pebble
{% macro card(title) %}
<div class="card">
    <div class="card-header">
        <h5>{{ title }}</h5>
    </div>
    <div class="card-body">
        {{ caller() }}
    </div>
</div>
{% endmacro %}

{# Usar con contenido #}
{% call card('Mi Tarjeta') %}
    <p>Este es el contenido de la tarjeta</p>
    <button class="btn btn-primary">Acción</button>
{% endcall %}
```

## Includes

### Include Simple

**templates/fragments/alert.peb:**

```pebble
{% if mensaje %}
<div class="alert alert-{{ tipo | default('info') }} alert-dismissible fade show">
    {{ mensaje }}
    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
</div>
{% endif %}
```

**Usar:**

```pebble
{% include "fragments/alert.peb" %}
```

### Include con Variables

```pebble
{% include "fragments/alert.peb" with {"mensaje": "Operación exitosa", "tipo": "success"} %}
```

### Include Condicional

```pebble
{% if usuario.esAdmin %}
    {% include "fragments/admin-menu.peb" %}
{% endif %}
```

## Integración con Datos de Sesión en Plantillas

Las sesiones HTTP permiten mantener información del usuario entre diferentes peticiones. En Pebble, puedes acceder a datos de sesión que el controller pasa al modelo.

### Mostrar Datos de Sesión desde el Controller

#### En el Controller

```java
@ControllerAdvice
public class GlobalControllerAdvice {
    
    @ModelAttribute("carritoCount")
    public int carritoCount(HttpSession session) {
        @SuppressWarnings("unchecked")
        Map<Long, ItemCarrito> carrito = 
            (Map<Long, ItemCarrito>) session.getAttribute("carrito");
        return carrito != null ? carrito.values().stream()
            .mapToInt(ItemCarrito::getCantidad).sum() : 0;
    }
    
    @ModelAttribute("preferenciaIdioma")
    public String preferenciaIdioma(HttpSession session) {
        String idioma = (String) session.getAttribute("idioma");
        return idioma != null ? idioma : "es";
    }
}
```

#### En la Plantilla Pebble

```pebble
{# Navbar con contador de carrito desde sesión #}
<nav class="navbar navbar-expand-lg">
    <ul class="navbar-nav">
        <li class="nav-item">
            <a class="nav-link" href="/carrito">
                <i class="bi bi-cart"></i> Carrito
                {% if carritoCount > 0 %}
                <span class="badge bg-danger">{{ carritoCount }}</span>
                {% endif %}
            </a>
        </li>
    </ul>
    
    {# Selector de idioma basado en preferencia de sesión #}
    <div class="dropdown">
        <button class="btn btn-link">
            {% if preferenciaIdioma == 'es' %}
            🇪🇸 Español
            {% else %}
            🇬🇧 English
            {% endif %}
        </button>
        <ul class="dropdown-menu">
            <li><a href="?lang=es">🇪🇸 Español</a></li>
            <li><a href="?lang=en">🇬🇧 English</a></li>
        </ul>
    </div>
</nav>
```

### Ejemplo Completo: Carrito de Compras con Sesión

#### Controller

```java
@Controller
@RequestMapping("/carrito")
public class CarritoController {
    
    @GetMapping("/ver")
    public String verCarrito(HttpSession session, Model model) {
        @SuppressWarnings("unchecked")
        Map<Long, ItemCarrito> carrito = 
            (Map<Long, ItemCarrito>) session.getAttribute("carrito");
        
        if (carrito == null) {
            carrito = new HashMap<>();
        }
        
        List<ItemCarrito> items = new ArrayList<>(carrito.values());
        double total = items.stream()
            .mapToDouble(item -> item.getProducto().getPrecio() * item.getCantidad())
            .sum();
        
        model.addAttribute("items", items);
        model.addAttribute("total", total);
        model.addAttribute("isEmpty", items.isEmpty());
        
        return "carrito/ver";
    }
}
```

#### Plantilla: carrito/ver.peb

```pebble
{% extends "layouts/base.peb" %}

{% block title %}Mi Carrito de Compras{% endblock %}

{% block content %}
<div class="container mt-4">
    <h1><i class="bi bi-cart-fill"></i> Mi Carrito</h1>
    
    {# Verificar si el carrito está vacío #}
    {% if isEmpty %}
    <div class="alert alert-info">
        <i class="bi bi-info-circle"></i>
        Tu carrito está vacío. 
        <a href="/productos" class="alert-link">Explorar productos</a>
    </div>
    {% else %}
    
    {# Tabla de productos en el carrito #}
    <div class="card">
        <div class="card-body">
            <table class="table">
                <thead>
                    <tr>
                        <th>Producto</th>
                        <th>Precio Unitario</th>
                        <th>Cantidad</th>
                        <th>Subtotal</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    {% for item in items %}
                    <tr>
                        <td>
                            <div class="d-flex align-items-center">
                                <img src="{{ item.producto.imagen }}" 
                                     width="60" 
                                     class="me-3 rounded">
                                <div>
                                    <strong>{{ item.producto.nombre }}</strong>
                                    <br>
                                    <small class="text-muted">
                                        {{ item.producto.categoria }}
                                    </small>
                                </div>
                            </div>
                        </td>
                        <td>{{ item.producto.precio | numberformat('0.00') }}€</td>
                        <td>
                            <form method="POST" 
                                  action="/carrito/actualizar/{{ item.producto.id }}" 
                                  class="d-flex align-items-center">
                                <input type="number" 
                                       name="cantidad" 
                                       value="{{ item.cantidad }}" 
                                       min="1" 
                                       max="99" 
                                       class="form-control form-control-sm me-2" 
                                       style="width: 70px;">
                                <button type="submit" 
                                        class="btn btn-sm btn-outline-primary">
                                    <i class="bi bi-arrow-repeat"></i>
                                </button>
                            </form>
                        </td>
                        <td>
                            <strong>{{ item.subtotal | numberformat('0.00') }}€</strong>
                        </td>
                        <td>
                            <a href="/carrito/eliminar/{{ item.producto.id }}" 
                               class="btn btn-sm btn-danger"
                               onclick="return confirm('¿Eliminar este producto?')">
                                <i class="bi bi-trash"></i>
                            </a>
                        </td>
                    </tr>
                    {% endfor %}
                </tbody>
            </table>
            
            {# Total del carrito #}
            <div class="row">
                <div class="col-md-6 offset-md-6">
                    <div class="card bg-light">
                        <div class="card-body">
                            <h5>Resumen del Pedido</h5>
                            <hr>
                            <div class="d-flex justify-content-between">
                                <span>Subtotal ({{ items | length }} items):</span>
                                <strong>{{ total | numberformat('0.00') }}€</strong>
                            </div>
                            <div class="d-flex justify-content-between">
                                <span>Envío:</span>
                                <strong>
                                    {% if total > 50 %}
                                    <span class="text-success">GRATIS</span>
                                    {% else %}
                                    {{ 5.99 | numberformat('0.00') }}€
                                    {% endif %}
                                </strong>
                            </div>
                            <hr>
                            <div class="d-flex justify-content-between">
                                <strong>Total:</strong>
                                <strong class="text-primary fs-4">
                                    {{ (total > 50 ? total : total + 5.99) | numberformat('0.00') }}€
                                </strong>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    {# Botones de acción #}
    <div class="d-flex justify-content-between mt-4">
        <a href="/productos" class="btn btn-secondary">
            <i class="bi bi-arrow-left"></i> Seguir Comprando
        </a>
        <div>
            <a href="/carrito/vaciar" 
               class="btn btn-warning me-2"
               onclick="return confirm('¿Vaciar todo el carrito?')">
                <i class="bi bi-trash"></i> Vaciar Carrito
            </a>
            <a href="/compra/finalizar" class="btn btn-success btn-lg">
                <i class="bi bi-check-circle"></i> Proceder al Pago
            </a>
        </div>
    </div>
    
    {% endif %}
</div>
{% endblock %}
```

### Mostrar Mensaje de Bienvenida con Datos de Sesión

```pebble
{# Mostrar nombre de usuario desde sesión #}
{% if usuarioNombre %}
<div class="alert alert-info">
    👋 Bienvenido de nuevo, <strong>{{ usuarioNombre }}</strong>!
    <a href="/auth/logout" class="btn btn-sm btn-outline-secondary ms-3">
        Cerrar Sesión
    </a>
</div>
{% endif %}

{# Mostrar última visita #}
{% if ultimaVisita %}
<p class="text-muted">
    Última visita: {{ ultimaVisita | date('dd/MM/yyyy HH:mm') }}
</p>
{% endif %}
```

### Condicionales Basados en Datos de Sesión

```pebble
{# Mostrar diferentes opciones según rol del usuario #}
{% if usuarioRol == 'ADMIN' %}
<div class="admin-panel">
    <h3>Panel de Administración</h3>
    <ul>
        <li><a href="/admin/usuarios">Gestionar Usuarios</a></li>
        <li><a href="/admin/productos">Gestionar Productos</a></li>
        <li><a href="/admin/estadisticas">Ver Estadísticas</a></li>
    </ul>
</div>
{% elseif usuarioRol == 'MODERATOR' %}
<div class="moderator-panel">
    <h3>Panel de Moderación</h3>
    <ul>
        <li><a href="/moderador/reportes">Ver Reportes</a></li>
        <li><a href="/moderador/comentarios">Moderar Comentarios</a></li>
    </ul>
</div>
{% else %}
<div class="user-panel">
    <h3>Mi Cuenta</h3>
    <ul>
        <li><a href="/perfil">Mi Perfil</a></li>
        <li><a href="/compras">Mis Compras</a></li>
        <li><a href="/productos/mis-productos">Mis Productos</a></li>
    </ul>
</div>
{% endif %}

{# Mostrar banner promocional solo si el usuario no lo ha cerrado #}
{% if not bannerCerrado %}
<div class="alert alert-warning alert-dismissible">
    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    <strong>¡Oferta especial!</strong> 20% de descuento en tu primera compra.
    <a href="/promociones">Ver ofertas</a>
</div>
{% endif %}
```

### Personalización de Vista Según Preferencias de Sesión

```pebble
{# Tema oscuro/claro basado en preferencia de sesión #}
<body class="{% if temaOscuro %}theme-dark{% else %}theme-light{% endif %}">

{# Vista de lista vs cuadrícula según preferencia #}
{% if vistaPreferida == 'grid' %}
<div class="row row-cols-1 row-cols-md-3 g-4">
    {% for producto in productos %}
    <div class="col">
        <div class="card">
            <img src="{{ producto.imagen }}" class="card-img-top">
            <div class="card-body">
                <h5>{{ producto.nombre }}</h5>
                <p>{{ producto.precio }}€</p>
            </div>
        </div>
    </div>
    {% endfor %}
</div>
{% else %}
<div class="list-group">
    {% for producto in productos %}
    <div class="list-group-item">
        <div class="d-flex">
            <img src="{{ producto.imagen }}" width="100" class="me-3">
            <div>
                <h5>{{ producto.nombre }}</h5>
                <p>{{ producto.descripcion | slice(0, 100) }}...</p>
                <strong>{{ producto.precio }}€</strong>
            </div>
        </div>
    </div>
    {% endfor %}
</div>
{% endif %}

{# Botones para cambiar vista #}
<div class="btn-group mb-3">
    <a href="/productos?vista=grid" 
       class="btn btn-outline-secondary {{ vistaPreferida == 'grid' ? 'active' : '' }}">
        <i class="bi bi-grid-3x3"></i> Cuadrícula
    </a>
    <a href="/productos?vista=list" 
       class="btn btn-outline-secondary {{ vistaPreferida == 'list' ? 'active' : '' }}">
        <i class="bi bi-list"></i> Lista
    </a>
</div>
```

### Notificaciones y Mensajes Flash desde Sesión

```pebble
{# Mostrar mensajes flash (almacenados temporalmente en sesión) #}
{% if mensaje %}
<div class="alert alert-success alert-dismissible fade show">
    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    <i class="bi bi-check-circle"></i> {{ mensaje }}
</div>
{% endif %}

{% if error %}
<div class="alert alert-danger alert-dismissible fade show">
    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    <i class="bi bi-exclamation-triangle"></i> {{ error }}
</div>
{% endif %}

{% if advertencia %}
<div class="alert alert-warning alert-dismissible fade show">
    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    <i class="bi bi-info-circle"></i> {{ advertencia }}
</div>
{% endif %}
```

## Internacionalización

### Configuración

**ConfiguracionI18n.java:**

```java
@Bean
public LocaleResolver localeResolver() {
    SessionLocaleResolver slr = new SessionLocaleResolver();
    slr.setDefaultLocale(new Locale("es"));
    return slr;
}
```

**messages_es.properties:**

```properties
producto.nombre=Nombre del Producto
producto.precio=Precio
producto.crear=Crear Producto
usuario.bienvenida=Bienvenido, {0}
carrito.vacio=Tu carrito está vacío
carrito.items=items en el carrito
```

**messages_en.properties:**

```properties
producto.nombre=Product Name
producto.precio=Price
producto.crear=Create Product
usuario.bienvenida=Welcome, {0}
carrito.vacio=Your cart is empty
carrito.items=items in cart
```

### Uso en Plantillas

```pebble
<h1>{{ i18n('producto.crear') }}</h1>

<label>{{ i18n('producto.nombre') }}</label>
<input type="text" name="nombre">

<label>{{ i18n('producto.precio') }}</label>
<input type="number" name="precio">

{# Con parámetros #}
<p>{{ i18n('usuario.bienvenida', usuario.nombre) }}</p>

{# En el carrito #}
{% if carritoCount > 0 %}
<span class="badge bg-primary">
    {{ carritoCount }} {{ i18n('carrito.items') }}
</span>
{% else %}
<p>{{ i18n('carrito.vacio') }}</p>
{% endif %}
```

### Selector de Idioma con Sesión

```pebble
<div class="dropdown">
    <button class="btn btn-link dropdown-toggle" data-bs-toggle="dropdown">
        <i class="bi bi-globe"></i>
        {% if idiomaActual == 'es' %}
        Español
        {% else %}
        English
        {% endif %}
    </button>
    <ul class="dropdown-menu">
        <li>
            <a class="dropdown-item {{ idiomaActual == 'es' ? 'active' : '' }}" 
               href="?lang=es">
                🇪🇸 Español
            </a>
        </li>
        <li>
            <a class="dropdown-item {{ idiomaActual == 'en' ? 'active' : '' }}" 
               href="?lang=en">
                🇬🇧 English
            </a>
        </li>
    </ul>
</div>
```

## Best Practices

### 1. Estructura Organizada

```
templates/
├── layouts/           # Layouts base
├── fragments/         # Componentes pequeños
├── macros/           # Macros reutilizables
├── [modulo]/         # Por módulo (productos, usuarios, etc.)
└── errors/           # Páginas de error
```

### 2. Naming Conventions

```pebble
{# Usar nombres descriptivos #}
{% block mainContent %}...{% endblock %}    ✅
{% block b1 %}...{% endblock %}             ❌

{# Variables en camelCase #}
{{ userName }}                              ✅
{{ user_name }}                             ❌
```

### 3. Separar Lógica de Presentación

❌ **Mal:**

```pebble
{% set total = 0 %}
{% for item in carrito %}
    {% set total = total + (item.precio * item.cantidad) %}
{% endfor %}
{{ total }}
```

✅ **Bien:**

```java
// En el Controller
model.addAttribute("total", carritoServicio.calcularTotal());
```

```pebble
{{ total }}
```

### 4. Usar Macros para Componentes Repetitivos

✅ **Bien:**

```pebble
{% import "macros/cards.peb" %}

{% for producto in productos %}
    {{ productoCard(producto) }}
{% endfor %}
```

### 5. Escapar HTML por Defecto

```pebble
{# Auto-escapado - Seguro #}
{{ comentario }}                            ✅

{# Raw - Solo si confías en el contenido #}
{{ htmlSeguro | raw }}                      ⚠️
```

### 6. Comentar Plantillas Complejas

```pebble
{#
    Tarjeta de producto con:
    - Imagen
    - Nombre y descripción
    - Precio
    - Botón de compra
#}
<div class="producto-card">
    ...
</div>
```

### 7. Usar Condicionales Simples

❌ **Mal:**

```pebble
{% if usuario %}
    {% if usuario.rol == 'ADMIN' %}
        {% if usuario.activo %}
            <a href="/admin">Panel</a>
        {% endif %}
    {% endif %}
{% endif %}
```

✅ **Bien:**

```pebble
{% if usuario and usuario.rol == 'ADMIN' and usuario.activo %}
    <a href="/admin">Panel</a>
{% endif %}
```

## Recursos Adicionales

- [Pebble Documentation](https://pebbletemplates.io/)
- [Pebble GitHub](https://github.com/PebbleTemplates/pebble)
- [Spring Boot + Pebble](https://github.com/PebbleTemplates/pebble-spring-boot-starter)

---

**Última actualización:** Enero 2025  
**Autor:** José Luis González Sánchez

## Casos de Uso Reales del Proyecto

### 1. Verificación de Nulos con `is not null`

❌ **Problema:** Comparar directamente objetos causa PebbleException

```pebble
{% if producto.compra %}  {# ERROR: Wrong operand type #}
    <span>Vendido</span>
{% endif %}
```

✅ **Solución:** Usar `is not null` para verificar objetos

```pebble
{% if producto.compra is not null %}
    <span class="badge bg-success">Vendido</span>
{% else %}
    <span class="badge bg-warning">Disponible</span>
{% endif %}
```

### 2. CSRF Tokens en Formularios

```pebble
<form method="post" action="/app/misproductos/editar/submit">
    {# IMPORTANTE: Incluir token CSRF para evitar error 403 #}
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    
    <input type="text" name="nombre" value="{{ producto.nombre | default('') }}">
    <button type="submit">Guardar</button>
</form>
```

### 3. Rutas Dinámicas y Parámetros

```pebble
{# Factura con ID dinámico #}
<a href="/app/miscompras/factura/{{ compra.id }}" class="btn btn-primary">
    Ver Factura #{{ compra.id }}
</a>

{# PDF con ruta correcta #}
<a href="/app/miscompras/factura/{{ compra.id }}/pdf" class="btn btn-danger">
    <i class="bi bi-file-pdf"></i> Descargar PDF
</a>
```

### 4. Cálculos de IVA en Facturas

```pebble
<table class="table">
    <tr>
        <td>Subtotal (sin IVA):</td>
        <td>{{ (total / 1.21) | formatPrice }}</td>
    </tr>
    <tr>
        <td>IVA (21%):</td>
        <td>{{ (total - (total / 1.21)) | formatPrice }}</td>
    </tr>
    <tr>
        <td><strong>TOTAL:</strong></td>
        <td><strong>{{ total | formatPrice }}</strong></td>
    </tr>
</table>
```

### 5. Iteración con Filtro Condicional

```pebble
{# Mostrar solo productos de una compra específica #}
{% for producto in productos %}
    {% if producto.compra.id == compra.id %}
    <li class="list-group-item">
        <span>{{ producto.nombre }}</span>
        <span class="badge">{{ producto.precio | formatPrice }}</span>
    </li>
    {% endif %}
{% endfor %}
```

### 6. Estados Dinámicos con Íconos

```pebble
{# Sistema de favoritos con estado dinámico #}
<button onclick="toggleFavorite({{ producto.id }})">
    <i class="bi bi-heart{% if isFavorite %}-fill{% endif %}" id="favoriteIcon"></i>
    <span id="favoriteText">
        {% if isFavorite %}Quitar de favoritos{% else %}Añadir a favoritos{% endif %}
    </span>
</button>
```

### 7. Renderizado de Estrellas de Rating

```pebble
{# Mostrar rating promedio con estrellas #}
<div class="rating">
    {% if averageRating > 0 %}
    <div class="text-warning">
        {% for i in range(1, 6) %}
            {% if i <= averageRating %}
            <i class="bi bi-star-fill"></i>
            {% elseif i - averageRating < 1 %}
            <i class="bi bi-star-half"></i>
            {% else %}
            <i class="bi bi-star"></i>
            {% endif %}
        {% endfor %}
    </div>
    <span>{{ averageRating | number_format(1) }} ({{ ratingCount }} valoraciones)</span>
    {% else %}
    <span class="text-muted">Sin valoraciones</span>
    {% endif %}
</div>
```

### 8. Condicionales de Autenticación y Roles

```pebble
{# Mostrar opciones según autenticación y rol #}
{% if isAuthenticated %}
    {% if currentUser.email != producto.propietario.email %}
        <a href="/app/carrito/add/{{ producto.id }}" class="btn btn-success">
            <i class="bi bi-cart-plus"></i> Comprar
        </a>
    {% endif %}
    
    {% if isAdmin %}
        <a href="/admin/productos/eliminar/{{ producto.id }}" class="btn btn-danger">
            Eliminar
        </a>
    {% endif %}
{% else %}
    <a href="/auth/login" class="btn btn-primary">
        Inicia sesión para comprar
    </a>
{% endif %}
```

### 9. Listas Vacías con Mensaje Alternativo

```pebble
{# Mis compras con mensaje si está vacío #}
{% if miscompras is empty %}
<div class="alert alert-info">
    <p>No has realizado ninguna compra todavía.</p>
    <a href="/" class="btn btn-primary">Ir a comprar</a>
</div>
{% else %}
<div class="row">
    {% for compra in miscompras %}
    <div class="card">
        <h5>Compra #{{ compra.id }}</h5>
        <p>Fecha: {{ compra.fechaCompra | formatDate }}</p>
    </div>
    {% endfor %}
</div>
{% endif %}
```

### 10. Includes de Fragments

```pebble
<!DOCTYPE html>
<html lang="es">
{# Incluir head común #}
{% include "fragments/head" %}

<body>
    {# Incluir navbar #}
    {% include "fragments/navbar" %}
    
    <main>
        {# Contenido específico de la página #}
    </main>
    
    {# Incluir footer #}
    {% include "fragments/footer" %}
</body>
</html>
```

### 11. Valores por Defecto en Formularios

```pebble
{# Formulario que funciona para crear Y editar #}
<form method="post" action="{% if producto.id != 0 %}/editar/submit{% else %}/nuevo/submit{% endif %}">
    <input type="hidden" name="id" value="{{ producto.id | default(0) }}">
    
    <input type="text" 
           name="nombre" 
           value="{{ producto.nombre | default('') }}" 
           required>
    
    <input type="number" 
           step="0.01" 
           name="precio" 
           value="{{ producto.precio | default(0) }}" 
           min="0.01" 
           required>
    
    <button type="submit">
        {% if producto.id != 0 %}Actualizar{% else %}Crear{% endif %}
    </button>
</form>
```

### 12. Badges Condicionales con Clases Dinámicas

```pebble
{# Badge que cambia de color según el estado #}
{% if producto.compra is not null %}
    <span class="badge bg-success">Vendido</span>
{% else %}
    <span class="badge bg-warning">Disponible</span>
{% endif %}

{# Badge de carrito con contador dinámico #}
<a href="/app/carrito">
    <span class="badge bg-danger">{{ items_carrito | default(0) }}</span>
    <i class="bi bi-cart"></i> Carrito
</a>
```

### 13. JavaScript Embebido con Datos de Pebble

```pebble
<script>
// Cargar valoraciones al iniciar la página
{% if isAuthenticated %}
document.addEventListener('DOMContentLoaded', function() {
    loadRatings({{ producto.id }});
});
{% endif %}

// Función con parámetros desde Pebble
function toggleFavorite(productoId) {
    fetch(`/app/favoritos/add/${productoId}`, {
        method: 'POST'
    }).then(response => response.json())
      .then(data => console.log(data));
}
</script>
```

### 14. Estilos Inline Dinámicos

```pebble
{# Barra de progreso según porcentaje #}
<div class="progress">
    <div class="progress-bar" 
         style="width: {{ porcentaje }}%"
         role="progressbar">
        {{ porcentaje }}%
    </div>
</div>

{# Imagen con fallback #}
<img src="{% if producto.imagen is empty %}https://placehold.it/300x200{% else %}{{ producto.imagen }}{% endif %}"
     alt="{{ producto.nombre }}"
     class="img-fluid">
```

### 15. Uso de Variables de Sesión y ModelAttributes

```pebble
{# Variables inyectadas automáticamente por @ModelAttribute #}
<p>Total del carrito: {{ total_carrito | formatPrice }}</p>
<p>Items en carrito: {{ items_carrito }}</p>

{# Información del usuario autenticado (GlobalControllerAdvice) #}
<p>Bienvenido, {{ username | default('Usuario') }}</p>
<p>Rol: {{ userRole | default('USER') }}</p>

{# Variables de sesión #}
{% if session.getAttribute('mensaje') %}
<div class="alert alert-info">{{ session.getAttribute('mensaje') }}</div>
{% endif %}
```

## Filtros Personalizados Implementados

### formatPrice
```pebble
{{ 99.99 | formatPrice }}  {# Salida: 99,99 € #}
{{ total | formatPrice }}   {# Formatea según locale #}
```

### formatDate
```pebble
{{ compra.fechaCompra | formatDate }}  {# Salida: 15/01/2025 #}
```

### number_format
```pebble
{{ averageRating | number_format(1) }}  {# Salida: 4.5 #}
{{ precio | number_format(2) }}         {# Salida: 99.95 #}
```

## Errores Comunes y Soluciones

### Error: "Wrong operand(s) type in conditional expression"
```pebble
❌ {% if objeto %}           {# Error con objetos #}
✅ {% if objeto is not null %}  {# Correcto #}
```

### Error: "Invalid CSRF token"
```pebble
❌ <form method="post">  {# Falta token #}
✅ <form method="post">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
</form>
```

### Error: Variable no encontrada
```pebble
❌ {{ precio }}              {# Error si no existe #}
✅ {{ precio | default(0) }}  {# Con valor por defecto #}
```

### Error: Ruta 404
```pebble
❌ <a href="/app/compra/factura/{{ id }}/pdf">  {# Ruta incorrecta #}
✅ <a href="/app/miscompras/factura/{{ id }}/pdf">  {# Ruta correcta #}
```

---

**Actualizado con ejemplos reales:** Noviembre 2025  
**Proyecto:** WalaSpringBoot Marketplace
