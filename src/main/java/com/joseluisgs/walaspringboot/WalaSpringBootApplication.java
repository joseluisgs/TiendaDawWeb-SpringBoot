package com.joseluisgs.walaspringboot;

import com.joseluisgs.walaspringboot.modelos.Producto;
import com.joseluisgs.walaspringboot.modelos.Usuario;
import com.joseluisgs.walaspringboot.servicios.ProductoServicio;
import com.joseluisgs.walaspringboot.servicios.UsuarioServicio;
import com.joseluisgs.walaspringboot.upload.StorageProperties;
import com.joseluisgs.walaspringboot.upload.StorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.List;

@EnableConfigurationProperties(StorageProperties.class)
@EnableCaching
@SpringBootApplication
public class WalaSpringBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(WalaSpringBootApplication.class, args);
    }

    /**
     * Inicializa datos de prueba SOLO en perfil de desarrollo
     * En producción (perfil prod) este bean no se crea
     */
    @Bean
    @org.springframework.context.annotation.Profile("dev")
    public CommandLineRunner initData(UsuarioServicio usuarioServicio, ProductoServicio productoServicio) {
        return args -> {
            System.out.println("🔧 PERFIL DEV: Cargando datos de prueba...");
            
            // Crear usuarios con diferentes roles
            Usuario admin = new Usuario("Admin", "Administrador", null, "admin@walaspringboot.com", "admin", "ADMIN");
            admin = usuarioServicio.registrar(admin);

            Usuario usuario = new Usuario("Prueba", "Probando Mucho", null, "prueba@prueba.com", "prueba", "USER");
            usuario = usuarioServicio.registrar(usuario);

            Usuario moderador = new Usuario("Moderador", "Usuario", null, "moderador@walaspringboot.com", "moderador", "MODERATOR");
            moderador = usuarioServicio.registrar(moderador);

            Usuario usuario2 = new Usuario("Otro", "Usuario", null, "otro@otro.com", "otro", "USER");
            usuario2 = usuarioServicio.registrar(usuario2);

            System.out.println("✅ Usuarios de prueba creados: admin, prueba, moderador, otro");

            // Productos actualizados 2024-2025
            List<Producto> listado = Arrays.asList(
                new Producto("iPhone 15 Pro Max", 1199.0f,
                    "https://store.storeimages.cdn-apple.com/4982/as-images.apple.com/is/iphone-15-pro-finish-select-202309-6-7inch-naturaltitanium?wid=5120&hei=2880&fmt=p-jpg&qlt=80&.v=1692895702708",
                    "El iPhone más avanzado de Apple con chip A17 Pro, titanio aeroespacial y cámara de 48MP. Estado impecable, apenas usado.",
                    "Smartphones", usuario),
                new Producto("Samsung Galaxy S24 Ultra", 1099.0f,
                    "https://images.samsung.com/is/image/samsung/p6pim/es/2401/gallery/es-galaxy-s24-s928-sm-s928bztgeub-thumb-539573268",
                    "Flagship de Samsung con S Pen integrado, pantalla Dynamic AMOLED 2X y cámara de 200MP. Como nuevo, con todos los accesorios.",
                    "Smartphones", usuario),
                new Producto("Google Pixel 8 Pro", 899.0f,
                    "https://lh3.googleusercontent.com/wEt6FZKrZ8kYKMvDNBrY9bMh4hhRr4vQbqFqxnFTOqfB0Ku7fZ8vR5qF5qR5qF5qR5qF5qR5qF5qR5qF5qR5qF5qR5qF5qR5qF5qR5qF5qR5qF5qR5qF5qR5qF5qF=w200",
                    "El mejor teléfono para fotografía con IA de Google Tensor G3. Pantalla LTPO de 120Hz. Excelente estado de conservación.",
                    "Smartphones", usuario2),
                new Producto("MacBook Pro M3", 1999.0f,
                    "https://store.storeimages.cdn-apple.com/4982/as-images.apple.com/is/mbp14-m3-pro-max-spaceblack-select-202310?wid=904&hei=840&fmt=jpeg&qlt=90&.v=1697311054290",
                    "MacBook Pro 14 con chip M3 Max, 36GB RAM, 1TB SSD. Perfecto para profesionales creativos. Garantía Apple vigente.",
                    "Laptops", admin),
                new Producto("AirPods Pro 2ª Gen", 249.0f,
                    "https://store.storeimages.cdn-apple.com/4982/as-images.apple.com/is/MQD83?wid=1144&hei=1144&fmt=jpeg&qlt=90&.v=1660803972361",
                    "Auriculares con cancelación de ruido adaptativa y audio espacial personalizado. Nuevos en caja sellada.",
                    "Audio", moderador),
                new Producto("Steam Deck OLED", 549.0f,
                    "https://cdn.cloudflare.steamstatic.com/steamdeck/images/steamdeck_unity_share.jpg",
                    "Consola portátil con pantalla OLED HDR de 7.4 pulgadas. Modelo de 512GB. Gaming en cualquier lugar. Como nuevo.",
                    "Gaming", usuario2)
            );

            listado.forEach(productoServicio::insertar);
            
            System.out.println("✅ " + listado.size() + " productos de prueba creados");
            System.out.println("🔧 Datos de prueba cargados correctamente");
        };
    }
}
