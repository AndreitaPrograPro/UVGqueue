
package controller;

import model.Usuario;
import service.Authservice;
import view.EstudianteFrame;
import view.LoginView;

import javax.swing.SwingWorker;
import java.util.Objects;

/** Coordina la vista de autenticación con la lógica y la base de datos. */
public class Logincontroller {

    private final LoginView vista;
    private final Authservice authservice;

    public Logincontroller(LoginView vista, Authservice authservice) {
        this.vista = Objects.requireNonNull(vista, "Falta LoginView.");
        this.authservice = Objects.requireNonNull(authservice, "Falta Authservice.");
        registrarEventos();
    }

    private void registrarEventos() {
        vista.getBtnSignIn().addActionListener(evento -> iniciarSesion());
        vista.getBtnConfirmRegister().addActionListener(evento -> registrarEstudiante());
        vista.getBtnForgotPassword().addActionListener(evento ->
                vista.mostrarMensaje(
                        "Recuperar contraseña",
                        "Esta función estará disponible próximamente."
                )
        );
    }

    private void iniciarSesion() {
        String correo = vista.getLoginUser();
        String contrasena = vista.getLoginPassword();
        vista.setProcesando(true, "Iniciando sesión...");

        new SwingWorker<Usuario, Void>() {
            @Override
            protected Usuario doInBackground() {
                return authservice.iniciarSesion(correo, contrasena);
            }

            @Override
            protected void done() {
                try {
                    Usuario usuario = get();
                    abrirPantallaSegunRol(usuario);
                } catch (Exception excepcion) {
                    vista.limpiarContrasena();
                    vista.mostrarError(obtenerMensaje(excepcion));
                } finally {
                    vista.setProcesando(false, "Iniciar sesión");
                }
            }
        }.execute();
    }

    private void registrarEstudiante() {
        String nombre = vista.getRegUser();
        String correo = vista.getRegEmail();
        String contrasena = vista.getRegPassword();
        vista.setRegistroProcesando(true);

        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                return authservice.registrarEstudiante(nombre, correo, contrasena);
            }

            @Override
            protected void done() {
                try {
                    get();
                    vista.mostrarMensaje(
                            "Cuenta creada",
                            "Tu cuenta fue registrada. Ya puedes iniciar sesión."
                    );
                    vista.mostrarLogin();
                } catch (Exception excepcion) {
                    vista.mostrarError(obtenerMensaje(excepcion));
                } finally {
                    vista.setRegistroProcesando(false);
                }
            }
        }.execute();
    }

    private void abrirPantallaSegunRol(Usuario usuario) {
        if ("ESTUDIANTE".equals(usuario.getRol())) {
            EstudianteFrame dashboard = new EstudianteFrame(usuario.getNombre());
            dashboard.setVisible(true);
            vista.dispose();
            return;
        }

        vista.mostrarMensaje(
                "Rol reconocido",
                "La pantalla para el rol " + usuario.getRol() + " estará disponible próximamente."
        );
        authservice.cerrarSesion();
    }

    private String obtenerMensaje(Exception excepcion) {
        Throwable causa = excepcion;
        while (causa.getCause() != null) {
            causa = causa.getCause();
        }
        return causa.getMessage() == null
                ? "Ocurrió un error inesperado."
                : causa.getMessage();
    }
}
