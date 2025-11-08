package com.joseluisgs.walaspringboot.services;

import com.joseluisgs.walaspringboot.models.CarritoItem;
import com.joseluisgs.walaspringboot.models.Product;
import com.joseluisgs.walaspringboot.models.User;
import com.joseluisgs.walaspringboot.repositories.CarritoItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CarritoItemService {

    @Autowired
    private CarritoItemRepository carritoItemRepository;

    @Autowired
    private ProductService productService;

    public List<CarritoItem> findByUsuario(User usuario) {
        return carritoItemRepository.findByUsuario(usuario);
    }

    public CarritoItem addItem(User usuario, Long productoId, int cantidad) {
        Optional<CarritoItem> existingItem = carritoItemRepository.findByUsuarioAndProductoId(usuario, productoId);
        
        if (existingItem.isPresent()) {
            CarritoItem item = existingItem.get();
            item.setCantidad(item.getCantidad() + cantidad);
            return carritoItemRepository.save(item);
        } else {
            Product producto = productService.findById(productoId);
            if (producto != null) {
                CarritoItem newItem = new CarritoItem(usuario, producto, cantidad);
                return carritoItemRepository.save(newItem);
            }
        }
        return null;
    }

    public CarritoItem updateCantidad(Long itemId, int cantidad) {
        Optional<CarritoItem> item = carritoItemRepository.findById(itemId);
        if (item.isPresent()) {
            CarritoItem carritoItem = item.get();
            carritoItem.setCantidad(cantidad);
            return carritoItemRepository.save(carritoItem);
        }
        return null;
    }

    public void deleteItem(Long itemId) {
        carritoItemRepository.deleteById(itemId);
    }

    @Transactional
    public void clearCarrito(User usuario) {
        carritoItemRepository.deleteByUsuario(usuario);
    }

    public double calcularTotal(User usuario) {
        List<CarritoItem> items = findByUsuario(usuario);
        return items.stream()
                .mapToDouble(CarritoItem::getSubtotal)
                .sum();
    }

    public int contarItems(User usuario) {
        return findByUsuario(usuario).size();
    }
}
