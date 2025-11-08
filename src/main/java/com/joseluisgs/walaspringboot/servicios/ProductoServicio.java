package com.joseluisgs.walaspringboot.servicios;

import com.joseluisgs.walaspringboot.modelos.Compra;
import com.joseluisgs.walaspringboot.modelos.Producto;
import com.joseluisgs.walaspringboot.modelos.Usuario;
import com.joseluisgs.walaspringboot.repositorios.ProductoRepository;
import com.joseluisgs.walaspringboot.upload.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoServicio {
    @Autowired
    ProductoRepository repositorio;

    @Autowired
    StorageService storageService;

    @CacheEvict(value = "productos", allEntries = true)
    public Producto insertar(Producto p) {
        return repositorio.save(p);
    }

    @CacheEvict(value = "productos", allEntries = true)
    public void borrar(long id) {
        repositorio.deleteById(id);
    }

    @CacheEvict(value = "productos", allEntries = true)
    public void borrar(Producto p) {
        if (!p.getImagen().isEmpty())
             storageService.delete(p.getImagen());
        repositorio.delete(p);
    }

    @CacheEvict(value = "productos", allEntries = true)
    public Producto editar(Producto p) {
        return repositorio.save(p);
    }

    @Cacheable(value = "productos", key = "#id")
    public Producto findById(long id) {
        return repositorio.findById(id).orElse(null);
    }

    @Cacheable(value = "productos")
    public List<Producto> findAll() {
        return repositorio.findAll();
    }

    @Cacheable(value = "productos", key = "'usuario_' + #u.id")
    public List<Producto> productosDeUnPropietario(Usuario u) {
        return repositorio.findByPropietario(u);
    }

    @Cacheable(value = "productos", key = "'compra_' + #c.id")
    public List<Producto> productosDeUnaCompra(Compra c) {
        return repositorio.findByCompra(c);
    }

    @Cacheable(value = "productos", key = "'sinvender'")
    public List<Producto> productosSinVender() {
        return repositorio.findByCompraIsNull();
    }

    public List<Producto> buscar(String query) {
        return repositorio.findByNombreContainsIgnoreCaseAndCompraIsNull(query);
    }

    public List<Producto> buscarMisProductos(String query, Usuario u) {
        return repositorio.findByNombreContainsIgnoreCaseAndPropietario(query,u);
    }

    public List<Producto> variosPorId(List<Long> ids) {
        return repositorio.findAllById(ids);
    }
}
