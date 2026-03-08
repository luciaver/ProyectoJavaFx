package org.example.model;

import jakarta.persistence.*;

@Entity
@Table(name = "pistas")
public class Pista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "deporte_id", nullable = false)
    private Deporte deporte;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "precio", nullable = false)
    private double precio;

    @Column(name = "estado", nullable = false)
    private String estado;

    public Pista() {}

    public Pista(String nombre, Deporte deporte, String descripcion, double precio, String estado) {
        this.nombre      = nombre;
        this.deporte     = deporte;
        this.descripcion = descripcion;
        this.precio      = precio;
        this.estado      = estado;
    }

    public int     getId()          { return id; }
    public void    setId(int id)    { this.id = id; }
    public String  getNombre()      { return nombre; }
    public Deporte getDeporte()     { return deporte; }
    public String  getDescripcion() { return descripcion; }
    public double  getPrecio()      { return precio; }
    public String  getEstado()      { return estado; }

    public void setNombre(String nombre)           { this.nombre = nombre; }
    public void setDeporte(Deporte deporte)        { this.deporte = deporte; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setPrecio(double precio)           { this.precio = precio; }
    public void setEstado(String estado)           { this.estado = estado; }
}