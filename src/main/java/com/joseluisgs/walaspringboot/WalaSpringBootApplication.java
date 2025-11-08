package com.joseluisgs.walaspringboot;

import com.joseluisgs.walaspringboot.models.Product;
import com.joseluisgs.walaspringboot.models.User;
import com.joseluisgs.walaspringboot.services.ProductService;
import com.joseluisgs.walaspringboot.services.UserService;
import com.joseluisgs.walaspringboot.storage.StorageProperties;
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

    static void main(String[] args) {
        SpringApplication.run(WalaSpringBootApplication.class, args);
    }

    /**
     * Inicializa datos de prueba SOLO en perfil de desarrollo
     * En producción (perfil prod) este bean no se crea
     */
    @Bean
    @org.springframework.context.annotation.Profile("dev")
    public CommandLineRunner initData(UserService usuarioServicio, ProductService productoServicio) {
        return args -> {
            System.out.println("🔧 PERFIL DEV: Cargando datos de prueba...");

            // Crear usuarios con diferentes roles
            User admin = new User("Admin", "Administrador", null, "admin@walaspringboot.com", "admin", "ADMIN");
            admin = usuarioServicio.registrar(admin);

            User usuario = new User("Prueba", "Probando Mucho", null, "prueba@prueba.com", "prueba", "USER");
            usuario = usuarioServicio.registrar(usuario);

            User moderador = new User("Moderador", "User", null, "moderador@walaspringboot.com", "moderador", "MODERATOR");
            moderador = usuarioServicio.registrar(moderador);

            User usuario2 = new User("Otro", "User", null, "otro@otro.com", "otro", "USER");
            usuario2 = usuarioServicio.registrar(usuario2);

            System.out.println("✅ Usuarios de prueba creados: admin, prueba, moderador, otro");

            // Productos actualizados 2024-2025
            List<Product> listado = Arrays.asList(
                    new Product("iPhone 17 Pro Max", 1199.0f,
                            "https://medias.lapostemobile.fr/fiche_mobile/layer/9724_Layer_2.png",
                            "El iPhone más avanzado de Apple con chip A17 Pro, titanio aeroespacial y cámara de 48MP. Estado impecable, apenas usado.",
                            "Smartphones", usuario),
                    new Product("Samsung Galaxy S24 Ultra", 1099.0f,
                            "https://cdn.grupoelcorteingles.es/SGFM/dctm/MEDIA03/202404/11/00157063608129009_22__1200x1200.jpg",
                            "Flagship de Samsung con S Pen integrado, pantalla Dynamic AMOLED 2X y cámara de 200MP. Como nuevo, con todos los accesorios.",
                            "Smartphones", usuario),
                    new Product("Google Pixel 8 Pro", 899.0f,
                            "https://http2.mlstatic.com/D_NQ_NP_802433-MLU78081005713_072024-O.webp",
                            "El mejor teléfono para fotografía con IA de Google Tensor G3. Pantalla LTPO de 120Hz. Excelente estado de conservación.",
                            "Smartphones", usuario2),
                    new Product("MacBook Pro M3", 1999.0f,
                            "https://www.notebookcheck.org/fileadmin/Notebooks/Apple/MacBook_Pro_14_2023_M3_Max/IMG_1008.JPG",
                            "MacBook Pro 14 con chip M3 Max, 36GB RAM, 1TB SSD. Perfecto para profesionales creativos. Garantía Apple vigente.",
                            "Laptops", admin),
                    new Product("AirPods Pro 2ª Gen", 249.0f,
                            "https://cdsassets.apple.com/live/SZLF0YNV/images/sp/111851_sp880-airpods-Pro-2nd-gen.png",
                            "Auriculares con cancelación de ruido adaptativa y audio espacial personalizado. Nuevos en caja sellada.",
                            "Audio", moderador),
                    new Product("Steam Deck OLED", 549.0f,
                            "https://i.blogs.es/420d82/steam-deck-oled-portada/1366_521.jpeg",
                            "Consola portátil con pantalla OLED HDR de 7.4 pulgadas. Modelo de 512GB. Gaming en cualquier lugar. Como nuevo.",
                            "Gaming", usuario2)
            );

            listado.forEach(productoServicio::insertar);

            System.out.println("✅ " + listado.size() + " productos de prueba creados");
            System.out.println("🔧 Datos de prueba cargados correctamente");
        };
    }
}
