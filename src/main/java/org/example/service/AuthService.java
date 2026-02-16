package org.example.service;

import org.example.model.Usuario;
import java.util.ArrayList;
import java.util.List;

public class AuthService {
    private static List<Usuario> usuarios = new ArrayList<>();

    static {
        usuarios.add(new Usuario("Admin", "Sistema", "admin@multisports.com", "1234"));
    }

    public Usuario validarCredenciales(String email, String password) {
        for (Usuario usuario : usuarios) {
            if (usuario.getEmail().equalsIgnoreCase(email) &&
                    usuario.getPassword().equals(password)) {
                return usuario;
            }
        }
        return null;
    }

    public boolean registrarUsuario(String nombre, String apellidos, String email, String password) {
        for (Usuario usuario : usuarios) {
            if (usuario.getEmail().equalsIgnoreCase(email)) {
                return false;
            }
        }
        Usuario nuevoUsuario = new Usuario(nombre, apellidos, email, password);
        usuarios.add(nuevoUsuario);
        return true;
    }

    public boolean emailExiste(String email) {
        for (Usuario usuario : usuarios) {
            if (usuario.getEmail().equalsIgnoreCase(email)) {
                return true;
            }
        }
        return false;
    }
}