package dev.joseluisgs.walaspringboot.services;

import dev.joseluisgs.walaspringboot.models.User;
import dev.joseluisgs.walaspringboot.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
        return repositorio.findAllActive();
    }

    @CacheEvict(value = "usuarios", allEntries = true)
    public User editar(User u) {
        return repositorio.save(u);
    }

    @CacheEvict(value = "usuarios", allEntries = true)
    public void borrar(Long id) {
        repositorio.deleteById(id);
    }

    public Optional<User> findByIdOptional(Long id) {
        return repositorio.findActiveById(id);
    }

    @CacheEvict(value = "usuarios", allEntries = true)
    public void softDelete(Long id, String deletedBy) {
        User user = findById(id);
        if (user != null) {
            user.softDelete(deletedBy);
            repositorio.save(user);
        }
    }

    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(buscarPorEmail(email));
    }

    // Pagination methods
    public Page<User> findAllPaginated(Pageable pageable) {
        return repositorio.findAllActivePaginated(pageable);
    }

    public Page<User> findBySearchPaginated(String search, Pageable pageable) {
        return repositorio.findBySearchActivePaginated(search, pageable);
    }

    public Page<User> findByRolPaginated(String rol, Pageable pageable) {
        return repositorio.findByRolActivePaginated(rol, pageable);
    }

    public Page<User> findBySearchAndRolPaginated(String search, String rol, Pageable pageable) {
        return repositorio.findBySearchAndRolActivePaginated(search, rol, pageable);
    }
}
