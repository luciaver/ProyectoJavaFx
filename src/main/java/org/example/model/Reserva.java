package org.example.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pista_id", nullable = false)
    private Pista pista;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    @Column(name = "total", nullable = false)
    private double total;

    public Reserva() {}

    public Reserva(Usuario usuario, Pista pista, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin) {
        this.usuario    = usuario;
        this.pista      = pista;
        this.fecha      = fecha;
        this.horaInicio = horaInicio;
        this.horaFin    = horaFin;
        double horas = (double)(horaFin.toSecondOfDay() - horaInicio.toSecondOfDay()) / 3600.0;
        this.total = horas * pista.getPrecio();
    }

    public int       getId()         { return id; }
    public Usuario   getUsuario()    { return usuario; }
    public Pista     getPista()      { return pista; }
    public LocalDate getFecha()      { return fecha; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public LocalTime getHoraFin()    { return horaFin; }
    public double    getTotal()      { return total; }

    public void setFecha(LocalDate fecha)           { this.fecha = fecha; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }
    public void setHoraFin(LocalTime horaFin)       { this.horaFin = horaFin; }
    public void setTotal(double total)              { this.total = total; }
}