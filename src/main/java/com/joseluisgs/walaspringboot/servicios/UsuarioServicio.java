package com.joseluisgs.walaspringboot.servicios;

import com.joseluisgs.walaspringboot.modelos.Usuario;
import com.joseluisgs.walaspringboot.repositorios.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioServicio {

    @Autowired
    UsuarioRepository repositorio;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @CacheEvict(value = "usuarios", allEntries = true)
    public Usuario registrar(Usuario u) {
        u.setPassword(passwordEncoder.encode(u.getPassword()));
        return repositorio.save(u);
    }

    @Cacheable(value = "usuarios", key = "#id")
    public Usuario findById(long id) {
        return repositorio.findById(id).orElse(null);
    }

    @Cacheable(value = "usuarios", key = "#email")
    public Usuario buscarPorEmail(String email) {
        return repositorio.findFirstByEmail(email);
    }
}
