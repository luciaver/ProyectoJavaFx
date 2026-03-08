package org.example.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.example.dao.ReservaDAO;
import org.example.dao.UsuarioDAO;
import org.example.model.*;
import org.example.service.PistaService;
import org.example.service.ReservaService;

import java.time.LocalTime;

public class AdminController {

    @FXML private Label lblSesion;
    @FXML private Label lblKpiPistas;
    @FXML private Label lblKpiLibres;
    @FXML private Label lblKpiReservas;
    @FXML private BarChart<String, Number> barChart;

    @FXML private TableView<Pista>             tablaPistas;
    @FXML private TableColumn<Pista, String>   colNombre;
    @FXML private TableColumn<Pista, String>   colDeporte;
    @FXML private TableColumn<Pista, String>   colDescripcion;
    @FXML private TableColumn<Pista, String>   colPrecio;
    @FXML private TableColumn<Pista, String>   colEstado;
    @FXML private TableColumn<Pista, Void>     colAcciones;

    @FXML private TableView<Usuario>           tablaUsuarios;
    @FXML private TableColumn<Usuario, String> colUsNombre;
    @FXML private TableColumn<Usuario, String> colUsEmail;
    @FXML private TableColumn<Usuario, Void>   colUsAcciones;

    @FXML private TableView<Reserva>           tablaReservas;
    @FXML private TableColumn<Reserva, String> colResPista;
    @FXML private TableColumn<Reserva, String> colResUsuario;
    @FXML private TableColumn<Reserva, String> colResInicio;
    @FXML private TableColumn<Reserva, String> colResFin;
    @FXML private TableColumn<Reserva, Void>   colResAcciones;

    private final PistaService   pistaSvc   = new PistaService();
    private final ReservaService reservaSvc = new ReservaService();
    private final UsuarioDAO     usuarioDAO = new UsuarioDAO();
    private Usuario usuarioActual;

    private static final String[] HORAS = {
            "08:00","09:00","10:00","11:00","12:00","13:00",
            "14:00","15:00","16:00","17:00","18:00","19:00","20:00","21:00"
    };

    public void inicializar(Usuario usuario) {
        this.usuarioActual = usuario;
        lblSesion.setText("  — " + usuario.getNombre());

        setupTablaPistas();
        setupTablaUsuarios();
        setupTablaReservas();
        actualizarKpis();
        actualizarGrafica();

        PistaService.pistas.addListener(
                (javafx.collections.ListChangeListener<Pista>) c -> { actualizarKpis(); actualizarGrafica(); });
        PistaService.reservas.addListener(
                (javafx.collections.ListChangeListener<Reserva>) c -> { actualizarKpis(); actualizarGrafica(); });
        PistaService.usuarios.addListener(
                (javafx.collections.ListChangeListener<Usuario>) c -> actualizarKpis());
    }

    private void actualizarKpis() {
        lblKpiPistas.setText(String.valueOf(PistaService.pistas.size()));
        lblKpiLibres.setText(String.valueOf(
                PistaService.pistas.stream().filter(p -> "LIBRE".equals(p.getEstado())).count()));
        lblKpiReservas.setText(String.valueOf(PistaService.reservas.size()));
    }

    private void actualizarGrafica() {
        barChart.getData().clear();
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        PistaService.deportes.forEach(d -> {
            long count = PistaService.reservas.stream()
                    .filter(r -> r.getPista().getDeporte().getId() == d.getId()).count();
            serie.getData().add(new XYChart.Data<>(d.getNombre(), count));
        });
        barChart.getData().add(serie);
    }

    private void setupTablaPistas() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDeporte.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getDeporte().getNombre()));
        colDescripcion.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getDescripcion() == null ? "" : c.getValue().getDescripcion()));
        colPrecio.setCellValueFactory(c -> new SimpleStringProperty(
                String.format("%.2f €", c.getValue().getPrecio())));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        colAcciones.setCellFactory(col -> new TableCell<>() {
            private final Button btnEditar   = new Button("Editar");
            private final Button btnEliminar = new Button("Borrar");
            {
                btnEditar.getStyleClass().add("btn-tabla-editar");
                btnEliminar.getStyleClass().add("btn-tabla-borrar");
                btnEditar.setOnAction(e ->
                        mostrarDialogoPista(getTableView().getItems().get(getIndex())));
                btnEliminar.setOnAction(e -> {
                    Pista p = getTableView().getItems().get(getIndex());
                    if (confirmar("¿Borrar la pista \"" + p.getNombre() + "\"?"))
                        pistaSvc.eliminar(p.getId());
                });
            }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : new HBox(6, btnEditar, btnEliminar));
            }
        });

        tablaPistas.setItems(PistaService.pistas);
    }

    @FXML private void onNuevaPista() { mostrarDialogoPista(null); }

    private void mostrarDialogoPista(Pista existente) {
        boolean esNueva = (existente == null);
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle(esNueva ? "Nueva Pista" : "Editar Pista");
        dialog.setHeaderText(null);
        ButtonType guardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(guardar, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(8);
        grid.setPadding(new Insets(12));

        TextField         txtNombre = new TextField(esNueva ? "" : existente.getNombre());
        TextField         txtDesc   = new TextField(esNueva ? "" :
                (existente.getDescripcion() == null ? "" : existente.getDescripcion()));
        TextField         txtPrecio = new TextField(esNueva ? "" :
                String.valueOf(existente.getPrecio()));
        ComboBox<Deporte> cmbDep    = new ComboBox<>(PistaService.deportes);
        ComboBox<String>  cmbEst    = new ComboBox<>();
        cmbEst.getItems().addAll("LIBRE", "OCUPADA");
        cmbDep.setValue(esNueva ? PistaService.deportes.get(0) : existente.getDeporte());
        cmbEst.setValue(esNueva ? "LIBRE" : existente.getEstado());

        grid.add(new Label("Nombre:"),  0, 0); grid.add(txtNombre, 1, 0);
        grid.add(new Label("Deporte:"), 0, 1); grid.add(cmbDep,    1, 1);
        grid.add(new Label("Desc:"),    0, 2); grid.add(txtDesc,   1, 2);
        grid.add(new Label("Precio:"),  0, 3); grid.add(txtPrecio, 1, 3);
        grid.add(new Label("Estado:"),  0, 4); grid.add(cmbEst,    1, 4);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(bt -> {
            if (bt != guardar) return null;
            try {
                double precio = Double.parseDouble(txtPrecio.getText().trim());
                if (esNueva) {
                    Pista p = new Pista(txtNombre.getText().trim(), cmbDep.getValue(),
                            txtDesc.getText().trim(), precio, cmbEst.getValue());
                    pistaSvc.guardar(p);
                } else {
                    existente.setNombre(txtNombre.getText().trim());
                    existente.setDeporte(cmbDep.getValue());
                    existente.setDescripcion(txtDesc.getText().trim());
                    existente.setPrecio(precio);
                    existente.setEstado(cmbEst.getValue());
                    pistaSvc.actualizar(existente);
                }
            } catch (NumberFormatException e) {
                new Alert(Alert.AlertType.WARNING,
                        "El precio debe ser un número.", ButtonType.OK).showAndWait();
            }
            return null;
        });
        dialog.showAndWait();
    }

    private void setupTablaUsuarios() {
        colUsNombre.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getNombre() + " " + c.getValue().getApellidos()));
        colUsEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        colUsAcciones.setCellFactory(col -> new TableCell<>() {
            private final Button btnEditar   = new Button("Editar");
            private final Button btnEliminar = new Button("Borrar");
            {
                btnEditar.getStyleClass().add("btn-tabla-editar");
                btnEliminar.getStyleClass().add("btn-tabla-borrar");
                btnEditar.setOnAction(e ->
                        mostrarDialogoUsuario(getTableView().getItems().get(getIndex())));
                btnEliminar.setOnAction(e -> {
                    Usuario u = getTableView().getItems().get(getIndex());
                    if (u.isAdmin()) {
                        new Alert(Alert.AlertType.WARNING,
                                "No se puede borrar un administrador.", ButtonType.OK)
                                .showAndWait();
                        return;
                    }
                    if (confirmar("¿Borrar el usuario \"" + u.getNombre() + "\"?")) {
                        usuarioDAO.eliminar(u.getId());
                        PistaService.usuarios.removeIf(x -> x.getId() == u.getId());
                    }
                });
            }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : new HBox(6, btnEditar, btnEliminar));
            }
        });

        tablaUsuarios.setItems(PistaService.usuarios);
    }

    @FXML private void onNuevoUsuario() { mostrarDialogoUsuario(null); }

    private void mostrarDialogoUsuario(Usuario existente) {
        boolean esNuevo = (existente == null);
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle(esNuevo ? "Nuevo Usuario" : "Editar Usuario");
        dialog.setHeaderText(null);
        ButtonType guardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(guardar, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(8);
        grid.setPadding(new Insets(12));

        TextField     txtNombre    = new TextField(esNuevo ? "" : existente.getNombre());
        TextField     txtApellidos = new TextField(esNuevo ? "" : existente.getApellidos());
        TextField     txtEmail     = new TextField(esNuevo ? "" : existente.getEmail());
        PasswordField txtPass      = new PasswordField();
        if (!esNuevo) txtPass.setPromptText("Dejar vacío para no cambiar");
        CheckBox      chkAdmin     = new CheckBox("Administrador");
        chkAdmin.setSelected(!esNuevo && existente.isAdmin());

        grid.add(new Label("Nombre:"),     0, 0); grid.add(txtNombre,    1, 0);
        grid.add(new Label("Apellidos:"),  0, 1); grid.add(txtApellidos, 1, 1);
        grid.add(new Label("Email:"),      0, 2); grid.add(txtEmail,     1, 2);
        grid.add(new Label("Contraseña:"), 0, 3); grid.add(txtPass,      1, 3);
        grid.add(new Label("Rol:"),        0, 4); grid.add(chkAdmin,     1, 4);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(bt -> {
            if (bt != guardar) return null;
            if (esNuevo) {
                if (txtPass.getText().isBlank()) {
                    new Alert(Alert.AlertType.WARNING,
                            "La contraseña no puede estar vacía.", ButtonType.OK).showAndWait();
                    return null;
                }
                if (usuarioDAO.emailExiste(txtEmail.getText().trim())) {
                    new Alert(Alert.AlertType.WARNING,
                            "Ya existe un usuario con ese email.", ButtonType.OK).showAndWait();
                    return null;
                }
                Usuario u = new Usuario(
                        txtNombre.getText().trim(), txtApellidos.getText().trim(),
                        txtEmail.getText().trim(), txtPass.getText(), chkAdmin.isSelected());
                usuarioDAO.guardar(u);
                PistaService.usuarios.add(u);
            } else {
                existente.setNombre(txtNombre.getText().trim());
                existente.setApellidos(txtApellidos.getText().trim());
                existente.setEmail(txtEmail.getText().trim());
                if (!txtPass.getText().isBlank()) existente.setPassword(txtPass.getText());
                existente.setAdmin(chkAdmin.isSelected());
                usuarioDAO.actualizar(existente);
                int idx = PistaService.usuarios.indexOf(existente);
                if (idx >= 0) PistaService.usuarios.set(idx, existente);
            }
            return null;
        });
        dialog.showAndWait();
    }

    private void setupTablaReservas() {
        colResPista.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getPista().getNombre()));
        colResUsuario.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getUsuario().getNombre() + " " +
                        c.getValue().getUsuario().getApellidos()));
        colResInicio.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getFecha() + "  " + c.getValue().getHoraInicio()));
        colResFin.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getFecha() + "  " + c.getValue().getHoraFin()));

        colResAcciones.setCellFactory(col -> new TableCell<>() {
            private final Button btnEditar   = new Button("Editar");
            private final Button btnCancelar = new Button("Cancelar");
            {
                btnEditar.getStyleClass().add("btn-tabla-editar");
                btnCancelar.getStyleClass().add("btn-tabla-borrar");
                btnEditar.setOnAction(e ->
                        mostrarDialogoReserva(getTableView().getItems().get(getIndex())));
                btnCancelar.setOnAction(e -> {
                    Reserva r = getTableView().getItems().get(getIndex());
                    if (confirmar("¿Cancelar reserva de \"" + r.getUsuario().getNombre() + "\"?"))
                        reservaSvc.cancelar(r.getId());
                });
            }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : new HBox(6, btnEditar, btnCancelar));
            }
        });

        tablaReservas.setItems(PistaService.reservas);
    }

    private void mostrarDialogoReserva(Reserva reserva) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Editar Reserva");
        dialog.setHeaderText(reserva.getPista().getNombre()
                + " — " + reserva.getUsuario().getNombre());
        ButtonType guardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(guardar, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(8);
        grid.setPadding(new Insets(12));

        DatePicker       dpFecha   = new DatePicker(reserva.getFecha());
        ComboBox<String> cmbInicio = new ComboBox<>();
        ComboBox<String> cmbFin    = new ComboBox<>();
        cmbInicio.getItems().addAll(HORAS);
        cmbFin.getItems().addAll(HORAS);
        cmbInicio.setValue(reserva.getHoraInicio().toString());
        cmbFin.setValue(reserva.getHoraFin().toString());

        grid.add(new Label("Fecha:"),       0, 0); grid.add(dpFecha,   1, 0);
        grid.add(new Label("Hora inicio:"), 0, 1); grid.add(cmbInicio, 1, 1);
        grid.add(new Label("Hora fin:"),    0, 2); grid.add(cmbFin,    1, 2);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(bt -> {
            if (bt != guardar) return null;
            if (dpFecha.getValue() == null) {
                new Alert(Alert.AlertType.WARNING, "Selecciona una fecha.", ButtonType.OK)
                        .showAndWait();
                return null;
            }
            LocalTime ini = LocalTime.parse(cmbInicio.getValue());
            LocalTime fin = LocalTime.parse(cmbFin.getValue());
            if (!fin.isAfter(ini)) {
                new Alert(Alert.AlertType.WARNING,
                        "La hora fin debe ser posterior al inicio.", ButtonType.OK)
                        .showAndWait();
                return null;
            }
            reserva.setFecha(dpFecha.getValue());
            reserva.setHoraInicio(ini);
            reserva.setHoraFin(fin);
            double horas = (double)(fin.toSecondOfDay() - ini.toSecondOfDay()) / 3600.0;
            reserva.setTotal(horas * reserva.getPista().getPrecio());
            new ReservaDAO().actualizar(reserva);
            PistaService.reservas.setAll(new ReservaDAO().obtenerTodas());
            return null;
        });
        dialog.showAndWait();
    }

    private boolean confirmar(String msg) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, msg,
                ButtonType.YES, ButtonType.NO);
        alert.setHeaderText(null);
        return alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }

    @FXML private void onVolver() {
        try {
            Stage stage = (Stage) tablaPistas.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/fxml/home.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(
                    getClass().getResource("/org/example/css/styles.css").toExternalForm());
            loader.<HomeController>getController().inicializar(usuarioActual);
            stage.setScene(scene);
        } catch (Exception e) { e.printStackTrace(); }
    }
}