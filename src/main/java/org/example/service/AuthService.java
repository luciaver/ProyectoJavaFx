package org.example.service;

import org.example.dao.UsuarioDAO;
import org.example.model.Usuario;

public class AuthService {

    private final UsuarioDAO dao = new UsuarioDAO();

    public Usuario validarCredenciales(String email, String password) {
        Usuario u = dao.obtenerPorEmail(email);
        if (u != null && u.getPassword().equals(password)) {
            return u;
        }
        return null;
    }

    public boolean registrarUsuario(String nombre, String apellidos,
                                    String email, String password) {
        if (dao.emailExiste(email)) return false;
        Usuario u = new Usuario(nombre, apellidos, email, password, false);
        return dao.guardar(u);
    }

    public boolean emailExiste(String email) {
        return dao.emailExiste(email);
    }
}