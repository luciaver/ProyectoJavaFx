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
            // Obtener los datos
            String nombre = txtNombre.getText().trim();
            String apellidos = txtApellidos.getText().trim();
            String usuario = txtEmail.getText().trim();
            String email = txtEmail.getText().trim();
            String password = txtPassword.getText();
            String passwordConfirm = txtPasswordConfirm.getText();

            // Validaciones
            if (nombre.isEmpty() || usuario.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()) {
                mostrarError("Por favor completa todos los campos obligatorios");
                return;
            }
            if (apellidos.length() < 2) {  // NUEVA VALIDACIÓN
                mostrarError("Los apellidos deben tener al menos 2 caracteres");
                return;
            }

            if (usuario.length() < 3) {
                mostrarError("El usuario debe tener al menos 3 caracteres");
                return;
            }

            if (password.length() < 4) {
                mostrarError("La contraseña debe tener al menos 4 caracteres");
                return;
            }

            if (!password.equals(passwordConfirm)) {
                mostrarError("Las contraseñas no coinciden");
                return;
            }

            // Registrar usuario
            boolean registrado = authService.registrarUsuario(nombre, usuario, email, password);

            if (registrado) {
                mostrarExito("¡Cuenta creada exitosamente!");

                new Thread(() -> {
                    try {
                        Thread.sleep(1000);
                        javafx.application.Platform.runLater(this::onVolver);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else {
                mostrarError("El usuario ya existe. Elige otro nombre de usuario");
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
            lblMensaje.setStyle("-fx-text-fill: #dc2626;");
        }

        private void mostrarExito(String mensaje) {
            lblMensaje.setText(mensaje);
            lblMensaje.setStyle("-fx-text-fill: #10b981;");
        }
    }

