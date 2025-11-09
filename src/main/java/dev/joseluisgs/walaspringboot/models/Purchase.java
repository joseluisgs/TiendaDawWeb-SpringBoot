package dev.joseluisgs.walaspringboot.models;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCompra;

    @ManyToOne
    private User propietario;

    // Relación bidireccional con productos
    @OneToMany(mappedBy = "compra", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Product> productos = new ArrayList<>();

    // Campo calculado para total
    @Transient
    private Double total;

    public Purchase() {
    }

    public Purchase(User propietario) {
        this.propietario = propietario;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(Date fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

    public User getPropietario() {
        return propietario;
    }

    public void setPropietario(User propietario) {
        this.propietario = propietario;
    }

    public List<Product> getProductos() {
        return productos;
    }

    public void setProductos(List<Product> productos) {
        this.productos = productos;
    }

    public Double getTotal() {
        if (this.total == null) {
            calculateTotal();
        }
        return this.total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    // Método helper para calcular total
    public void calculateTotal() {
        if (productos != null && !productos.isEmpty()) {
            this.total = productos.stream()
                    .filter(producto -> !producto.getDeleted()) // Excluir productos soft-deleted
                    .mapToDouble(Product::getPrecio)
                    .sum();
        } else {
            this.total = 0.0;
        }
    }

    // Método helper para añadir producto
    public void addProduct(Product product) {
        productos.add(product);
        product.setCompra(this);
        this.total = null; // Reset para recalcular
    }

    // Método helper para remover producto
    public void removeProduct(Product product) {
        productos.remove(product);
        product.setCompra(null);
        this.total = null; // Reset para recalcular
    }

    // Getter para cantidad de productos
    public int getCantidadProductos() {
        return productos != null ? productos.size() : 0;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Purchase compra = (Purchase) o;
        return id == compra.id &&
                Objects.equals(fechaCompra, compra.fechaCompra) &&
                Objects.equals(propietario, compra.propietario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fechaCompra, propietario);
    }

    @Override
    public String toString() {
        return "Purchase{" +
                "id=" + id +
                ", fechaCompra=" + fechaCompra +
                ", propietario=" + propietario +
                ", cantidadProductos=" + getCantidadProductos() +
                ", total=" + getTotal() +
                '}';
    }
}
