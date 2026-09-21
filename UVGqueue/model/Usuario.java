package model;
import java.util.ArrayList;
public class Usuario {

    private int idUsuario;
    private String nombre;
    private String correo;
    private String contrasenaHash;
    private ArrayList<Restaurante> favoritos;
    private TipoUsuario tipo;
    public Usuario(String nombre, String correo, String contrasenaHash,TipoUsuario tipo) {
    this.idUsuario = 0;
    this.nombre = nombre;
    this.correo = correo;
    this.contrasenaHash = contrasenaHash;
    this.tipo = tipo;
    this.favoritos = new ArrayList<>();
}
    public Usuario(int idUsuario, String nombre, String correo, String contrasenaHash, TipoUsuario tipo){
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.correo = correo;
        this.contrasenaHash = contrasenaHash;
        this.tipo = tipo;
    }

    public String getNombre(){
        return nombre;
    }
    public String getCorreo(){
        return correo;
    }
    public TipoUsuario getTipo(){
        return tipo;
    }
    public int getID(){
        return idUsuario;
    }
    public String getContrasenaHash(){
        return contrasenaHash;
    }
    public ArrayList<Restaurante> getFavoritos(){
        return new ArrayList<>(favoritos);
    }
    public void setNombre(String nombre){
        this.nombre = nombre;
    }
    public void setCorreo(String correo){
        this.correo= correo;
    }
    public void setContrasenaHash(String contrasenaHash) {
        this.contrasenaHash = contrasenaHash;
    }
    public boolean AgregarFavorito(Restaurante restaurante){
        if (restaurante==null){
            return false;
        }
        if (favoritos.contains(restaurante)){
            return false;
        }
        favoritos.add(restaurante);
        return true;
    }
    public boolean eliminarFavorito(Restaurante restaurante){
        if (restaurante==null){
            return false;
        }
        return favoritos.remove(restaurante);
    }
    public boolean esFavorito(Restaurante restaurante){
        if (restaurante == null){
            return false;
        }
        return favoritos.contains(restaurante);
    }
    
}