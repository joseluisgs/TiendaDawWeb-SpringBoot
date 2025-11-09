package com.joseluisgs.walaspringboot.repositories;

import com.joseluisgs.walaspringboot.models.Purchase;
import com.joseluisgs.walaspringboot.models.Product;
import com.joseluisgs.walaspringboot.models.ProductCategory;
import com.joseluisgs.walaspringboot.models.User;
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
}
