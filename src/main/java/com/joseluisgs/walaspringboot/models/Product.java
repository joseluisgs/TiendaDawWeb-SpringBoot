package com.joseluisgs.walaspringboot.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class Product {
    public static final String DEFAULT_IMAGE = "https://cdn-icons-png.flaticon.com/512/5617/5617585.png";
    public static final String DEFAULT_IMAGE_URL = "https://cdn-icons-png.flaticon.com/512/5617/5617585.png";
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotEmpty
    private String nombre;

    @Min(value=0, message="{producto.precio.mayorquecero}")
    private float precio;

    private String imagen;

    @Column(length = 1000)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "categoria", nullable = false)
    private ProductCategory categoria;

    @ManyToOne
    private User propietario;

    @ManyToOne
    private Purchase compra;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by")
    private String deletedBy;

    @Column(name = "views", nullable = false)
    private Long views = 0L;

    public Product() {
    }

    public Product(String nombre, float precio, String imagen, User propietario) {
        this.nombre = nombre;
        this.precio = precio;
        this.imagen = imagen;
        this.propietario = propietario;
    }

    public Product(String nombre, float precio, String imagen, String descripcion, String categoria, User propietario) {
        this.nombre = nombre;
        this.precio = precio;
        this.imagen = imagen;
        this.descripcion = descripcion;
        this.categoria = ProductCategory.fromString(categoria);
        this.propietario = propietario;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public User getPropietario() {
        return propietario;
    }

    public void setPropietario(User propietario) {
        this.propietario = propietario;
    }

    public Purchase getCompra() {
        return compra;
    }

    public void setCompra(Purchase compra) {
        this.compra = compra;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public ProductCategory getCategoria() {
        return categoria;
    }

    public void setCategoria(ProductCategory categoria) {
        this.categoria = categoria;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }

    public void softDelete(String deletedBy) {
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deletedBy;
    }

    public void restore() {
        this.deleted = false;
        this.deletedAt = null;
        this.deletedBy = null;
    }

    public boolean isActive() {
        return !deleted;
    }

    public Long getViews() {
        return views;
    }

    public void setViews(Long views) {
        this.views = views;
    }

    public void incrementViews() {
        this.views = this.views + 1;
    }

    /**
     * Método helper para simplificar templates
     * Retorna la URL de la imagen o la imagen por defecto si no hay imagen
     */
    public String getImagenOrDefault() {
        if (this.imagen != null && !this.imagen.isEmpty()) {
            if (this.imagen.startsWith("http")) {
                return this.imagen;
            } else {
                return "/files/" + this.imagen;
            }
        }
        return DEFAULT_IMAGE_URL;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product producto = (Product) o;
        return id == producto.id &&
                Float.compare(producto.precio, precio) == 0 &&
                Objects.equals(nombre, producto.nombre) &&
                Objects.equals(imagen, producto.imagen) &&
                Objects.equals(propietario, producto.propietario) &&
                Objects.equals(compra, producto.compra);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nombre, precio, imagen, propietario, compra);
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", precio=" + precio +
                ", imagen='" + imagen + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", categoria=" + categoria +
                ", propietario=" + propietario +
                ", compra=" + compra +
                '}';
    }
}
