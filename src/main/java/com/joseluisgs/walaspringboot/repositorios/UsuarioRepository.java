package com.joseluisgs.walaspringboot.repositorios;

import com.joseluisgs.walaspringboot.modelos.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Usuario findFirstByEmail(String email);
}
