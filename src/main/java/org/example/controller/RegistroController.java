package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.service.AuthService;

public class RegistroController {

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtApellidos;

    @FXML
    private TextField txtEmail;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private PasswordField txtPasswordConfirm;

    @FXML
    private Label lblMensaje;

    private AuthService authService = new AuthService();

    @FXML
    private void onRegistrar() {
        String nombre = txtNombre.getText().trim();
        String apellidos = txtApellidos.getText().trim();
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText();
        String passwordConfirm = txtPasswordConfirm.getText();

        if (nombre.isEmpty() || apellidos.isEmpty() || email.isEmpty() ||
                password.isEmpty() || passwordConfirm.isEmpty()) {
            mostrarError("Por favor completa todos los campos obligatorios");
            return;
        }

        if (apellidos.length() < 2) {
            mostrarError("Los apellidos deben tener al menos 2 caracteres");
            return;
        }


        if (!email.contains("@") || !email.contains(".")) {
            mostrarError("Por favor ingresa un email valido");
            return;
        }

        if (nombre.length() < 3) {
            mostrarError("El nombre debe tener al menos 3 caracteres");
            return;
        }

        if (password.length() < 4) {
            mostrarError("La contrasena debe tener al menos 4 caracteres");
            return;
        }

        if (!password.equals(passwordConfirm)) {
            mostrarError("Las contrasenas no coinciden");
            return;
        }

        boolean registrado = authService.registrarUsuario(nombre, apellidos, email, password);

        if (registrado) {
            mostrarExito("Cuenta creada exitosamente!");

            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    javafx.application.Platform.runLater(this::onVolver);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        } else {
            mostrarError("El email ya esta registrado. Usa otro email");
        }
    }

    @FXML
    private void onVolver() {
        try {
            Stage stage = (Stage) txtNombre.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/fxml/login.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/org/example/css/styles.css").toExternalForm());
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarError(String mensaje) {
        lblMensaje.setText(mensaje);
        lblMensaje.getStyleClass().setAll("mensaje", "mensaje-error");
    }

    private void mostrarExito(String mensaje) {
        lblMensaje.setText(mensaje);
        lblMensaje.getStyleClass().setAll("mensaje", "mensaje-exito");
    }
}