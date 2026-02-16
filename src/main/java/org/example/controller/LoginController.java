package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Label lblMensaje;

    @FXML
    private void onLogin() {
        // Obtener lo que escribió el usuario
        String usuario = txtUsuario.getText();
        String password = txtPassword.getText();

        // Verificar si es correcto
        if (usuario.equals("admin") && password.equals("1234")) {
            lblMensaje.setText(" Login correcto!");
            lblMensaje.setStyle("-fx-text-fill: green;");
        } else {
            lblMensaje.setText("Usuario o contraseña incorrectos");
            lblMensaje.setStyle("-fx-text-fill: red;");
        }
    }
}