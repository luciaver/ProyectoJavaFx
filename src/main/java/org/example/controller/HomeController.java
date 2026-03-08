package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.example.model.Deporte;
import org.example.model.Pista;
import org.example.model.Usuario;
import org.example.service.PistaService;

import java.util.List;

public class HomeController {

    @FXML private Label     lblBienvenida;
    @FXML private Label     lblTitulo;
    @FXML private Label     lblSubtitulo;
    @FXML private FlowPane  flowPistas;
    @FXML private TextField txtBuscar;
    @FXML private HBox      barraFiltros;
    @FXML private VBox      panelAdmin;
    @FXML private Button    btnPistas;
    @FXML private Button    btnMisReservas;

    private Usuario            usuarioActual;
    private final PistaService svc = new PistaService();
    private String             filtroActivo = "Todos";

    public void inicializar(Usuario usuario) {
        this.usuarioActual = usuario;
        lblBienvenida.setText("Hola, " + usuario.getNombre());

        if (usuario.isAdmin()) {
            panelAdmin.setVisible(true);
            panelAdmin.setManaged(true);
        }

        crearBotonesFiltro();
        cargarPistas();

        PistaService.pistas.addListener(
                (javafx.collections.ListChangeListener<Pista>) c -> onFiltrar());
    }

    // Filtros
    private void crearBotonesFiltro() {
        barraFiltros.getChildren().clear();
        barraFiltros.getChildren().add(crearBtnFiltro("Todos"));
        for (Deporte d : svc.getDeportes()) {
            barraFiltros.getChildren().add(crearBtnFiltro(d.getNombre()));
        }
        actualizarEstiloBotones();
    }

    private Button crearBtnFiltro(String etiqueta) {
        Button btn = new Button(etiqueta);
        btn.getStyleClass().add("filtro-btn");
        btn.setOnAction(e -> {
            filtroActivo = etiqueta;
            actualizarEstiloBotones();
            onFiltrar();
        });
        return btn;
    }

    private void actualizarEstiloBotones() {
        for (javafx.scene.Node n : barraFiltros.getChildren()) {
            if (n instanceof Button btn) {
                btn.getStyleClass().remove("filtro-btn-activo");
                if (btn.getText().equals(filtroActivo))
                    btn.getStyleClass().add("filtro-btn-activo");
            }
        }
    }

    //Pistas
    private void cargarPistas() {
        mostrarPistas(svc.getPistas());
    }

    private void mostrarPistas(List<Pista> pistas) {
        flowPistas.getChildren().clear();
        if (pistas.isEmpty()) {
            Label lbl = new Label("No se encontraron instalaciones");
            lbl.getStyleClass().add("sin-resultados");
            flowPistas.getChildren().add(lbl);
            return;
        }
        for (Pista p : pistas) {
            flowPistas.getChildren().add(crearCard(p));
        }
    }

    private VBox crearCard(Pista pista) {
        VBox card = new VBox(0);
        card.getStyleClass().add("card-pista");
        card.setPrefWidth(270);

        ImageView imgView = new ImageView();
        imgView.setFitWidth(270);
        imgView.setFitHeight(160);
        imgView.setPreserveRatio(false);
        try {
            var res = getClass().getResource(imagenDeporte(pista.getDeporte().getNombre()));
            if (res != null) imgView.setImage(new Image(res.toExternalForm()));
        } catch (Exception ignored) {}

        VBox body = new VBox(8);
        body.getStyleClass().add("card-body");
        body.setPadding(new Insets(14, 16, 16, 16));

        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);
        Label nombre = new Label(pista.getNombre());
        nombre.getStyleClass().add("card-nombre");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label badge = new Label(pista.getEstado());
        badge.getStyleClass().add("LIBRE".equals(pista.getEstado()) ? "badge-libre" : "badge-ocupada");
        row.getChildren().addAll(nombre, spacer, badge);

        Label deporteLbl = new Label(pista.getDeporte().getNombre());
        deporteLbl.getStyleClass().add("card-deporte");

        Label descLbl = new Label(pista.getDescripcion() == null ? "" : pista.getDescripcion());
        descLbl.getStyleClass().add("card-detalle");
        descLbl.setWrapText(true);

        Label precioLbl = new Label(String.format("%.2f €/hora", pista.getPrecio()));
        precioLbl.getStyleClass().add("card-precio");

        Region gap = new Region();
        VBox.setVgrow(gap, Priority.ALWAYS);

        boolean libre = "LIBRE".equals(pista.getEstado());
        Button btnReservar = new Button(libre ? "Reservar" : "No disponible");
        btnReservar.getStyleClass().add(libre ? "boton-reservar" : "boton-reservar-disabled");
        btnReservar.setMaxWidth(Double.MAX_VALUE);
        btnReservar.setDisable(!libre);
        btnReservar.setOnAction(e -> abrirReserva(pista));

        body.getChildren().addAll(row, deporteLbl, descLbl, precioLbl, gap, btnReservar);
        card.getChildren().addAll(imgView, body);
        return card;
    }

    private String imagenDeporte(String deporte) {
        if (deporte == null) return "/org/example/images/Baloncesto (1).jpg";
        return switch (deporte.toLowerCase()) {
            case "padel"      -> "/org/example/images/padel.jpg";
            case "tenis"      -> "/org/example/images/tenis.jpg";
            case "futbol"     -> "/org/example/images/futbol.jpg";
            case "baloncesto" -> "/org/example/images/Baloncesto (1).jpg";
            default           -> "/org/example/images/Baloncesto (1).jpg";
        };
    }

    @FXML
    private void onFiltrar() {
        mostrarPistas(svc.buscar(txtBuscar.getText(), filtroActivo));
    }

    @FXML
    private void onVerPistas() {
        lblTitulo.setText("Catálogo de Instalaciones");
        lblSubtitulo.setText("Selecciona una pista para reservar");
        activarBtn(btnPistas);
        filtroActivo = "Todos";
        crearBotonesFiltro();
        cargarPistas();
    }

    @FXML
    private void onVerMisReservas() {
        lblTitulo.setText("Mis Reservas");
        lblSubtitulo.setText("Historial y próximas reservas");
        activarBtn(btnMisReservas);
        navegarA("/org/example/fxml/mis_reservas.fxml",
                ctrl -> { if (ctrl instanceof MisReservasController c) c.inicializar(usuarioActual); });
    }

    @FXML
    private void onIrAdmin() {
        navegarA("/org/example/fxml/admin.fxml",
                ctrl -> { if (ctrl instanceof AdminController c) c.inicializar(usuarioActual); });
    }

    @FXML
    private void onCerrarSesion() {
        navegarA("/org/example/fxml/login.fxml", ctrl -> {});
    }

    private void abrirReserva(Pista pista) {
        try {
            Stage stage = (Stage) flowPistas.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/fxml/pista.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(
                    getClass().getResource("/org/example/css/styles.css").toExternalForm());
            loader.<PistasController>getController().inicializar(pista, usuarioActual);
            stage.setScene(scene);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void navegarA(String fxml, java.util.function.Consumer<Object> setup) {
        try {
            Stage stage = (Stage) flowPistas.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(
                    getClass().getResource("/org/example/css/styles.css").toExternalForm());
            setup.accept(loader.getController());
            stage.setScene(scene);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void activarBtn(Button activo) {
        btnPistas.getStyleClass().remove("sidebar-btn-activo");
        btnMisReservas.getStyleClass().remove("sidebar-btn-activo");
        activo.getStyleClass().add("sidebar-btn-activo");
    }
}