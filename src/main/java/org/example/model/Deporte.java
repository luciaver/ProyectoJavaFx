package org.example.model;

import jakarta.persistence.*;

@Entity
@Table(name = "deportes")
public class Deporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nombre", nullable = false, unique = true)
    private String nombre;

    public Deporte() {}

    public Deporte(String nombre) {
        this.nombre = nombre;
    }

    public int    getId()     { return id; }
    public String getNombre() { return nombre; }
    public void   setNombre(String nombre) { this.nombre = nombre; }

    @Override
    public String toString() { return nombre; }
}