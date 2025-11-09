package dev.joseluisgs.walaspringboot.services;

import dev.joseluisgs.walaspringboot.models.Product;
import dev.joseluisgs.walaspringboot.models.Rating;
import dev.joseluisgs.walaspringboot.models.User;
import dev.joseluisgs.walaspringboot.repositories.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    public List<Rating> findByProducto(Product producto) {
        return ratingRepository.findByProducto(producto);
    }

    public Double getAverageRating(Product producto) {
        return ratingRepository.getAverageRatingByProducto(producto);
    }

    public Long getCountRatings(Product producto) {
        return ratingRepository.getCountByProducto(producto);
    }

    public boolean yaValorado(Long userId, Long productoId) {
        User usuario = userService.findById(userId);
        Product producto = productService.findById(productoId);
        if (usuario != null && producto != null) {
            return ratingRepository.findByUsuarioAndProducto(usuario, producto).isPresent();
        }
        return false;
    }

    public Optional<Rating> getValoracionUsuario(Long userId, Long productoId) {
        User usuario = userService.findById(userId);
        Product producto = productService.findById(productoId);
        if (usuario != null && producto != null) {
            return ratingRepository.findByUsuarioAndProducto(usuario, producto);
        }
        return Optional.empty();
    }

    public Rating addRating(User usuario, Long productoId, int puntuacion, String comentario) {
        Product producto = productService.findById(productoId);
        if (producto != null) {
            // Check if user already rated this product
            Optional<Rating> existingRating = ratingRepository.findByUsuarioAndProducto(usuario, producto);
            if (existingRating.isPresent()) {
                return null; // User already rated this product
            }
            Rating rating = new Rating(usuario, producto, puntuacion, comentario);
            return ratingRepository.save(rating);
        }
        return null;
    }

    public void deleteRating(Long ratingId) {
        ratingRepository.deleteById(ratingId);
    }
}
