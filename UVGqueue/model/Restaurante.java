package model;
import java.util.ArrayList;

public class Restaurante{
    private String nombre;
    private EstadoRestaurante estado;
    private String ubicacion;
    private ArrayList<Reporte> reportes;
    public Restaurante (String nombre, String ubicacion, EstadoRestaurante estado){
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.estado = estado;
        this.reportes = new ArrayList<>();
    }
    public String getNombre(){
        return nombre;
    }
    public String getUbicacion(){
        return ubicacion;
    }
    public EstadoRestaurante getEstado(){
        return estado;
    }
    public ArrayList<Reporte> getReportes(){
        return new ArrayList<>(reportes);
    }
    public void setEstado(EstadoRestaurante estado){
        this.estado = estado;
    }
    public void agregarReporte(Reporte reporte){
        if (reporte!=null){
            reportes.add(reporte);
        }
    }

}