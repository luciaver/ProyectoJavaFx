package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.example.model.Pista;
import org.example.model.Usuario;
import org.example.service.ReservaService;

import java.time.LocalDate;
import java.time.LocalTime;

public class PistasController {

    @FXML private ImageView        imgPista;
    @FXML private Label            lblDeportePista;
    @FXML private Label            lblNombrePista;
    @FXML private Label            lblPrecioPista;
    @FXML private Label            lblDescripcionPista;
    @FXML private DatePicker       dpFechaInicio;
    @FXML private DatePicker       dpFechaFin;
    @FXML private ComboBox<String> cbHoraInicio;
    @FXML private ComboBox<String> cbHoraFin;
    @FXML private Label            lblMensaje;
    @FXML private Label            lblTotal;
    @FXML private Button           btnConfirmar;

    private Pista   pista;
    private Usuario usuario;
    private final ReservaService svc = new ReservaService();

    private static final String[] HORAS = {
            "08:00","09:00","10:00","11:00","12:00","13:00",
            "14:00","15:00","16:00","17:00","18:00","19:00","20:00","21:00"
    };

    public void inicializar(Pista pista, Usuario usuario) {
        this.pista   = pista;
        this.usuario = usuario;

        lblNombrePista.setText(pista.getNombre());
        lblDeportePista.setText(pista.getDeporte().getNombre());
        lblPrecioPista.setText(String.format("%.2f €/hora", pista.getPrecio()));
        lblDescripcionPista.setText(
                pista.getDescripcion() == null ? "" : pista.getDescripcion());

        try {
            var res = getClass().getResource(imagenDeporte(pista.getDeporte().getNombre()));
            if (res != null) imgPista.setImage(new Image(res.toExternalForm()));
        } catch (Exception ignored) {}

        // ── Horas
        cbHoraInicio.getItems().addAll(HORAS);
        cbHoraFin.getItems().addAll(HORAS);

        // Valores por defecto
        cbHoraInicio.setValue("10:00");
        cbHoraFin.setValue("11:00");

        // Fechas
        dpFechaInicio.setValue(LocalDate.now());
        dpFechaFin.setValue(LocalDate.now());
        dpFechaFin.setDisable(true); // fecha fin = misma que inicio

        cbHoraInicio.setOnAction(e -> actualizarTotal());
        cbHoraFin.setOnAction(e    -> actualizarTotal());
        dpFechaInicio.setOnAction(e -> {
            dpFechaFin.setValue(dpFechaInicio.getValue());
            actualizarTotal();
        });

        actualizarTotal();
    }

    private void actualizarTotal() {
        if (pista == null) return;
        if (cbHoraInicio.getValue() == null || cbHoraFin.getValue() == null) return;
        try {
            LocalTime ini = LocalTime.parse(cbHoraInicio.getValue());
            LocalTime fin = LocalTime.parse(cbHoraFin.getValue());
            if (fin.isAfter(ini)) {
                double horas = (double)(fin.toSecondOfDay() - ini.toSecondOfDay()) / 3600.0;
                lblTotal.setText(String.format("%.2f €", horas * pista.getPrecio()));
                btnConfirmar.setDisable(false);
            } else {
                lblTotal.setText("--");
                btnConfirmar.setDisable(true);
            }
        } catch (Exception e) {
            lblTotal.setText("--");
            btnConfirmar.setDisable(true);
        }
    }

    @FXML private void onConfirmar() {
        if (dpFechaInicio.getValue() == null) {
            mostrarError("Selecciona una fecha"); return;
        }
        if (dpFechaInicio.getValue().isBefore(LocalDate.now())) {
            mostrarError("No puedes reservar en fechas pasadas"); return;
        }
        LocalTime ini = LocalTime.parse(cbHoraInicio.getValue());
        LocalTime fin = LocalTime.parse(cbHoraFin.getValue());
        if (!fin.isAfter(ini)) {
            mostrarError("La hora fin debe ser posterior al inicio"); return;
        }

        boolean ok = svc.reservar(usuario, pista, dpFechaInicio.getValue(), ini, fin);
        if (ok) {
            mostrarExito("¡Reserva confirmada!");
            btnConfirmar.setDisable(true);
        } else {
            mostrarError("La pista ya está ocupada en ese horario");
        }
    }

    @FXML private void onVolver() {
        try {
            Stage stage = (Stage) lblNombrePista.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/fxml/home.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(
                    getClass().getResource("/org/example/css/styles.css").toExternalForm());
            loader.<HomeController>getController().inicializar(usuario);
            stage.setScene(scene);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private String imagenDeporte(String d) {
        if (d == null) return "/org/example/images/Baloncesto (1).jpg";
        return switch (d.toLowerCase()) {
            case "padel"      -> "/org/example/images/padel.jpg";
            case "tenis"      -> "/org/example/images/tenis.jpg";
            case "futbol"     -> "/org/example/images/futbol.jpg";
            case "baloncesto" -> "/org/example/images/Baloncesto (1).jpg";
            default           -> "/org/example/images/Baloncesto (1).jpg";
        };
    }

    private void mostrarError(String msg) {
        lblMensaje.setText(msg);
        lblMensaje.getStyleClass().setAll("mensaje", "mensaje-error");
    }
    private void mostrarExito(String msg) {
        lblMensaje.setText(msg);
        lblMensaje.getStyleClass().setAll("mensaje", "mensaje-exito");
    }
}