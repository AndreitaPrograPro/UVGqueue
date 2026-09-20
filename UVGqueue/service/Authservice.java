package service;

import dao.UsuarioDAO;
import model.Usuario;

import java.util.Objects;

public class Authservice {

    private final UsuarioDAO usuarioDAO;
    private Usuario usuarioActual;

    public Authservice() {
        this(new UsuarioDAO());
    }

    public Authservice(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = Objects.requireNonNull(usuarioDAO, "Falta UsuarioDAO.");
    }

    public boolean registrarEstudiante(String nombre, String correo, String contrasena) {
        nombre = validarNombre(nombre);
        correo = validarCorreo(correo);
        validarContrasena(contrasena);

        if (usuarioDAO.buscarPorCorreo(correo) != null) {
            throw new IllegalArgumentException("Ya existe un usuario con ese correo.");
        }

        Usuario nuevo = new Usuario(0, nombre, correo, contrasena, "ESTUDIANTE");
        if (!usuarioDAO.registrar(nuevo)) {
            throw new IllegalStateException(
                    "No se pudo registrar el usuario. Revisa el correo y la conexion.");
        }
        return true;
    }

    public Usuario iniciarSesion(String correo, String contrasena) {
        usuarioActual = null;
        correo = validarCorreo(correo);
        validarContrasena(contrasena);

        Usuario encontrado = usuarioDAO.iniciarSesion(correo, contrasena);
        if (encontrado == null) {
            throw new IllegalStateException(
                    "No se pudo iniciar sesion. Revisa tus credenciales y la conexion.");
        }
        if (encontrado.getIdUsuario() <= 0 || !rolValido(encontrado.getRol())) {
            throw new IllegalStateException("La cuenta no tiene un ID o rol valido.");
        }

        usuarioActual = encontrado;
        return usuarioActual;
    }

    public void cerrarSesion() {
        usuarioActual = null;
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public boolean haySesionActiva() {
        return usuarioActual != null;
    }

    private boolean rolValido(String rol) {
        return "ESTUDIANTE".equals(rol)
                || "ENCARGADO".equals(rol)
                || "ADMIN".equals(rol);
    }

    private String validarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("Debes ingresar tu nombre.");
        }
        nombre = nombre.trim();
        if (nombre.length() > 100) {
            throw new IllegalArgumentException("El nombre admite hasta 100 caracteres.");
        }
        return nombre;
    }

    private String validarCorreo(String correo) {
        if (correo == null || correo.trim().isEmpty()) {
            throw new IllegalArgumentException("Debes ingresar tu correo.");
        }
        correo = correo.trim();
        if (correo.length() > 150 || !correo.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")) {
            throw new IllegalArgumentException("Ingresa un correo valido de hasta 150 caracteres.");
        }
        return correo;
    }

    private void validarContrasena(String contrasena) {
        if (contrasena == null || contrasena.isBlank()) {
            throw new IllegalArgumentException("Debes ingresar una contrasena.");
        }
        if (contrasena.length() > 255) {
            throw new IllegalArgumentException("La contrasena admite hasta 255 caracteres.");
        }

    }
}
