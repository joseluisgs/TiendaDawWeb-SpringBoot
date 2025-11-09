package com.joseluisgs.walaspringboot;

import com.joseluisgs.walaspringboot.data.DataFactory;
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
    /**
     * Inicializa datos de prueba para desarrollo
     * @param usuarioServicio Servicio de usuarios
     * @param productoServicio Servicio de productos
     * @return CommandLineRunner configurado
     */
    public CommandLineRunner initData(UserService usuarioServicio, ProductService productoServicio) {
        return args -> {
            System.out.println("🔧 PERFIL DEV: Inicializando marketplace con datos de prueba...");
            System.out.println("📅 Fecha: " + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            try {
                // 👥 Crear usuarios de prueba
                System.out.println("\n👥 Creando usuarios de prueba...");
                List<User> usuarios = DataFactory.createTestUsers()
                        .stream()
                        .map(usuarioServicio::registrar)
                        .toList();

                System.out.printf("✅ %d usuarios creados exitosamente:\n", usuarios.size());
                usuarios.forEach(user ->
                        System.out.printf("   • %s (%s) - %s%n",
                                user.getNombre(),
                                user.getEmail(),
                                user.getRol()
                        )
                );

                // 📦 Crear productos de prueba
                System.out.println("\n📦 Creando catálogo de productos...");
                List<Product> productos = DataFactory.createTestProducts(usuarios);
                productos.forEach(productoServicio::insertar);

                // 📊 Estadísticas finales
                System.out.println("\n" + DataFactory.getDataSummary());
                System.out.println("\n🚀 Marketplace inicializado correctamente!");
                System.out.println("🌐 Acceso: http://localhost:8080/public");
                System.out.println("🔑 Login admin: admin@waladaw.com / admin");

            } catch (Exception e) {
                System.err.println("❌ Error inicializando datos de prueba: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }
}
