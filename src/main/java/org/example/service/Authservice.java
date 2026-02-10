package org.example.service;

public class AuthService {

    public boolean validarCredenciales(String usuario, String password) {
        return usuario.equals("admin") && password.equals("1234");
    }
}
