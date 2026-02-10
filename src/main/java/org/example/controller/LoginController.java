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

    private AuthService authService = new AuthService();

    @FXML
    private void onLogin() {
        String usuario = txtUsuario.getText();
        String password = txtPassword.getText();

        if (authService.validarCredenciales(usuario, password)) {
            lblMensaje.setText("Login correcto ");
        } else {
            lblMensaje.setText("Usuario o contraseña incorrectos ");
        }
    }
}
