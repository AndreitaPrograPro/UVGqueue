
package model;

import java.time.LocalDateTime;

public class Turno {

    private int idTurno;
    private int idFila;
    private int idUsuario;
    private int numeroTurno;
    private String estado;
    private LocalDateTime fechaHora;

    public Turno(int idTurno, int idFila, int idUsuario,
                 int numeroTurno, String estado,
                 LocalDateTime fechaHora) {

        this.idTurno = idTurno;
        this.idFila = idFila;
        this.idUsuario = idUsuario;
        this.numeroTurno = numeroTurno;
        this.estado = estado;
        this.fechaHora = fechaHora;
    }

    public int getIdTurno() {
        return idTurno;
    }

    public int getIdFila() {
        return idFila;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public int getNumeroTurno() {
        return numeroTurno;
    }

    public String getEstado() {
        return estado;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }
}