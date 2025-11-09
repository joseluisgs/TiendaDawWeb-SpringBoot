package com.joseluisgs.walaspringboot.models;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;
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
                '}';
    }
}
