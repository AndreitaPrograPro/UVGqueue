package model;
import java.time.LocalDateTime;
public class Reporte {
    private Usuario usuario;
    private int cantidadPersonas;
    private int tiempoEspera;
    private LocalDateTime fechaHora;

    public Reporte(Usuario usuario, int cantidadPersonas, int tiempoEspera){
        this.usuario = usuario;
        this.cantidadPersonas = cantidadPersonas;
        this.tiempoEspera = tiempoEspera;
        this.fechaHora = LocalDateTime.now();
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
}
