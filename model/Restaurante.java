package model;

import java.time.LocalTime;

public class Restaurante {

    private int idRestaurante;
    private String nombre;
    private String ubicacion;
    private LocalTime horaApertura;
    private LocalTime horaCierre;
    private boolean activo;

    public Restaurante(int idRestaurante, String nombre, String ubicacion,
                       LocalTime horaApertura, LocalTime horaCierre,
                       boolean activo) {

        this.idRestaurante = idRestaurante;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.horaApertura = horaApertura;
        this.horaCierre = horaCierre;
        this.activo = activo;
    }

    public int getIdRestaurante() {
        return idRestaurante;
    }

    public String getNombre() {
        return nombre;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public LocalTime getHoraApertura() {
        return horaApertura;
    }

    public LocalTime getHoraCierre() {
        return horaCierre;
    }

    public boolean isActivo() {
        return activo;
    }
}