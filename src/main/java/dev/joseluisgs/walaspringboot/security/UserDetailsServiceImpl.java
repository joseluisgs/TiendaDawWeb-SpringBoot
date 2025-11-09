package dev.joseluisgs.walaspringboot.security;

import dev.joseluisgs.walaspringboot.models.User;
import dev.joseluisgs.walaspringboot.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository repositorio;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User usuario = repositorio.findFirstByEmail(username);

        if (usuario == null) {
            throw new UsernameNotFoundException("User no encontrado");
        }

        return usuario;
    }
}
