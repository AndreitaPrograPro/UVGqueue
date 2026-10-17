package model;
import java.time.LocalDateTime;
public class Reporte {
    private Usuario usuario;
    private int cantidadPersonas;
    private int tiempoEspera;
    private LocalDateTime fechaHora;
    private String nombreRestaurante;

    public Reporte(Usuario usuario, int cantidadPersonas, int tiempoEspera){
        this.usuario = usuario;
        this.cantidadPersonas = cantidadPersonas;
        this.tiempoEspera = tiempoEspera;
        this.fechaHora = LocalDateTime.now();
    }
    public Reporte(Usuario usuario,String nombreRestaurante,int cantidadPersonas,int tiempoEspera,LocalDateTime fechaHora) {
        this.usuario = usuario;
        this.nombreRestaurante = nombreRestaurante;
        this.cantidadPersonas = cantidadPersonas;
        this.tiempoEspera = tiempoEspera;
        this.fechaHora = fechaHora;
    }
    public Usuario getUsuario(){
        return usuario;
    }
    public int getCantidadPersonas(){
        return cantidadPersonas;
    }
    public int getTiempoEspera(){
        return tiempoEspera;
    }
    public LocalDateTime getFechaHora(){
        return fechaHora;
    }
    public String getNombreRestaurante() {
    return nombreRestaurante;
    }
}
