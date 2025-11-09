package dev.joseluisgs.walaspringboot.repositories;

import dev.joseluisgs.walaspringboot.models.Product;
import dev.joseluisgs.walaspringboot.models.ProductCategory;
import dev.joseluisgs.walaspringboot.models.Purchase;
import dev.joseluisgs.walaspringboot.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByPropietario(User propietario);

    List<Product> findByCompra(Purchase compra);

    List<Product> findByCompraIsNull();

    List<Product> findByNombreContainsIgnoreCaseAndCompraIsNull(String nombre);

    List<Product> findByNombreContainsIgnoreCaseAndPropietario(String nombre, User propietario);

    List<Product> findByPrecioBetweenAndCompraIsNull(float minPrecio, float maxPrecio);

    @Query("SELECT p FROM Product p WHERE p.categoria = :categoria AND p.deleted = false AND p.compra IS NULL ORDER BY p.id DESC")
    List<Product> findByCategoria(@Param("categoria") ProductCategory categoria);

    @Query("SELECT p FROM Product p WHERE p.deleted = false ORDER BY p.id DESC")
    List<Product> findAllActive();

    @Query("SELECT p FROM Product p WHERE p.id = :id AND p.deleted = false")
    Optional<Product> findActiveById(@Param("id") Long id);

    @Query("SELECT p FROM Product p WHERE p.propietario = :propietario AND p.deleted = false")
    List<Product> findByPropietarioActive(@Param("propietario") User propietario);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.propietario = :propietario AND p.deleted = false")
    long countByPropietarioActive(@Param("propietario") User propietario);

    @Query("SELECT p FROM Product p WHERE p.nombre LIKE %:search% AND p.deleted = false")
    List<Product> findByNombreContainingActive(@Param("search") String search);

    // Pagination methods
    @Query("SELECT p FROM Product p WHERE p.deleted = false AND p.compra IS NULL ORDER BY p.id DESC")
    Page<Product> findByDeletedFalseAndCompraIsNull(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.categoria = :categoria AND p.deleted = false AND p.compra IS NULL ORDER BY p.id DESC")
    Page<Product> findByCategoriaAndDeletedFalseAndCompraIsNull(@Param("categoria") ProductCategory categoria, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) AND p.deleted = false AND p.compra IS NULL ORDER BY p.id DESC")
    Page<Product> findByNombreContainingIgnoreCaseAndDeletedFalseAndCompraIsNull(@Param("nombre") String nombre, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.precio BETWEEN :min AND :max AND p.deleted = false AND p.compra IS NULL ORDER BY p.id DESC")
    Page<Product> findByPrecioBetweenAndDeletedFalseAndCompraIsNull(@Param("min") Float min, @Param("max") Float max, Pageable pageable);

    // Admin pagination methods (includes all products, not just unsold)
    @Query("SELECT p FROM Product p WHERE p.deleted = false ORDER BY p.id DESC")
    Page<Product> findAllActivePaginated(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) AND p.deleted = false ORDER BY p.id DESC")
    Page<Product> findByNombreContainingActivePaginated(@Param("nombre") String nombre, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.categoria = :categoria AND p.deleted = false ORDER BY p.id DESC")
    Page<Product> findByCategoriaActivePaginated(@Param("categoria") ProductCategory categoria, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.propietario.id = :propietarioId AND p.deleted = false ORDER BY p.id DESC")
    Page<Product> findByPropietarioIdActivePaginated(@Param("propietarioId") Long propietarioId, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) AND p.categoria = :categoria AND p.deleted = false ORDER BY p.id DESC")
    Page<Product> findByNombreAndCategoriaActivePaginated(@Param("nombre") String nombre, @Param("categoria") ProductCategory categoria, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) AND p.propietario.id = :propietarioId AND p.deleted = false ORDER BY p.id DESC")
    Page<Product> findByNombreAndPropietarioActivePaginated(@Param("nombre") String nombre, @Param("propietarioId") Long propietarioId, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.categoria = :categoria AND p.propietario.id = :propietarioId AND p.deleted = false ORDER BY p.id DESC")
    Page<Product> findByCategoriaAndPropietarioActivePaginated(@Param("categoria") ProductCategory categoria, @Param("propietarioId") Long propietarioId, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) AND p.categoria = :categoria AND p.propietario.id = :propietarioId AND p.deleted = false ORDER BY p.id DESC")
    Page<Product> findByAllFiltersActivePaginated(@Param("nombre") String nombre, @Param("categoria") ProductCategory categoria, @Param("propietarioId") Long propietarioId, Pageable pageable);
}
