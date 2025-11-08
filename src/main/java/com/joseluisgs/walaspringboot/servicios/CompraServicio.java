package com.joseluisgs.walaspringboot.servicios;

import com.joseluisgs.walaspringboot.modelos.Compra;
import com.joseluisgs.walaspringboot.modelos.Producto;
import com.joseluisgs.walaspringboot.modelos.Usuario;
import com.joseluisgs.walaspringboot.repositorios.CompraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompraServicio {

    @Autowired
    CompraRepository repositorio;

    @Autowired
    ProductoServicio productoServicio;

    @CacheEvict(value = "compras", allEntries = true)
    public Compra insertar(Compra c, Usuario u) {
        c.setPropietario(u);
        return repositorio.save(c);
    }

    @CacheEvict(value = "compras", allEntries = true)
    public Compra insertar(Compra c) {
        return repositorio.save(c);
    }

    public Producto addProductoCompra(Producto p, Compra c) {
        p.setCompra(c);
        return productoServicio.editar(p);
    }

    @Cacheable(value = "compras", key = "#id")
    public Compra buscarPorId(long id) {
        return repositorio.findById(id).orElse(null);
    }

    @Cacheable(value = "compras")
    public List<Compra> todas() {
        return repositorio.findAll();
    }

    @Cacheable(value = "compras", key = "'usuario_' + #u.id")
    public List<Compra> porPropietario(Usuario u) {
        return repositorio.findByPropietario(u);
    }
}
