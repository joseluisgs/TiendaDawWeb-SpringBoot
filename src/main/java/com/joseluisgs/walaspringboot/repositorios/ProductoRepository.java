package com.joseluisgs.walaspringboot.repositorios;

import com.joseluisgs.walaspringboot.modelos.Compra;
import com.joseluisgs.walaspringboot.modelos.Producto;
import com.joseluisgs.walaspringboot.modelos.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByPropietario(Usuario propietario);
    List<Producto> findByCompra(Compra compra);
    List<Producto> findByCompraIsNull();
    List<Producto> findByNombreContainsIgnoreCaseAndCompraIsNull(String nombre);
    List<Producto> findByNombreContainsIgnoreCaseAndPropietario(String nombre, Usuario propietario);
    List<Producto> findByCategoriaAndCompraIsNull(String categoria);
    List<Producto> findByPrecioBetweenAndCompraIsNull(float minPrecio, float maxPrecio);
    List<Producto> findByNombreContainsIgnoreCaseAndCategoriaAndCompraIsNull(String nombre, String categoria);
}
