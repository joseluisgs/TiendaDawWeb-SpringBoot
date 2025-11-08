package com.joseluisgs.walaspringboot.services;

import com.joseluisgs.walaspringboot.models.Rating;
import com.joseluisgs.walaspringboot.models.Product;
import com.joseluisgs.walaspringboot.models.User;
import com.joseluisgs.walaspringboot.repositories.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private ProductService productService;

    public List<Rating> findByProducto(Product producto) {
        return ratingRepository.findByProducto(producto);
    }

    public Double getAverageRating(Product producto) {
        return ratingRepository.getAverageRatingByProducto(producto);
    }

    public Long getCountRatings(Product producto) {
        return ratingRepository.getCountByProducto(producto);
    }

    public Rating addRating(User usuario, Long productoId, int puntuacion, String comentario) {
        Product producto = productService.findById(productoId);
        if (producto != null) {
            Rating rating = new Rating(usuario, producto, puntuacion, comentario);
            return ratingRepository.save(rating);
        }
        return null;
    }

    public void deleteRating(Long ratingId) {
        ratingRepository.deleteById(ratingId);
    }
}
