package com.joseluisgs.walaspringboot.data;

import com.joseluisgs.walaspringboot.models.Product;
import com.joseluisgs.walaspringboot.models.User;

import java.util.Arrays;
import java.util.List;

/**
 * Factory para generar datos de prueba del marketplace
 * Genera usuarios y productos para testing y desarrollo
 *
 * @author José Luis González (@joseluisgs)
 * @version 1.0
 * @since 2025-11-09
 */
public class DataFactory {

    /**
     * Crea lista de usuarios de prueba con diferentes roles y avatars
     *
     * @return Lista de usuarios sin persistir
     */
    public static List<User> createTestUsers() {
        return Arrays.asList(
                new User("Admin", "Administrador", "https://robohash.org/admin?size=200x200&bgset=bg1",
                        "admin@waladaw.com", "admin", "ADMIN"),
                new User("Prueba", "Probando Mucho", "https://robohash.org/prueba?size=200x200&bgset=bg2",
                        "prueba@prueba.com", "prueba", "USER"),
                new User("Moderador", "User", "https://api.dicebear.com/7.x/avataaars/svg?seed=moderador&backgroundColor=b6e3f4",
                        "moderador@waladaw.com", "moderador", "MODERATOR"),
                new User("Otro", "User", "https://api.dicebear.com/7.x/personas/svg?seed=otro&backgroundColor=c0aede",
                        "otro@otro.com", "otro", "USER"),
                new User("María", "García López", "https://api.dicebear.com/7.x/avataaars/svg?seed=maria&backgroundColor=ffd5dc",
                        "maria@email.com", "maria123", "USER"),
                new User("Carlos", "Rodríguez Pérez", "https://robohash.org/carlos?size=200x200&bgset=any&set=set1",
                        "carlos@email.com", "carlos123", "USER"),
                new User("Ana", "Martín Sánchez", "https://api.dicebear.com/7.x/adventurer/svg?seed=ana&backgroundColor=ffdfbf",
                        "ana@email.com", "ana123", "USER"),
                new User("David", "López Torres", "https://robohash.org/david?size=200x200&bgset=bg1&set=set4",
                        "david@email.com", "david123", "USER"),
                new User("Laura", "Fernández Ruiz", "https://api.dicebear.com/7.x/big-smile/svg?seed=laura&backgroundColor=d1f2eb",
                        "laura@email.com", "laura123", "USER"),
                new User("Javier", "Moreno Silva", "https://robohash.org/javier?size=200x200&bgset=any&set=set3",
                        "javier@email.com", "javier123", "USER")
        );
    }

    /**
     * Crea lista completa de productos de prueba para el marketplace
     *
     * @param usuarios Lista de usuarios ya creados para asignar como propietarios
     * @return Lista de productos sin persistir
     */
    public static List<Product> createTestProducts(List<User> usuarios) {
        // Asignación de usuarios para variedad
        User admin = usuarios.get(0);      // Admin
        User usuario = usuarios.get(1);     // Prueba
        User moderador = usuarios.get(2);   // Moderador
        User usuario2 = usuarios.get(3);    // Otro
        User maria = usuarios.get(4);       // María
        User carlos = usuarios.get(5);      // Carlos
        User ana = usuarios.get(6);         // Ana
        User david = usuarios.get(7);       // David
        User laura = usuarios.get(8);       // Laura
        User javier = usuarios.get(9);      // Javier

        return Arrays.asList(
                // SMARTPHONES - 10 productos
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
                new Product("iPhone 15 Pro", 999.0f,
                        "https://images.unsplash.com/photo-1695048133142-1a20484d2569?w=400",
                        "iPhone 15 Pro con titanio natural, cámara principal de 48MP y zoom óptico 3x. Perfecto estado, sin rayones.",
                        "Smartphones", maria),
                new Product("OnePlus 12", 749.0f,
                        "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400",
                        "Flagship killer con Snapdragon 8 Gen 3, carga rápida 100W y pantalla AMOLED 120Hz. Como recién salido de caja.",
                        "Smartphones", carlos),
                new Product("Xiaomi 14 Ultra", 899.0f,
                        "https://images.unsplash.com/photo-1592899677977-9c10ca588bbd?w=400",
                        "Cámara Leica profesional, Snapdragon 8 Gen 3 y diseño premium. Ideal para fotografía móvil avanzada.",
                        "Smartphones", ana),
                new Product("iPhone 14", 699.0f,
                        "https://images.unsplash.com/photo-1678685888221-cda773a3dcdb?w=400",
                        "iPhone 14 en azul, 128GB. Batería excelente, pantalla perfecta. Incluye cargador y funda original.",
                        "Smartphones", david),
                new Product("Nothing Phone 2", 599.0f,
                        "https://images.unsplash.com/photo-1598327105666-5b89351aff97?w=400",
                        "Diseño transparente único con Glyph Interface. Android puro y rendimiento excepcional.",
                        "Smartphones", laura),
                new Product("Samsung Galaxy Z Flip 5", 999.0f,
                        "https://images.unsplash.com/photo-1567721913486-6585f069b332?w=400",
                        "Plegable compacto con pantalla externa mejorada. Perfecto para quienes buscan innovación.",
                        "Smartphones", javier),
                new Product("Google Pixel 7a", 449.0f,
                        "https://images.unsplash.com/photo-1598300042247-d088f8ab3a91?w=400",
                        "El mejor Pixel en relación calidad-precio. Cámara excepcional y Android puro garantizado.",
                        "Smartphones", moderador),

                // LAPTOPS - 8 productos
                new Product("MacBook Pro M3", 1999.0f,
                        "https://www.notebookcheck.org/fileadmin/Notebooks/Apple/MacBook_Pro_14_2023_M3_Max/IMG_1008.JPG",
                        "MacBook Pro 14 con chip M3 Max, 36GB RAM, 1TB SSD. Perfecto para profesionales creativos. Garantía Apple vigente.",
                        "Laptops", admin),
                new Product("MacBook Air M2", 1299.0f,
                        "https://images.unsplash.com/photo-1541807084-5c52b6b3adef?w=400",
                        "MacBook Air M2 de 13 pulgadas, 16GB RAM, 512GB SSD. Ultra portátil y silencioso. Perfecto para estudiantes.",
                        "Laptops", usuario),
                new Product("Dell XPS 13", 1149.0f,
                        "https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=400",
                        "Ultrabook premium con Intel i7, 16GB RAM, pantalla 4K táctil. Ideal para productividad y diseño.",
                        "Laptops", maria),
                new Product("ASUS ROG Strix", 1799.0f,
                        "https://images.unsplash.com/photo-1593642632559-0c6d3fc62b89?w=400",
                        "Laptop gaming con RTX 4070, Intel i7-13700H, 32GB RAM. Perfecto para gaming y streaming profesional.",
                        "Laptops", carlos),
                new Product("ThinkPad X1 Carbon", 1599.0f,
                        "https://images.unsplash.com/photo-1588872657578-7efd1f1555ed?w=400",
                        "Laptop empresarial premium, ultra liviano, teclado excepcional. Ideal para profesionales y ejecutivos.",
                        "Laptops", ana),
                new Product("HP Spectre x360", 1249.0f,
                        "https://images.unsplash.com/photo-1525547719571-a2d4ac8945e2?w=400",
                        "Convertible 2-en-1 con pantalla táctil OLED, Intel i7 y diseño premium. Versatilidad máxima.",
                        "Laptops", david),
                new Product("Surface Laptop 5", 1399.0f,
                        "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=400",
                        "Microsoft Surface con procesador Intel de 12ª gen, pantalla PixelSense táctil. Elegancia y rendimiento.",
                        "Laptops", laura),
                new Product("Framework Laptop", 999.0f,
                        "https://images.unsplash.com/photo-1484788984921-03950022c9ef?w=400",
                        "Laptop modular y reparable, diseño sostenible. Perfecto para desarrolladores conscientes.",
                        "Laptops", javier),

                // AUDIO - 8 productos
                new Product("AirPods Pro 2ª Gen", 249.0f,
                        "https://cdsassets.apple.com/live/SZLF0YNV/images/sp/111851_sp880-airpods-Pro-2nd-gen.png",
                        "Auriculares con cancelación de ruido adaptativa y audio espacial personalizado. Nuevos en caja sellada.",
                        "Audio", moderador),
                new Product("Sony WH-1000XM5", 299.0f,
                        "https://images.unsplash.com/photo-1583394838336-acd977736f90?w=400",
                        "Auriculares noise-cancelling líderes del mercado. Sonido excepcional y comodidad todo el día.",
                        "Audio", usuario),
                new Product("Bose QuietComfort", 249.0f,
                        "https://images.unsplash.com/photo-1545127398-14699f92334b?w=400",
                        "Cancelación de ruido premium de Bose. Perfectos para viajes largos y trabajo concentrado.",
                        "Audio", maria),
                new Product("Marshall Acton III", 199.0f,
                        "https://images.unsplash.com/photo-1608043152269-423dbba4e7e1?w=400",
                        "Altavoz Bluetooth vintage con sonido Marshall icónico. Perfecto para ambientar cualquier espacio.",
                        "Audio", carlos),
                new Product("JBL Flip 6", 89.0f,
                        "https://images.unsplash.com/photo-1608043152269-423dbba4e7e1?w=400",
                        "Altavoz portátil resistente al agua, sonido potente y batería de 12 horas. Ideal para exteriores.",
                        "Audio", ana),
                new Product("Sennheiser HD 660S", 399.0f,
                        "https://images.unsplash.com/photo-1484704849700-f032a568e944?w=400",
                        "Auriculares audiófilo de referencia con drivers dinámicos mejorados. Para los más exigentes.",
                        "Audio", david),
                new Product("Jabra Elite 85h", 179.0f,
                        "https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=400",
                        "Auriculares wireless con cancelación de ruido inteligente y 36h de batería. Perfecto para oficina.",
                        "Audio", laura),
                new Product("Audio-Technica ATH-M50x", 149.0f,
                        "https://images.unsplash.com/photo-1487215078519-e21cc028cb29?w=400",
                        "Auriculares de estudio profesional con sonido neutro y construcción robusta. Clásico atemporal.",
                        "Audio", javier),

                // GAMING - 8 productos
                new Product("Steam Deck OLED", 549.0f,
                        "https://i.blogs.es/420d82/steam-deck-oled-portada/1366_521.jpeg",
                        "Consola portátil con pantalla OLED HDR de 7.4 pulgadas. Modelo de 512GB. Gaming en cualquier lugar. Como nuevo.",
                        "Gaming", usuario2),
                new Product("PlayStation 5", 499.0f,
                        "https://images.unsplash.com/photo-1606813907291-d86efa9b94db?w=400",
                        "PS5 en perfecto estado con un mando adicional. Incluye 3 juegos digitales. Como nueva.",
                        "Gaming", usuario),
                new Product("Xbox Series X", 459.0f,
                        "https://images.unsplash.com/photo-1621259182978-fbf93132d53d?w=400",
                        "Consola next-gen con 1TB, Game Pass incluido por 3 meses. Potencia 4K para gaming hardcore.",
                        "Gaming", maria),
                new Product("Nintendo Switch OLED", 329.0f,
                        "https://images.unsplash.com/photo-1578662996442-48f60103fc96?w=400",
                        "Switch OLED con pantalla mejorada, incluye Pro Controller y 5 juegos físicos. Diversión garantizada.",
                        "Gaming", carlos),
                new Product("Logitech G Pro X", 159.0f,
                        "https://images.unsplash.com/photo-1542751371-adc38448a05e?w=400",
                        "Auriculares gaming profesionales con micrófono Blue VO!CE. Usados en esports profesional.",
                        "Gaming", ana),
                new Product("Razer DeathAdder V3", 69.0f,
                        "https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=400",
                        "Ratón gaming ergonómico con sensor Focus Pro 30K. Precisión profesional para competir.",
                        "Gaming", david),
                new Product("ROG Ally", 699.0f,
                        "https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=400",
                        "Handheld PC gaming con Windows 11, juega tu biblioteca Steam en cualquier lugar.",
                        "Gaming", laura),
                new Product("Valve Index VR", 999.0f,
                        "https://images.unsplash.com/photo-1622979135225-d2ba269cf1ac?w=400",
                        "Sistema VR premium con tracking de dedos y audio off-ear. La mejor experiencia de realidad virtual.",
                        "Gaming", javier),

                // ACCESSORIES - 8 productos
                new Product("Apple Watch Series 9", 399.0f,
                        "https://images.unsplash.com/photo-1434493789847-2f02dc6ca35d?w=400",
                        "Smartwatch más avanzado de Apple, GPS + Cellular, correa deportiva. Salud y fitness al máximo.",
                        "Accessories", usuario),
                new Product("iPad Pro 12.9", 1099.0f,
                        "https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=400",
                        "iPad Pro con M2, Apple Pencil incluido. Perfecto para diseño digital y productividad profesional.",
                        "Accessories", moderador),
                new Product("MagSafe Charger Pack", 79.0f,
                        "https://images.unsplash.com/photo-1588423771073-b8903fbb85b5?w=400",
                        "Cargador inalámbrico MagSafe original + soporte + cable USB-C. Carga rápida garantizada.",
                        "Accessories", maria),
                new Product("Anker PowerCore", 45.0f,
                        "https://images.unsplash.com/photo-1609091839311-d5365f9ff1c5?w=400",
                        "Batería portátil 20.000mAh con carga rápida PD. Ideal para viajes y emergencias energéticas.",
                        "Accessories", carlos),
                new Product("Samsung Galaxy Watch", 289.0f,
                        "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=400",
                        "Smartwatch Android con GPS, monitor cardíaco y resistencia al agua. Perfecto para fitness.",
                        "Accessories", ana),
                new Product("Mechanical Keyboard", 149.0f,
                        "https://images.unsplash.com/photo-1541140532154-b024d705b90a?w=400",
                        "Teclado mecánico RGB con switches Cherry MX Blue. Perfecto para programadores y gamers.",
                        "Accessories", david),
                new Product("4K Webcam", 89.0f,
                        "https://images.unsplash.com/photo-1587825140708-dfaf72ae4b04?w=400",
                        "Cámara web 4K con autoenfoque, ideal para streaming y videoconferencias profesionales.",
                        "Accessories", laura),
                new Product("Wireless Charger Stand", 39.0f,
                        "https://images.unsplash.com/photo-1583394838336-acd977736f90?w=400",
                        "Soporte cargador inalámbrico con ventilador de refrigeración. Compatible con todos los estándares Qi.",
                        "Accessories", javier)
        );
    }

    /**
     * Obtiene estadísticas de los datos generados
     *
     * @return String con información de usuarios y productos creados
     */
    public static String getDataSummary() {
        List<User> users = createTestUsers();
        List<Product> products = createTestProducts(users);

        long smartphoneCount = products.stream().filter(p -> "Smartphones".equals(p.getCategoria().getDisplayName())).count();
        long laptopCount = products.stream().filter(p -> "Laptops".equals(p.getCategoria().getDisplayName())).count();
        long audioCount = products.stream().filter(p -> "Audio".equals(p.getCategoria().getDisplayName())).count();
        long gamingCount = products.stream().filter(p -> "Gaming".equals(p.getCategoria().getDisplayName())).count();
        long accessoryCount = products.stream().filter(p -> "Accessories".equals(p.getCategoria().getDisplayName())).count();

        return String.format(
                "📊 DataFactory Summary:\n" +
                        "👥 Usuarios: %d (1 Admin, 1 Moderador, %d Users)\n" +
                        "📱 Productos: %d total\n" +
                        "   📱 Smartphones: %d\n" +
                        "   💻 Laptops: %d\n" +
                        "   🎧 Audio: %d\n" +
                        "   🎮 Gaming: %d\n" +
                        "   ⚡ Accessories: %d\n" +
                        "✨ Con avatars únicos y productos variados para testing completo",
                users.size(), users.size() - 2, products.size(),
                smartphoneCount, laptopCount, audioCount, gamingCount, accessoryCount
        );
    }
}