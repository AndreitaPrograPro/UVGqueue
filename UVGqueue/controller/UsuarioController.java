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
    private static  final int longitud_Hash = 256;
    private static final int longitud_salt = 16;
    private final LoginView loginView;
    private final UsuarioDAO usuarioDAO;
    private Usuario usuarioActivo;
    public UsuarioController(LoginView loginView){
        this.loginView = loginView;
        this.usuarioDAO = new UsuarioDAO();
        this.usuarioActivo = null;
        registrarEventos();
    }
    private void registrarEventos(){
        loginView.getBtnSignIn().addActionListener(evento-> procesarInicioSesion());
        loginView.getBtnConfirmRegister().addActionListener(evento-> procesarRegistro());
        loginView.getBtnForgotPassword().addActionListener(evento-> loginView.mostrarMensaje("Recuperar Contraseña", "Esta función estará disponible próximamente"));
    }
    private void procesarInicioSesion(){
        String identificador = loginView.getLoginUser();
        String contrasena = loginView.getLoginPassword();
        if (iniciarSesion(identificador, contrasena)){
            loginView.mostrarMensaje("Inicio de Sesion", "Bienvenido " + usuarioActivo.getNombre());
            EstudianteFrame estudianteFrame = new EstudianteFrame();
            estudianteFrame.setVisible(true);
            loginView.dispose();
            new RestauranteController(estudianteFrame);
            new ReporteController(estudianteFrame,usuarioActivo);
        } else { 
            loginView.mostrarMensaje("Error", "Usuario o contraseña incorrecto");
        }
    }
    private void procesarRegistro(){
        String nombre = loginView.getRegUser();
        String correo = loginView.getRegEmail();
        String contrasena = loginView.getRegPassword();
        String confirmacion = loginView.getRegConfirmPassword();
        boolean registro = registrarUsuario(nombre, correo, contrasena, confirmacion);
        if (registro){
            loginView.mostrarMensaje("Registro existoso", "La cuenta fue creada correctamente");
            loginView.limpiarRegistro();
            loginView.mostrarLogin();
        }
    }
    public boolean registrarUsuario(String nombre, String correo, String contrasena, String confirmacion){
        if (!validarNombre(nombre)){
            loginView.mostrarMensaje("Registro","El nombre debe de tener al menos tres caracteres");
            return false;
        }
        if (!validarCorreo(correo)){
            loginView.mostrarMensaje("Registro", "El correo no es valido");
            return false;
        }
        if (!validarContrasena(contrasena)){
            loginView.mostrarMensaje("Registro", "La contraseña debe de tener al menos 8 caracteres");
            return false;
        }
        if (!coincidenContrasenas(contrasena, confirmacion)){
            loginView.mostrarMensaje("Registro", "La contraseña no coincide");
            return false;
        }
        if(usuarioDAO.buscarPorNombre(nombre)!=null){
            loginView.mostrarMensaje("Registro", "El nombre de usuario ya está registrado");
            return false;
        }
        if (usuarioDAO.buscarPorCorreo(correo)!= null){
            loginView.mostrarMensaje("Registro", "El correo ya está registrado");
            return false;
        }
        String contrasenaHash;
        try {
            contrasenaHash = generarHash(contrasena);
        } catch (IllegalStateException excepcion){
            loginView.mostrarMensaje("Registro", "No fue posible proteger la contraseña");
            return false;
        }
        Usuario nuevoUsuario = new Usuario(nombre.trim(), correo.trim().toLowerCase(), contrasenaHash, TipoUsuario.ESTUDIANTE);
        return usuarioDAO.registrar(nuevoUsuario);
    }
    public boolean iniciarSesion(String identificador, String contrasena){
        Usuario usuario = buscarUsuario(identificador);
        if (usuario == null || contrasena == null){
            usuarioActivo = null;
            return false;
        }
        boolean contrasenaCorrecta = verificarContrasena(contrasena, usuario.getContrasenaHash());
        if (!contrasenaCorrecta){
            usuarioActivo= null;
            return false;
        }
        usuarioActivo = usuario;
        return true;
    }
    private Usuario buscarUsuario(String identificador){
        if (identificador == null || identificador.isBlank()){
            return null;
        }
        String valor = identificador.trim();
        Usuario usuario = usuarioDAO.buscarPorCorreo(valor);
        if (usuario==null){
            usuario = usuarioDAO.buscarPorNombre(valor);
        }
        return usuario;
    }
    public void cerrarSesion(){
        usuarioActivo = null;
    }
    public boolean haySesionActiva(){
        return usuarioActivo != null;
    }
    public Usuario getUsuarioActivo(){
        return usuarioActivo;
    }
    public boolean validarNombre(String nombre){
        return nombre!=null && nombre.trim().length()>=3;
    }
    private boolean validarCorreo(String correo){
        if (correo==null){
            return false;
        }
        return correo.trim().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    }
    private boolean validarContrasena(String contrasena){
        return contrasena!=null && contrasena.length()>= 8;
    }
    private boolean coincidenContrasenas(String contrasena, String confirmacion){
        return contrasena!= null && contrasena.equals(confirmacion);
    }
    private String generarHash(String contrasena){
        byte[] salt = new byte[longitud_Hash];
        
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);
        byte[] hash = derivarClave(contrasena.toCharArray(), salt, iteraciones);
        return iteraciones + ":" + Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
    }
    private boolean verificarContrasena(String contrasena, String hashGuardado){
        if (hashGuardado==null){
            return false;
        }
        try {
            String[] partes = hashGuardado.split(":");
            if (partes.length != 3){
                return false;
            }
            int iteraciones = Integer.parseInt(partes[0]);
            byte[] salt = Base64.getDecoder().decode(partes[1]);
            byte[] hashEsperado = Base64.getDecoder().decode(partes[2]);
            byte[] hashIngresado = derivarClave(contrasena.toCharArray(), salt, iteraciones);
            return MessageDigest.isEqual(hashEsperado, hashIngresado);
        } catch (IllegalArgumentException excepcion){
            return false;
        }
    }
    private byte[] derivarClave(char[] contrasena, byte[] salt, int iteraciones){
        PBEKeySpec especificacion = new PBEKeySpec(contrasena, salt, iteraciones, longitud_Hash);
        try{
            SecretKeyFactory fabrica = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return fabrica.generateSecret(especificacion).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException excepcion){
            throw new IllegalStateException("No fue posible generar el hash", excepcion);
        } finally{
            especificacion.clearPassword();
        }
    }

}
