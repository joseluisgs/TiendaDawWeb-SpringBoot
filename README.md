# 🛒 WalaDaw - Tienda de Segunda Mano 2025

[![Java](https://img.shields.io/badge/Java-21-orange)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.0-brightgreen)](https://spring.io/projects/spring-boot)
[![Gradle](https://img.shields.io/badge/Gradle-8.11.1-blue)](https://gradle.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

Migración completa y modernizada del proyecto [WalaSpringBoot2020](https://github.com/joseluisgs/WalaSpringBoot2020) con las últimas tecnologías 2025. Una aplicación web de comercio electrónico de segunda mano con características avanzadas de seguridad, internacionalización y gestión de usuarios.

## 📑 Tabla de Contenidos

- [Características](#-características)
- [Tecnologías](#-tecnologías)
- [Requisitos](#-requisitos)
- [Instalación](#-instalación-y-configuración)
- [Uso](#-uso)
- [Documentación](#-documentación)
- [Arquitectura](#-arquitectura)
- [Migración](#-migración-desde-walaspringboot2020)
- [Contribución](#-contribución)
- [Autor](#-autor)

## ✨ Características

### Funcionalidades Principales

- 🛍️ **Marketplace de Segunda Mano**: Compra y vende productos usados
- 🔐 **Sistema de Roles**: ADMIN, USER, MODERATOR con permisos diferenciados
- 🌍 **Internacionalización**: Soporte completo para Español e Inglés
- 📧 **Notificaciones por Email**: Confirmación automática asíncrona de compras con templates HTML
- 📊 **Dashboard Administrativo**: Estadísticas y gráficos con Chart.js
- 🔍 **Búsqueda Avanzada**: Filtros por nombre, categoría y precio
- 🖼️ **Gestión de Imágenes**: Subida, validación y redimensionado automático
- 📱 **Responsive Design**: Bootstrap 5.3 optimizado para todos los dispositivos
- ⚡ **Cache Inteligente**: Mejora de rendimiento con Spring Cache
- 📄 **Generación de PDFs**: Facturas automáticas con cálculo de IVA y diseño profesional
- ❤️ **Sistema de Favoritos**: Añade productos a favoritos con AJAX
- ⭐ **Valoraciones y Ratings**: Sistema completo de reviews con estrellas y comentarios
- 🛡️ **Seguridad CSRF**: Protección completa contra ataques Cross-Site Request Forgery

### Productos 2024-2025

La aplicación incluye productos actuales y relevantes:

- 📱 **Smartphones**: iPhone 15 Pro Max, Samsung Galaxy S24 Ultra, Google Pixel 8 Pro
- 💻 **Laptops**: MacBook Pro M3
- 🎧 **Audio**: AirPods Pro 2ª Generación
- 🎮 **Gaming**: Steam Deck OLED

## 🚀 Tecnologías

### Backend

| Tecnología | Versión | Descripción |
|------------|---------|-------------|
| **Java** | 21 (LTS) | Lenguaje de programación moderno y robusto |
| **Spring Boot** | 3.4.0 | Framework para aplicaciones empresariales |
| **Spring Security** | 6.x | Autenticación y autorización con configuración 2025 |
| **Spring Data JPA** | - | Persistencia de datos con Hibernate |
| **Spring Cache** | - | Sistema de caché para mejorar rendimiento |
| **Spring Mail** | - | Envío de emails de confirmación |
| **Gradle** | 8.11.1 | Build tool con Kotlin DSL |
| **H2 Database** | - | Base de datos embebida para desarrollo |

### Frontend

| Tecnología | Versión | Descripción |
|------------|---------|-------------|
| **Pebble Template Engine** | 3.2.2 | Motor de plantillas moderno y eficiente |
| **Bootstrap** | 5.3 (CDN) | Framework CSS responsive |
| **Bootstrap Icons** | 1.11.3 | Iconografía moderna |
| **Chart.js** | 4.4.0 | Gráficos interactivos para el dashboard |

### Seguridad

- ✅ **Spring Security 6** con configuración moderna
- ✅ **SecurityFilterChain** (no WebSecurityConfigurerAdapter deprecated)
- ✅ **BCrypt Password Encoder** para encriptación de contraseñas
- ✅ **CSRF Protection** habilitada
- ✅ **Method-level Security** con @PreAuthorize
- ✅ **Role-Based Access Control** (RBAC)

## 📋 Requisitos

### Software Necesario

- **Java JDK 21 o superior** ([Descargar](https://adoptium.net/))
- **Git** ([Descargar](https://git-scm.com/))
- **IntelliJ IDEA** (recomendado) o cualquier IDE Java ([Descargar](https://www.jetbrains.com/idea/download/))

### Opcional

- **Postman** para probar endpoints (si se agregan APIs REST)
- **Cliente SMTP** configurado para pruebas de email

## 🔧 Instalación y Configuración

### 1. Clonar el Repositorio

```bash
git clone https://github.com/joseluisgs/PruebaWalaSpringBoot.git
cd PruebaWalaSpringBoot
```

### 2. Configuración en IntelliJ IDEA

#### Opción A: Importar Proyecto

1. Abre IntelliJ IDEA
2. `File` → `Open...`
3. Selecciona la carpeta del proyecto
4. IntelliJ detectará automáticamente el proyecto Gradle
5. Espera a que se descarguen las dependencias

#### Opción B: Clonar desde IntelliJ

1. Abre IntelliJ IDEA
2. `Git` → `Clone...`
3. Pega la URL: `https://github.com/joseluisgs/PruebaWalaSpringBoot.git`
4. Haz clic en `Clone`

### 3. Configurar el JDK

1. `File` → `Project Structure...` → `Project`
2. En `SDK`, selecciona Java 21 o superior
3. Si no está disponible, haz clic en `Add SDK` → `Download JDK...`
4. Selecciona Eclipse Temurin (AdoptOpenJDK) versión 21

### 4. Compilar el Proyecto

```bash
./gradlew build
```

En Windows:
```cmd
gradlew.bat build
```

### 5. Ejecutar la Aplicación

#### Desde Terminal:

```bash
./gradlew bootRun
```

#### Desde IntelliJ IDEA:

1. Abre `WalaSpringBootApplication.java`
2. Haz clic derecho → `Run 'WalaSpringBootApplication'`
3. O usa el botón verde de play ▶️

La aplicación estará disponible en: **http://localhost:8080**

### 6. Configuración de Email (Opcional)

Para habilitar el envío de emails de confirmación:

1. Abre `src/main/resources/application.properties`
2. Configura las propiedades SMTP:

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=tu-email@gmail.com
spring.mail.password=tu-app-password
```

Para Gmail, necesitas generar una [contraseña de aplicación](https://support.google.com/accounts/answer/185833).

## 📖 Uso

### Usuarios de Prueba

La aplicación inicializa automáticamente con usuarios de diferentes roles:

| Email | Password | Rol | Descripción |
|-------|----------|-----|-------------|
| `admin@waladaw.com` | `admin` | **ADMIN** | Acceso total al dashboard y gestión |
| `prueba@prueba.com` | `prueba` | **USER** | Usuario estándar con sus productos |
| `moderador@waladaw.com` | `moderador` | **MODERATOR** | Permisos intermedios |
| `otro@otro.com` | `otro` | **USER** | Otro usuario estándar |

### Flujo de Usuario Normal

1. **Navegación**: Explora los productos disponibles en la página principal
2. **Filtrado**: Usa los filtros por categoría, precio y búsqueda
3. **Login**: Inicia sesión con un usuario de prueba
4. **Compra**: Añade productos al carrito y finaliza la compra
5. **Mis Compras**: Revisa tus compras en el historial
6. **Mis Productos**: Gestiona tus propios productos

### Flujo de Administrador

1. **Login**: Inicia sesión como `admin@waladaw.com`
2. **Dashboard**: Accede al panel de control desde el menú superior
3. **Estadísticas**: Visualiza métricas de productos, usuarios y compras
4. **Gestión de Usuarios**: Administra los usuarios registrados
5. **Gestión de Productos**: Supervisa todos los productos del sistema

### Cambio de Idioma

1. Haz clic en el icono del globo 🌍 en la barra de navegación
2. Selecciona Español o English
3. La interfaz cambiará automáticamente

### Consola H2 (Base de Datos)

Accede a la consola de base de datos en modo desarrollo:

- URL: **http://localhost:8080/h2-console**
- JDBC URL: `jdbc:h2:./waladaw`
- Usuario: `sa`
- Contraseña: *(vacío)*

## 📚 Documentación

### Tutoriales Incluidos

- **[SpringMVC.md](SpringMVC.md)**: Tutorial completo de Spring MVC con ejemplos de sesiones HTTP, Model vs ModelAndView, y casos prácticos paso a paso
- **[Pebble.md](Pebble.md)**: Guía exhaustiva del motor de plantillas Pebble con integración de datos de sesión y ejemplos avanzados
- **[SECURITY.md](SECURITY.md)**: Resumen completo de seguridad del proyecto en español

### Configuración de Perfiles

La aplicación soporta dos perfiles claramente diferenciados para desarrollo y producción:

#### Perfil DEV (Desarrollo)

```properties
spring.profiles.active=dev
```

**Configuración:**
- 💾 **Base de datos**: H2 en memoria para desarrollo rápido
- 🔄 **Datos de prueba**: Se cargan automáticamente al iniciar (usuarios, productos)
- 🗑️ **Storage**: El directorio de uploads se **limpia al iniciar** la aplicación
- 🐛 **Logging**: Nivel DEBUG para depuración detallada
- ⚡ **Caché**: Deshabilitado para hot-reload
- 🔧 **Consola H2**: Habilitada en http://localhost:8080/h2-console
- 📝 **SQL Queries**: Visibles en la consola

**Ideal para:**
- Desarrollo local
- Pruebas unitarias y de integración
- Aprendizaje y experimentación
- No requiere configuración adicional

#### Perfil PROD (Producción)

```properties
spring.profiles.active=prod
```

**Configuración:**
- 💾 **Base de datos**: H2 en fichero (`waladaw.mv.db`) para persistencia real
- 🚫 **Datos de prueba**: **NO se cargan** automáticamente
- 📁 **Storage**: El directorio de uploads **NO se borra**, mantiene los archivos
- 📊 **Logging**: Nivel WARN optimizado para producción
- ⚡ **Caché**: Habilitado para mejor rendimiento
- 🔒 **Consola H2**: Deshabilitada por seguridad
- 📝 **SQL Queries**: Ocultas

**Ideal para:**
- Despliegue en servidor
- Datos reales que deben persistir
- Mejor rendimiento
- Mayor seguridad

## 🏗️ Arquitectura

### Estructura del Proyecto

```
src/main/
├── java/com/joseluisgs/walaspringboot/
│   ├── configuracion/          # Configuraciones (i18n, validación, auditoría)
│   ├── controladores/          # Controllers MVC
│   │   ├── AdminController     # Dashboard administrativo
│   │   ├── CompraController    # Gestión de compras
│   │   ├── LoginController     # Autenticación
│   │   ├── ProductoController  # CRUD de productos
│   │   └── ZonaPublicaController # Área pública
│   ├── modelos/                # Entities JPA
│   │   ├── Compra             # Compras realizadas
│   │   ├── Producto           # Productos del marketplace
│   │   └── Usuario            # Usuarios del sistema
│   ├── repositorios/           # Repositories JPA
│   ├── seguridad/              # Security configuration
│   │   ├── SeguridadConfig    # Spring Security setup
│   │   └── UserDetailsServiceImpl
│   ├── servicios/              # Business logic
│   │   ├── CompraServicio
│   │   ├── EmailService       # Envío de emails
│   │   ├── ImageService       # Procesamiento de imágenes
│   │   ├── ProductoServicio
│   │   └── UsuarioServicio
│   ├── upload/                 # File storage
│   ├── utilidades/             # Utilities (PDF generation)
│   └── validacion/             # Custom validators
└── resources/
    ├── application.properties          # Configuración principal
    ├── application-dev.properties      # Perfil desarrollo
    ├── application-prod.properties     # Perfil producción
    ├── messages_es.properties          # i18n Español
    ├── messages_en.properties          # i18n Inglés
    ├── static/                         # Recursos estáticos
    │   ├── css/
    │   └── images/
    └── templates/                      # Plantillas Pebble
        ├── admin/                      # Templates admin
        │   ├── dashboard.peb
        │   ├── productos.peb
        │   └── usuarios.peb
        ├── app/                        # Templates privadas
        │   ├── compra/
        │   └── producto/
        ├── fragments/                  # Componentes reutilizables
        │   ├── head.peb
        │   ├── navbar.peb
        │   └── footer.peb
        ├── index.peb                   # Página principal
        └── login.peb                   # Login/Registro
```

### Patrón de Diseño

La aplicación sigue el patrón **MVC (Model-View-Controller)**:

- **Model**: Entidades JPA (Producto, Usuario, Compra)
- **View**: Plantillas Pebble (.peb)
- **Controller**: Controllers Spring MVC

Además implementa:

- **Repository Pattern**: Para acceso a datos
- **Service Layer**: Para lógica de negocio
- **Dependency Injection**: Con Spring IoC

## 🎯 Casos de Uso Principales

### Para Estudiantes de DAW

Esta aplicación está diseñada específicamente para estudiantes de **Desarrollo de Aplicaciones Web** que están aprendiendo Spring Boot. Incluye ejemplos prácticos de:

1. **🛒 E-commerce Completo**: Aprende a crear un marketplace de productos de segunda mano desde cero
   - Listado y búsqueda de productos
   - Carrito de compras con sesiones HTTP
   - Sistema de compra y confirmación por email
   - Gestión de productos propios

2. **🔐 Autenticación y Autorización**: Implementa seguridad real con Spring Security 6
   - Sistema de login/registro
   - Control de acceso basado en roles (ADMIN, MODERATOR, USER)
   - Protección de endpoints y vistas

3. **📊 Dashboard Administrativo**: Crea paneles de control con estadísticas
   - Gráficos interactivos con Chart.js
   - Gestión de usuarios y productos
   - Visualización de métricas del sistema

4. **🌍 Internacionalización**: Desarrolla aplicaciones multiidioma
   - Soporte español e inglés
   - Cambio dinámico de idioma
   - Mensajes localizados

5. **🖼️ Gestión de Archivos**: Maneja subida y procesamiento de imágenes
   - Validación de archivos
   - Redimensionado automático
   - Almacenamiento seguro

6. **📧 Notificaciones por Email**: Envía emails automáticos
   - Confirmación de compras
   - Plantillas HTML profesionales
   - Integración con servicios SMTP

### Características Técnicas Implementadas

1. ✅ **Arquitectura MVC completa**: Separación clara de responsabilidades
2. ✅ **Gestión de sesiones HTTP**: Carrito de compras persistente
3. ✅ **Motor de plantillas Pebble**: Vistas dinámicas y rápidas
4. ✅ **Spring Data JPA**: Persistencia de datos con Hibernate
5. ✅ **Validación de formularios**: Bean Validation y validadores personalizados
6. ✅ **Sistema de cache**: Mejora del rendimiento con @Cacheable
7. ✅ **Logging configurado**: Diferentes niveles según el entorno
8. ✅ **Perfiles de configuración**: Separación dev/prod
9. ✅ **Bootstrap 5**: Diseño responsive y moderno
10. ✅ **Base de datos H2**: Fácil desarrollo y pruebas

## 🤝 Contribución

Las contribuciones son bienvenidas. Para cambios importantes:

1. Fork el repositorio
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles.

## Autor

Codificado con :sparkling_heart: por [José Luis González Sánchez](https://twitter.com/JoseLuisGS_)

[![Twitter](https://img.shields.io/twitter/follow/JoseLuisGS_?style=social)](https://twitter.com/JoseLuisGS_)
[![GitHub](https://img.shields.io/github/followers/joseluisgs?style=social)](https://github.com/joseluisgs)
[![GitHub](https://img.shields.io/github/stars/joseluisgs?style=social)](https://github.com/joseluisgs)

### Contacto

<p>
  Cualquier cosa que necesites házmelo saber por si puedo ayudarte 💬.
</p>
<p>
 <a href="https://joseluisgs.dev" target="_blank">
        <img src="https://joseluisgs.github.io/img/favicon.png" 
    height="30">
    </a>  &nbsp;&nbsp;
    <a href="https://github.com/joseluisgs" target="_blank">
        <img src="https://distreau.com/github.svg" 
    height="30">
    </a> &nbsp;&nbsp;
        <a href="https://twitter.com/JoseLuisGS_" target="_blank">
        <img src="https://i.imgur.com/U4Uiaef.png" 
    height="30">
    </a> &nbsp;&nbsp;
    <a href="https://www.linkedin.com/in/joseluisgonsan" target="_blank">
        <img src="https://upload.wikimedia.org/wikipedia/commons/thumb/c/ca/LinkedIn_logo_initials.png/768px-LinkedIn_logo_initials.png" 
    height="30">
    </a>  &nbsp;&nbsp;
    <a href="https://g.dev/joseluisgs" target="_blank">
        <img loading="lazy" src="https://googlediscovery.com/wp-content/uploads/google-developers.png" 
    height="30">
    </a>  &nbsp;&nbsp;
<a href="https://www.youtube.com/@joseluisgs" target="_blank">
        <img loading="lazy" src="https://upload.wikimedia.org/wikipedia/commons/e/ef/Youtube_logo.png" 
    height="30">
    </a>  
</p>

## Licencia de uso

Este repositorio y todo su contenido está licenciado bajo licencia **Creative Commons**, si desea saber más, vea
la [LICENSE](https://joseluisgs.dev/docs/license/). Por favor si compartes, usas o modificas este proyecto cita a su
autor, y usa las mismas condiciones para su uso docente, formativo o educativo y no comercial.

<a rel="license" href="http://creativecommons.org/licenses/by-nc-sa/4.0/"><img alt="Licencia de Creative Commons" style="border-width:0" src="https://i.creativecommons.org/l/by-nc-sa/4.0/88x31.png" /></a><br /><span xmlns:dct="http://purl.org/dc/terms/" property="dct:title">
JoseLuisGS</span>
by <a xmlns:cc="http://creativecommons.org/ns#" href="https://joseluisgs.dev/" property="cc:attributionName" rel="cc:attributionURL">
José Luis González Sánchez</a> is licensed under
a <a rel="license" href="http://creativecommons.org/licenses/by-nc-sa/4.0/">Creative Commons
Reconocimiento-NoComercial-CompartirIgual 4.0 Internacional License</a>.<br />Creado a partir de la obra
en <a xmlns:dct="http://purl.org/dc/terms/" href="https://github.com/joseluisgs" rel="dct:source">https://github.com/joseluisgs</a>.
