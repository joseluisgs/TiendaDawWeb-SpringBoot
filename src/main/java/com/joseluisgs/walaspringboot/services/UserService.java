package com.joseluisgs.walaspringboot.services;

import com.joseluisgs.walaspringboot.models.User;
import com.joseluisgs.walaspringboot.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserRepository repositorio;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @CacheEvict(value = "usuarios", allEntries = true)
    public User registrar(User u) {
        u.setPassword(passwordEncoder.encode(u.getPassword()));
        return repositorio.save(u);
    }

    @Cacheable(value = "usuarios", key = "#id")
    public User findById(long id) {
        return repositorio.findById(id).orElse(null);
    }

    @Cacheable(value = "usuarios", key = "#email")
    public User buscarPorEmail(String email) {
        return repositorio.findFirstByEmail(email);
    }

    @Cacheable(value = "usuarios")
    public java.util.List<User> findAll() {
        return repositorio.findAll();
    }

    @CacheEvict(value = "usuarios", allEntries = true)
    public User editar(User u) {
        return repositorio.save(u);
    }

    @CacheEvict(value = "usuarios", allEntries = true)
    public void borrar(Long id) {
        repositorio.deleteById(id);
    }
}
