package org.example.service;

import org.example.model.Deporte;
import org.example.model.Pista;
import org.example.model.Reserva;
import org.example.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class ReservaServiceTest {

    private Pista pista;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        Deporte deporte = new Deporte("Padel");
        pista   = new Pista("Pista Test", deporte, "desc", 10.0, "LIBRE");
        usuario = new Usuario("Ana", "Lopez", "ana@test.com", "1234");
        PistaService.reservas.clear();
    }

    @Test
    void totalReservaCalculadoCorrectamente() {
        Reserva r = new Reserva(usuario, pista,
                LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(12, 0));
        assertEquals(20.0, r.getTotal(), 0.01);
    }

    @Test
    void reservaSinConflictoSeAnadeALaLista() {
        PistaService.reservas.add(new Reserva(usuario, pista,
                LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(11, 0)));

        boolean conflicto = PistaService.reservas.stream().anyMatch(r ->
                r.getPista().getNombre().equals(pista.getNombre())
                        && r.getFecha().equals(LocalDate.now())
                        && LocalTime.of(11, 0).isBefore(r.getHoraFin())
                        && LocalTime.of(12, 0).isAfter(r.getHoraInicio()));

        assertFalse(conflicto);
    }

    @Test
    void reservaConConflictoDetectada() {
        PistaService.reservas.add(new Reserva(usuario, pista,
                LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(12, 0)));

        boolean conflicto = PistaService.reservas.stream().anyMatch(r ->
                r.getPista().getNombre().equals(pista.getNombre())
                        && r.getFecha().equals(LocalDate.now())
                        && LocalTime.of(11, 0).isBefore(r.getHoraFin())
                        && LocalTime.of(13, 0).isAfter(r.getHoraInicio()));

        assertTrue(conflicto);
    }

    @Test
    void cancelarReservaLaEliminaDeLaLista() {
        Reserva r = new Reserva(usuario, pista,
                LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(11, 0));
        PistaService.reservas.add(r);
        PistaService.reservas.removeIf(x -> x == r);
        assertTrue(PistaService.reservas.isEmpty());
    }
}