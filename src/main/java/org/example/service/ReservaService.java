package org.example.service;

import org.example.dao.ReservaDAO;
import org.example.model.Pista;
import org.example.model.Reserva;
import org.example.model.Usuario;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ReservaService {

    private final ReservaDAO dao = new ReservaDAO();

    public boolean reservar(Usuario usuario, Pista pista, LocalDate fecha, LocalTime inicio, LocalTime fin) {
        if (dao.hayConflicto(pista.getId(), fecha, inicio, fin)) return false;
        Reserva r = new Reserva(usuario, pista, fecha, inicio, fin);
        boolean ok = dao.guardar(r);
        if (ok) {
            PistaService.reservas.setAll(dao.obtenerTodas());
        }
        return ok;
    }

    public List<Reserva> getTodas() {
        return PistaService.reservas;
    }

    public List<Reserva> getReservasUsuario(Usuario usuario) {
        return dao.obtenerPorUsuario(usuario.getEmail());
    }

    public void cancelar(int id) {
        dao.eliminar(id);
        PistaService.reservas.setAll(dao.obtenerTodas());
    }
}