package model;

import java.time.LocalDate;

public class Fila {

    private int idFila;
    private int idRestaurante;
    private String estado;
    private LocalDate fecha;

    public Fila(int idFila, int idRestaurante,
                String estado, LocalDate fecha) {

        this.idFila = idFila;
        this.idRestaurante = idRestaurante;
        this.estado = estado;
        this.fecha = fecha;
    }

    public int getIdFila() {
        return idFila;
    }

    public int getIdRestaurante() {
        return idRestaurante;
    }

    public String getEstado() {
        return estado;
    }

    public LocalDate getFecha() {
        return fecha;
    }
}