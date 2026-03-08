package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.model.Usuario;
import org.example.service.AuthService;

public class LoginController {

    @FXML private TextField     txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private Label         lblMensaje;

    private final AuthService authService = new AuthService();

    @FXML
    private void onLogin() {
        String email    = txtEmail.getText().trim();
        String password = txtPassword.getText();

        if (email.isEmpty() || password.isEmpty()) {
            mostrarError("Por favor completa todos los campos");
            return;
        }
        if (!email.contains("@") || !email.contains(".")) {
            mostrarError("Por favor ingresa un email válido");
            return;
        }

        Usuario encontrado = authService.validarCredenciales(email, password);
        if (encontrado != null) {
            mostrarExito("¡Bienvenido/a!");
            new Thread(() -> {
                try {
                    Thread.sleep(500);
                    javafx.application.Platform.runLater(() -> cargarHome(encontrado));
                } catch (InterruptedException e) { e.printStackTrace(); }
            }).start();
        } else {
            mostrarError("Email o contraseña incorrectos");
        }
    }

    @FXML
    private void onRegistro() {
        try {
            Stage stage = (Stage) txtEmail.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/fxml/registro.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/org/example/css/styles.css").toExternalForm());
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error al abrir el registro");
        }
    }

    private void cargarHome(Usuario usuario) {
        try {
            Stage stage = (Stage) txtEmail.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/fxml/home.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/org/example/css/styles.css").toExternalForm());
            HomeController ctrl = loader.getController();
            ctrl.inicializar(usuario);
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error al cargar la pantalla principal");
        }
    }

    private void mostrarError(String msg) { lblMensaje.setText(msg); lblMensaje.getStyleClass().setAll("mensaje", "mensaje-error"); }
    private void mostrarExito(String msg) { lblMensaje.setText(msg); lblMensaje.getStyleClass().setAll("mensaje", "mensaje-exito"); }
}