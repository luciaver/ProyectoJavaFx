package org.example.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "apellidos", nullable = false)
    private String apellidos;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "rol", nullable = false)
    private String rol;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reserva> reservas = new ArrayList<>();

    public Usuario() {}

    public Usuario(String nombre, String apellidos, String email, String password) {
        this.nombre    = nombre;
        this.apellidos = apellidos;
        this.email     = email;
        this.password  = password;
        this.rol       = "SOCIO";
    }

    public Usuario(String nombre, String apellidos, String email, String password, boolean admin) {
        this.nombre    = nombre;
        this.apellidos = apellidos;
        this.email     = email;
        this.password  = password;
        this.rol       = admin ? "ADMIN" : "SOCIO";
    }

    public int    getId()          { return id; }
    public void   setId(int id)    { this.id = id; }

    public String getNombre()               { return nombre; }
    public void   setNombre(String nombre)  { this.nombre = nombre; }

    public String getApellidos()                  { return apellidos; }
    public void   setApellidos(String apellidos)  { this.apellidos = apellidos; }

    public String getEmail()                { return email; }
    public void   setEmail(String email)    { this.email = email; }

    public String getPassword()                 { return password; }
    public void   setPassword(String password)  { this.password = password; }

    public String getRol()              { return rol; }
    public void   setRol(String rol)    { this.rol = rol; }

    public List<Reserva> getReservas()                    { return reservas; }
    public void          setReservas(List<Reserva> list)  { this.reservas = list; }

    public boolean isAdmin()                 { return "ADMIN".equalsIgnoreCase(rol); }
    public void    setAdmin(boolean admin)   { this.rol = admin ? "ADMIN" : "SOCIO"; }
}