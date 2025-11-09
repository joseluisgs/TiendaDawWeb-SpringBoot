package com.joseluisgs.walaspringboot.repositories;

import com.joseluisgs.walaspringboot.models.Product;
import com.joseluisgs.walaspringboot.models.Purchase;
import com.joseluisgs.walaspringboot.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    List<Purchase> findByPropietario(User propietario);
    
    @Query("SELECT COUNT(c) FROM Purchase c WHERE c.propietario = :propietario")
    long countByPropietario(@Param("propietario") User propietario);
    
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Product p WHERE p.compra IS NOT NULL AND p = :product")
    boolean existsByProduct(@Param("product") Product product);
    
    // Queries optimizadas con JOIN FETCH
    @Query("SELECT DISTINCT p FROM Purchase p LEFT JOIN FETCH p.productos ORDER BY p.fechaCompra DESC")
    List<Purchase> findAllWithProducts();
    
    @Query("SELECT p FROM Purchase p LEFT JOIN FETCH p.productos WHERE p.id = :id")
    Optional<Purchase> findByIdWithProducts(@Param("id") Long id);
    
    @Query("SELECT DISTINCT p FROM Purchase p LEFT JOIN FETCH p.productos WHERE p.propietario = :propietario ORDER BY p.fechaCompra DESC")
    List<Purchase> findByPropietarioWithProducts(@Param("propietario") User propietario);
    
    // Pagination methods with JOIN FETCH to avoid LazyInitializationException
    @Query("SELECT DISTINCT p FROM Purchase p LEFT JOIN FETCH p.productos ORDER BY p.fechaCompra DESC")
    Page<Purchase> findAllPaginated(Pageable pageable);
    
    @Query("SELECT DISTINCT p FROM Purchase p LEFT JOIN FETCH p.productos WHERE CAST(p.fechaCompra AS date) BETWEEN :desde AND :hasta ORDER BY p.fechaCompra DESC")
    Page<Purchase> findByFechaCompraBetween(@Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta, Pageable pageable);
    
    @Query("SELECT DISTINCT p FROM Purchase p LEFT JOIN FETCH p.productos WHERE p.propietario.id = :propietarioId ORDER BY p.fechaCompra DESC")
    Page<Purchase> findByPropietarioIdPaginated(@Param("propietarioId") Long propietarioId, Pageable pageable);
    
    @Query("SELECT DISTINCT p FROM Purchase p LEFT JOIN FETCH p.productos WHERE CAST(p.fechaCompra AS date) BETWEEN :desde AND :hasta AND p.propietario.id = :propietarioId ORDER BY p.fechaCompra DESC")
    Page<Purchase> findByFechaCompraAndPropietario(@Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta, @Param("propietarioId") Long propietarioId, Pageable pageable);
}
