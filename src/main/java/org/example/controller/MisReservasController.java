package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.example.model.Reserva;
import org.example.model.Usuario;
import org.example.service.PistaService;
import org.example.service.ReservaService;

public class MisReservasController {

    @FXML private VBox vboxReservas;

    private Usuario usuario;
    private final ReservaService svc = new ReservaService();

    public void inicializar(Usuario usuario) {
        this.usuario = usuario;
        cargar();

        PistaService.reservas.addListener(
                (javafx.collections.ListChangeListener<Reserva>) c -> cargar());
    }

    private void cargar() {
        vboxReservas.getChildren().clear();
        var lista = svc.getReservasUsuario(usuario);

        if (lista.isEmpty()) {
            Label lbl = new Label("No tienes reservas aún.");
            lbl.getStyleClass().add("home-subtitulo");
            vboxReservas.getChildren().add(lbl);
            return;
        }

        for (Reserva r : lista) {
            vboxReservas.getChildren().add(crearFila(r));
        }
    }

    private HBox crearFila(Reserva r) {
        HBox fila = new HBox(16);
        fila.getStyleClass().add("reserva-item");
        fila.setPadding(new Insets(12, 16, 12, 16));

        Label info = new Label(r.getPista().getNombre()
                + " — " + r.getFecha()
                + "  " + r.getHoraInicio() + " – " + r.getHoraFin()
                + "   (" + String.format("%.2f €", r.getPista().getPrecio()) + ")");
        info.getStyleClass().add("etiqueta");
        HBox.setHgrow(info, Priority.ALWAYS);

        Button btnCancelar = new Button("Cancelar");
        btnCancelar.getStyleClass().add("boton-registro");
        btnCancelar.setOnAction(e -> {
            Alert conf = new Alert(Alert.AlertType.CONFIRMATION,
                    "¿Cancelar reserva de " + r.getPista().getNombre() + "?",
                    ButtonType.YES, ButtonType.NO);
            conf.setHeaderText(null);
            conf.showAndWait().ifPresent(b -> {
                if (b == ButtonType.YES) svc.cancelar(r.getId());
                // cargar() se llama automáticamente por el listener
            });
        });

        fila.getChildren().addAll(info, btnCancelar);
        return fila;
    }

    @FXML private void onVolver() {
        try {
            Stage stage = (Stage) vboxReservas.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/fxml/home.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(
                    getClass().getResource("/org/example/css/styles.css").toExternalForm());
            loader.<HomeController>getController().inicializar(usuario);
            stage.setScene(scene);
        } catch (Exception e) { e.printStackTrace(); }
    }
}