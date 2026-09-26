package controller;

import dao.*;
import view.*;
import model.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class UsuarioController {

    private static final int iteraciones = 65536;
    private static final int longitud_Hash = 256;
    private static final int longitud_salt = 16;

    private final LoginView loginView;
    private final UsuarioDAO usuarioDAO;
    private Usuario usuarioActivo;

    public UsuarioController(LoginView loginView) {
        this.loginView = loginView;
        this.usuarioDAO = new UsuarioDAO();
        this.usuarioActivo = null;

        registrarEventos();
    }

    private void registrarEventos() {
        loginView.getBtnSignIn().addActionListener(
                evento -> procesarInicioSesion()
        );

        loginView.getBtnConfirmRegister().addActionListener(
                evento -> procesarRegistro()
        );

        loginView.getBtnForgotPassword().addActionListener(
                evento -> loginView.mostrarMensaje(
                        "Recuperar Contraseña",
                        "Esta función estará disponible próximamente"
                )
        );
    }

    private void procesarInicioSesion() {
        String identificador = loginView.getLoginUser();
        String contrasena = loginView.getLoginPassword();

        if (iniciarSesion(identificador, contrasena)) {
            loginView.mostrarMensaje(
                    "Inicio de Sesión",
                    "Bienvenido " + usuarioActivo.getNombre()
            );

            EstudianteFrame estudianteFrame =
                    new EstudianteFrame();

            estudianteFrame.setVisible(true);
            loginView.dispose();

            new RestauranteController(estudianteFrame);
            new ReporteController(
                    estudianteFrame,
                    usuarioActivo
            );

        } else {
            loginView.mostrarMensaje(
                    "Error",
                    "Usuario o contraseña incorrecto"
            );
        }
    }

    private void procesarRegistro() {
        String nombre = loginView.getRegUser();
        String correo = loginView.getRegEmail();
        String contrasena = loginView.getRegPassword();
        String confirmacion =
                loginView.getRegConfirmPassword();

        boolean registro = registrarUsuario(
                nombre,
                correo,
                contrasena,
                confirmacion
        );

        if (registro) {
            loginView.mostrarMensaje(
                    "Registro exitoso",
                    "La cuenta fue creada correctamente"
            );

            loginView.limpiarRegistro();
            loginView.mostrarLogin();
        }
    }

    public boolean registrarUsuario(
            String nombre,
            String correo,
            String contrasena,
            String confirmacion
    ) {
        if (!validarNombre(nombre)) {
            loginView.mostrarMensaje(
                    "Registro",
                    "El nombre debe tener al menos tres caracteres"
            );
            return false;
        }

        if (!validarCorreo(correo)) {
            loginView.mostrarMensaje(
                    "Registro",
                    "El correo no es válido"
            );
            return false;
        }

        if (!validarContrasena(contrasena)) {
            loginView.mostrarMensaje(
                    "Registro",
                    "La contraseña debe tener al menos 8 caracteres"
            );
            return false;
        }

        if (!coincidenContrasenas(
                contrasena,
                confirmacion
        )) {
            loginView.mostrarMensaje(
                    "Registro",
                    "La contraseña no coincide"
            );
            return false;
        }

        if (usuarioDAO.buscarPorNombre(nombre) != null) {
            loginView.mostrarMensaje(
                    "Registro",
                    "El nombre de usuario ya está registrado"
            );
            return false;
        }

        if (usuarioDAO.buscarPorCorreo(correo) != null) {
            loginView.mostrarMensaje(
                    "Registro",
                    "El correo ya está registrado"
            );
            return false;
        }

        String contrasenaHash;

        try {
            contrasenaHash = generarHash(contrasena);
        } catch (IllegalStateException excepcion) {
            loginView.mostrarMensaje(
                    "Registro",
                    "No fue posible proteger la contraseña"
            );
            return false;
        }

        Usuario nuevoUsuario = new Usuario(
                nombre.trim(),
                correo.trim().toLowerCase(),
                contrasenaHash,
                TipoUsuario.ESTUDIANTE
        );

        return usuarioDAO.registrar(nuevoUsuario);
    }

    public boolean iniciarSesion(
            String identificador,
            String contrasena
    ) {
        Usuario usuario = buscarUsuario(identificador);

        if (usuario == null || contrasena == null) {
            usuarioActivo = null;
            return false;
        }

        boolean contrasenaCorrecta =
                verificarContrasena(
                        contrasena,
                        usuario.getContrasenaHash()
                );

        if (!contrasenaCorrecta) {
            usuarioActivo = null;
            return false;
        }

        usuarioActivo = usuario;
        return true;
    }

    private Usuario buscarUsuario(String identificador) {
        if (identificador == null ||
                identificador.isBlank()) {
            return null;
        }

        String valor = identificador.trim();

        Usuario usuario =
                usuarioDAO.buscarPorCorreo(valor);

        if (usuario == null) {
            usuario =
                    usuarioDAO.buscarPorNombre(valor);
        }

        return usuario;
    }

    public void cerrarSesion() {
        usuarioActivo = null;
    }

    public boolean haySesionActiva() {
        return usuarioActivo != null;
    }

    public Usuario getUsuarioActivo() {
        return usuarioActivo;
    }

    public boolean validarNombre(String nombre) {
        return nombre != null &&
                nombre.trim().length() >= 3;
    }

    private boolean validarCorreo(String correo) {
        if (correo == null) {
            return false;
        }

        return correo.trim().matches(
                "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"
        );
    }

    private boolean validarContrasena(
            String contrasena
    ) {
        return contrasena != null &&
                contrasena.length() >= 8;
    }

    private boolean coincidenContrasenas(
            String contrasena,
            String confirmacion
    ) {
        return contrasena != null &&
                contrasena.equals(confirmacion);
    }

    private String generarHash(String contrasena) {

        /*
         * El salt contiene bytes aleatorios que se combinan
         * con la contraseña. Gracias a esto, dos usuarios que
         * tengan la misma contraseña obtendrán hashes diferentes.
         */
        byte[] salt = new byte[longitud_salt];

        /*
         * SecureRandom genera valores aleatorios adecuados
         * para operaciones relacionadas con seguridad.
         */
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);

        /*
         * Se genera el hash utilizando la contraseña, el salt
         * aleatorio y la cantidad de iteraciones establecida.
         */
        byte[] hash = derivarClave(
                contrasena.toCharArray(),
                salt,
                iteraciones
        );

        /*
         * El salt y el hash son arreglos de bytes, por lo que
         * se convierten a Base64 para almacenarlos como texto.
         *
         * El resultado se guarda con el siguiente formato:
         * iteraciones:salt:hash
         */
        return iteraciones
                + ":" + Base64.getEncoder()
                        .encodeToString(salt)
                + ":" + Base64.getEncoder()
                        .encodeToString(hash);
    }

    private boolean verificarContrasena(
            String contrasena,
            String hashGuardado
    ) {
        if (hashGuardado == null) {
            return false;
        }

        try {
            String[] partes = hashGuardado.split(":");

            if (partes.length != 3) {
                return false;
            }

            int iteraciones =
                    Integer.parseInt(partes[0]);

            byte[] salt = Base64.getDecoder()
                    .decode(partes[1]);

            byte[] hashEsperado = Base64.getDecoder()
                    .decode(partes[2]);

            byte[] hashIngresado = derivarClave(
                    contrasena.toCharArray(),
                    salt,
                    iteraciones
            );

            return MessageDigest.isEqual(
                    hashEsperado,
                    hashIngresado
            );

        } catch (IllegalArgumentException excepcion) {
            return false;
        }
    }

    private byte[] derivarClave(
            char[] contrasena,
            byte[] salt,
            int iteraciones
    ) {

        /*
         * PBEKeySpec reúne los datos necesarios para PBKDF2:
         * la contraseña, el salt, la cantidad de iteraciones
         * y la longitud que tendrá el hash.
         */
        PBEKeySpec especificacion = new PBEKeySpec(
                contrasena,
                salt,
                iteraciones,
                longitud_Hash
        );

        try {
            /*
             * PBKDF2WithHmacSHA256 procesa varias veces la
             * contraseña para hacer más difícil descubrirla
             * mediante intentos repetidos.
             */
            SecretKeyFactory fabrica =
                    SecretKeyFactory.getInstance(
                            "PBKDF2WithHmacSHA256"
                    );

            // Genera y devuelve los bytes finales del hash.
            return fabrica
                    .generateSecret(especificacion)
                    .getEncoded();

        } catch (
                NoSuchAlgorithmException |
                InvalidKeySpecException excepcion
        ) {
            /*
             * Si Java no puede utilizar el algoritmo, se genera
             * una excepción para detener el registro.
             */
            throw new IllegalStateException(
                    "No fue posible generar el hash",
                    excepcion
            );

        } finally {
            /*
             * Borra la contraseña guardada en PBEKeySpec para
             * evitar que permanezca innecesariamente en memoria.
             */
            especificacion.clearPassword();
        }
    }
}