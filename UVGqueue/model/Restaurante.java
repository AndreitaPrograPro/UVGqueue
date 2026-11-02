package model;
import java.util.ArrayList;
import java.time.LocalTime;
public class Restaurante {

    private String nombre;
    private EstadoRestaurante estado;
    private String ubicacion;
    private ArrayList<Reporte> reportes;

    private LocalTime horaApertura;
    private LocalTime horaCierre;

    public Restaurante(
            String nombre,
            String ubicacion,
            EstadoRestaurante estado) {

        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.estado = estado;
        this.reportes = new ArrayList<>();
    }

    // Nuevo constructor con horarios
    public Restaurante(
            String nombre,
            String ubicacion,
            EstadoRestaurante estado,
            LocalTime horaApertura,
            LocalTime horaCierre) {

        this(nombre, ubicacion, estado);

        this.horaApertura = horaApertura;
        this.horaCierre = horaCierre;
    }

    public String getNombre() {
        return nombre;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public EstadoRestaurante getEstado() {
        return estado;
    }

    public void setEstado(EstadoRestaurante estado) {
        this.estado = estado;
    }

    public LocalTime getHoraApertura() {
        return horaApertura;
    }

    public LocalTime getHoraCierre() {
        return horaCierre;
    }

    public ArrayList<Reporte> getReportes() {
        return new ArrayList<>(reportes);
    }

    public void agregarReporte(Reporte reporte) {
        if (reporte != null) {
            reportes.add(reporte);
        }
    }

    public boolean estaEnHorario() {

        if (horaApertura == null || horaCierre == null) {
            return false;
        }

        LocalTime ahora = LocalTime.now();

        if (horaApertura.equals(horaCierre)) {
            return true;
        }

        if (horaApertura.isBefore(horaCierre)) {

            return !ahora.isBefore(horaApertura)
                    && ahora.isBefore(horaCierre);
        }

        return !ahora.isBefore(horaApertura)
                || ahora.isBefore(horaCierre);
    }

    public boolean permiteReportes() {

        return estaEnHorario()
                && estado == EstadoRestaurante.ABIERTO;
    }

    public EstadoRestaurante getEstadoActual() {

        if (!estaEnHorario()) {
            return EstadoRestaurante.CERRADO;
        }

        return estado;
    }
}
