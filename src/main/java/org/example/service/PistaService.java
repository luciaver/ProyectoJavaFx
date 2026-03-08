package org.example.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.dao.DeporteDAO;
import org.example.dao.PistaDAO;
import org.example.dao.ReservaDAO;
import org.example.dao.UsuarioDAO;
import org.example.model.Deporte;
import org.example.model.Pista;
import org.example.model.Reserva;
import org.example.model.Usuario;

import java.util.List;
import java.util.stream.Collectors;

public class PistaService {

    public static final ObservableList<Deporte> deportes = FXCollections.observableArrayList();
    public static final ObservableList<Pista>   pistas   = FXCollections.observableArrayList();
    public static final ObservableList<Usuario> usuarios = FXCollections.observableArrayList();
    public static final ObservableList<Reserva> reservas = FXCollections.observableArrayList();

    private static final DeporteDAO deporteDAO = new DeporteDAO();
    private static final PistaDAO   pistaDAO   = new PistaDAO();
    private static final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private static final ReservaDAO reservaDAO = new ReservaDAO();

    static {
        List<Deporte> deps = deporteDAO.listarTodos();
        if (deps.isEmpty()) {
            Deporte padel      = new Deporte("Padel");
            Deporte tenis      = new Deporte("Tenis");
            Deporte futbol     = new Deporte("Futbol");
            Deporte baloncesto = new Deporte("Baloncesto");
            deporteDAO.guardar(padel);
            deporteDAO.guardar(tenis);
            deporteDAO.guardar(futbol);
            deporteDAO.guardar(baloncesto);
            deportes.addAll(padel, tenis, futbol, baloncesto);

            List<Pista> pistasIniciales = List.of(
                    new Pista("Pista Padel 1",  padel,      "Pista cubierta con césped artificial", 12.0, "LIBRE"),
                    new Pista("Pista Padel 2",  padel,      "Pista exterior panorámica",            10.0, "LIBRE"),
                    new Pista("Pista Tenis 1",  tenis,      "Pista de tierra batida",               15.0, "LIBRE"),
                    new Pista("Pista Tenis 2",  tenis,      "Pista de cemento cubierta",            15.0, "LIBRE"),
                    new Pista("Campo Fútbol",   futbol,     "Campo de hierba natural 11x11",        30.0, "LIBRE"),
                    new Pista("Fútbol Sala",    futbol,     "Pista cubierta de fútbol sala",        20.0, "LIBRE"),
                    new Pista("Cancha NBA",     baloncesto, "Cancha de parquet interior",           18.0, "LIBRE")
            );
            pistasIniciales.forEach(pistaDAO::guardar);
            pistas.addAll(pistasIniciales);
        } else {
            deportes.addAll(deps);
            pistas.addAll(pistaDAO.obtenerTodas());
        }

        List<Usuario> users = usuarioDAO.obtenerTodos();
        if (users.isEmpty()) {
            Usuario admin = new Usuario("Admin", "Sistema", "admin@multisports.com", "1234", true);
            usuarioDAO.guardar(admin);
            usuarios.add(admin);
        } else {
            usuarios.addAll(users);
        }

        reservas.addAll(reservaDAO.obtenerTodas());
    }

    public ObservableList<Deporte> getDeportes() { return deportes; }
    public ObservableList<Pista>   getPistas()   { return pistas; }

    public List<Pista> buscar(String texto, String deporte) {
        String txt = texto == null ? "" : texto.trim().toLowerCase();
        return pistas.stream()
                .filter(p -> {
                    boolean dep = "Todos".equals(deporte)
                            || p.getDeporte().getNombre().equalsIgnoreCase(deporte);
                    boolean tex = txt.isEmpty()
                            || p.getNombre().toLowerCase().contains(txt)
                            || p.getDeporte().getNombre().toLowerCase().contains(txt);
                    return dep && tex;
                })
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    public void guardar(Pista p) {
        pistaDAO.guardar(p);
        pistas.add(p);
    }

    public void actualizar(Pista editada) {
        pistaDAO.actualizar(editada);
        for (int i = 0; i < pistas.size(); i++) {
            if (pistas.get(i).getId() == editada.getId()) {
                pistas.set(i, editada);
                return;
            }
        }
    }

    public void eliminar(int id) {
        pistaDAO.eliminar(id);
        pistas.removeIf(p -> p.getId() == id);
    }
}