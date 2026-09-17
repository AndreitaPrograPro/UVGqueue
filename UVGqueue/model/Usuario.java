package model;

public class Usuario {

    private int idUsuario;
    private String nombre;
    private String correo;
    private String contrasena;
    private String rol;

    public Usuario(int idUsuario, String nombre, String correo,
                   String contrasena, String rol) {

        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.correo = correo;
        this.contrasena = contrasena;
        this.rol = rol;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public String getRol() {
        return rol;
    }
}