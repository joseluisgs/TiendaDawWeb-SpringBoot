package com.joseluisgs.walaspringboot.services;

import com.joseluisgs.walaspringboot.models.Purchase;
import com.joseluisgs.walaspringboot.models.Product;
import com.joseluisgs.walaspringboot.models.ProductCategory;
import com.joseluisgs.walaspringboot.models.User;
import com.joseluisgs.walaspringboot.repositories.ProductRepository;
import com.joseluisgs.walaspringboot.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    ProductRepository repositorio;

    @Autowired
    StorageService storageService;

    @CacheEvict(value = "productos", allEntries = true)
    public Product insertar(Product p) {
        return repositorio.save(p);
    }

    @CacheEvict(value = "productos", allEntries = true)
    public void borrar(long id) {
        repositorio.deleteById(id);
    }

    @CacheEvict(value = "productos", allEntries = true)
    public void borrar(Product p) {
        if (!p.getImagen().isEmpty())
             storageService.delete(p.getImagen());
        repositorio.delete(p);
    }

    @CacheEvict(value = "productos", allEntries = true)
    public Product editar(Product p) {
        return repositorio.save(p);
    }

    @Cacheable(value = "productos", key = "#id")
    public Product findById(long id) {
        return repositorio.findById(id).orElse(null);
    }

    @Cacheable(value = "productos")
    public List<Product> findAll() {
        return repositorio.findAllActive();
    }

    @Cacheable(value = "productos", key = "'usuario_' + #u.id")
    public List<Product> productosDeUnPropietario(User u) {
        return repositorio.findByPropietarioActive(u);
    }

    @Cacheable(value = "productos", key = "'compra_' + #c.id")
    public List<Product> productosDeUnaCompra(Purchase c) {
        return repositorio.findByCompra(c);
    }

    @Cacheable(value = "productos", key = "'sinvender'")
    public List<Product> productosSinVender() {
        return repositorio.findByCompraIsNull();
    }

    public List<Product> buscar(String query) {
        return repositorio.findByNombreContainsIgnoreCaseAndCompraIsNull(query);
    }

    public List<Product> buscarMisProductos(String query, User u) {
        return repositorio.findByNombreContainsIgnoreCaseAndPropietario(query,u);
    }

    public List<Product> variosPorId(List<Long> ids) {
        return repositorio.findAllById(ids);
    }
    
    public long countByPropietarioActive(User user) {
        return repositorio.countByPropietarioActive(user);
    }
    
    public Optional<Product> findByIdOptional(Long id) {
        return repositorio.findActiveById(id);
    }
    
    @CacheEvict(value = "productos", allEntries = true)
    public void softDelete(Long id, String deletedBy) {
        Product product = findById(id);
        if (product != null) {
            product.softDelete(deletedBy);
            repositorio.save(product);
        }
    }
    
    public boolean hasBeenSold(Product product) {
        return product.getCompra() != null;
    }
    
    @Cacheable(value = "productos", key = "'categoria_' + #categoria.name()")
    public List<Product> findByCategoria(ProductCategory categoria) {
        return repositorio.findByCategoria(categoria);
    }

    // Pagination methods
    public Page<Product> findAll(Pageable pageable) {
        return repositorio.findByDeletedFalseAndCompraIsNull(pageable);
    }

    public Page<Product> findByCategoria(ProductCategory categoria, Pageable pageable) {
        return repositorio.findByCategoriaAndDeletedFalseAndCompraIsNull(categoria, pageable);
    }

    public Page<Product> findByNombreContainingIgnoreCase(String nombre, Pageable pageable) {
        return repositorio.findByNombreContainingIgnoreCaseAndDeletedFalseAndCompraIsNull(nombre, pageable);
    }

    public Page<Product> findByPrecioBetween(Float min, Float max, Pageable pageable) {
        return repositorio.findByPrecioBetweenAndDeletedFalseAndCompraIsNull(min, max, pageable);
    }
    
    // Admin pagination methods
    public Page<Product> findAllActivePaginated(Pageable pageable) {
        return repositorio.findAllActivePaginated(pageable);
    }
    
    public Page<Product> findByNombreContainingActivePaginated(String nombre, Pageable pageable) {
        return repositorio.findByNombreContainingActivePaginated(nombre, pageable);
    }
    
    public Page<Product> findByCategoriaActivePaginated(ProductCategory categoria, Pageable pageable) {
        return repositorio.findByCategoriaActivePaginated(categoria, pageable);
    }
    
    public Page<Product> findByPropietarioIdActivePaginated(Long propietarioId, Pageable pageable) {
        return repositorio.findByPropietarioIdActivePaginated(propietarioId, pageable);
    }
    
    public Page<Product> findByNombreAndCategoriaActivePaginated(String nombre, ProductCategory categoria, Pageable pageable) {
        return repositorio.findByNombreAndCategoriaActivePaginated(nombre, categoria, pageable);
    }
    
    public Page<Product> findByNombreAndPropietarioActivePaginated(String nombre, Long propietarioId, Pageable pageable) {
        return repositorio.findByNombreAndPropietarioActivePaginated(nombre, propietarioId, pageable);
    }
    
    public Page<Product> findByCategoriaAndPropietarioActivePaginated(ProductCategory categoria, Long propietarioId, Pageable pageable) {
        return repositorio.findByCategoriaAndPropietarioActivePaginated(categoria, propietarioId, pageable);
    }
    
    public Page<Product> findByAllFiltersActivePaginated(String nombre, ProductCategory categoria, Long propietarioId, Pageable pageable) {
        return repositorio.findByAllFiltersActivePaginated(nombre, categoria, propietarioId, pageable);
    }
    
    @CacheEvict(value = "productos", allEntries = true)
    public void incrementarVistas(Long id) {
        Product product = findById(id);
        if (product != null) {
            product.incrementViews();
            repositorio.save(product);
        }
    }
}
