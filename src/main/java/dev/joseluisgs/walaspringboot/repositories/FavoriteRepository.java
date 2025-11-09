package dev.joseluisgs.walaspringboot.repositories;

import dev.joseluisgs.walaspringboot.models.Favorite;
import dev.joseluisgs.walaspringboot.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUsuario(User usuario);

    Optional<Favorite> findByUsuarioAndProductoId(User usuario, Long productoId);

    boolean existsByUsuarioAndProductoId(User usuario, Long productoId);

    void deleteByUsuarioAndProductoId(User usuario, Long productoId);
}
