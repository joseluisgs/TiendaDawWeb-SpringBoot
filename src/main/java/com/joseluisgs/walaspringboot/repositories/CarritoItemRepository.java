package com.joseluisgs.walaspringboot.repositories;

import com.joseluisgs.walaspringboot.models.CarritoItem;
import com.joseluisgs.walaspringboot.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CarritoItemRepository extends JpaRepository<CarritoItem, Long> {
    List<CarritoItem> findByUsuario(User usuario);
    Optional<CarritoItem> findByUsuarioAndProductoId(User usuario, Long productoId);
    void deleteByUsuario(User usuario);
}
