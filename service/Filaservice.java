package service;

import dao.FilaDAO;
import dao.RestauranteDAO;
import dao.TurnoDAO;
import model.Fila;
import model.Restaurante;
import model.Turno;
import model.Usuario;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class Filaservice {

    private static final Object BLOQUEO_CAMBIOS = new Object();

    private final Authservice authservice;
    private final FilaDAO filaDAO;
    private final TurnoDAO turnoDAO;
    private final RestauranteDAO restauranteDAO;

    public Filaservice(Authservice authservice) {
        this(authservice, new FilaDAO(), new TurnoDAO(), new RestauranteDAO());
    }

    public Filaservice(Authservice authservice, FilaDAO filaDAO,
                       TurnoDAO turnoDAO, RestauranteDAO restauranteDAO) {
        this.authservice = Objects.requireNonNull(authservice, "Falta Authservice.");
        this.filaDAO = Objects.requireNonNull(filaDAO, "Falta FilaDAO.");
        this.turnoDAO = Objects.requireNonNull(turnoDAO, "Falta TurnoDAO.");
        this.restauranteDAO = Objects.requireNonNull(restauranteDAO, "Falta RestauranteDAO.");
    }

    public List<Restaurante> obtenerRestaurantes() {
        return restauranteDAO.obtenerTodos();
    }

    public Fila obtenerFila(int idRestaurante) {
        buscarRestaurante(idRestaurante);
        Fila fila = filaDAO.buscarPorRestaurante(idRestaurante);
        return fila != null && LocalDate.now().equals(fila.getFecha()) ? fila : null;
    }

    public Fila abrirFila(int idRestaurante) {
        synchronized (BLOQUEO_CAMBIOS) {
            exigirEncargado();
            Restaurante restaurante = buscarRestaurante(idRestaurante);
            exigirRestauranteActivo(restaurante);
            Fila fila = filaDAO.buscarPorRestaurante(idRestaurante);

            if (fila != null && LocalDate.now().equals(fila.getFecha())) {
                if ("ABIERTA".equals(fila.getEstado())) {
                    return fila;
                }
                return guardarEstadoFila(fila, "ABIERTA");
            }

            Fila nueva = new Fila(0, idRestaurante, "ABIERTA", LocalDate.now());
            if (!filaDAO.crearFila(nueva)) {
                throw new IllegalStateException("No se pudo crear la fila.");
            }

            Fila guardada = filaDAO.buscarPorRestaurante(idRestaurante);
            if (guardada == null || guardada.getIdFila() <= 0
                    || !nueva.getFecha().equals(guardada.getFecha())
                    || !"ABIERTA".equals(guardada.getEstado())) {
                throw new IllegalStateException(
                        "Se guardo la fila, pero no se pudo recuperarla. Actualiza antes de repetir.");
            }
            return guardada;
        }
    }

    public Fila pausarFila(int idFila) {
        synchronized (BLOQUEO_CAMBIOS) {
            exigirEncargado();
            Fila fila = buscarFila(idFila);
            exigirFilaDeHoy(fila);
            if (!"ABIERTA".equals(fila.getEstado())) {
                throw new IllegalStateException("Solo se puede pausar una fila abierta.");
            }
            return guardarEstadoFila(fila, "PAUSADA");
        }
    }

    public Fila cerrarFila(int idFila) {
        synchronized (BLOQUEO_CAMBIOS) {
            exigirEncargado();
            Fila fila = buscarFila(idFila);
            return "CERRADA".equals(fila.getEstado())
                    ? fila : guardarEstadoFila(fila, "CERRADA");
        }
    }

    public Turno solicitarTurno(int idRestaurante) {
        synchronized (BLOQUEO_CAMBIOS) {
            Usuario estudiante = exigirEstudiante();
            Restaurante restaurante = buscarRestaurante(idRestaurante);
            exigirRestauranteActivo(restaurante);
            Fila fila = filaDAO.buscarPorRestaurante(idRestaurante);
            if (fila == null) {
                throw new IllegalStateException("El restaurante no tiene una fila disponible.");
            }
            exigirFilaDeHoy(fila);
            exigirFilaAbierta(fila);

            if (turnoDAO.buscarTurnoActivo(estudiante.getIdUsuario()) != null
                    || turnoDAO.tieneTurnoActivo(fila.getIdFila(), estudiante.getIdUsuario())) {
                throw new IllegalStateException(
                        "Ya tienes un turno activo. Cancelalo o espera a terminar la atencion.");
            }

            int numero = turnoDAO.obtenerSiguienteNumero(fila.getIdFila());
            if (numero <= 0) {
                throw new IllegalStateException("No se pudo obtener el numero de turno.");
            }
            Turno nuevo = new Turno(0, fila.getIdFila(), estudiante.getIdUsuario(),
                    numero, "ESPERANDO", LocalDateTime.now());
            if (!turnoDAO.crearTurno(nuevo)) {
                throw new IllegalStateException(
                        "No se pudo guardar el turno. Consulta tu turno activo antes de repetir.");
            }

            Turno guardado = turnoDAO.buscarTurnoActivo(estudiante.getIdUsuario());
            if (guardado == null || guardado.getIdTurno() <= 0
                    || guardado.getIdFila() != fila.getIdFila()
                    || guardado.getIdUsuario() != estudiante.getIdUsuario()
                    || guardado.getNumeroTurno() != numero) {
                throw new IllegalStateException(
                        "Se guardo la solicitud, pero no se pudo recuperar el turno. Consulta Mis turnos.");
            }
            return guardado;
        }
    }

    public Turno consultarMiTurno() {
        Usuario estudiante = exigirEstudiante();
        return turnoDAO.buscarTurnoActivo(estudiante.getIdUsuario());
    }

    public int consultarMiPosicion() {
        synchronized (BLOQUEO_CAMBIOS) {
            Turno turno = consultarMiTurno();
            if (turno == null) {
                return -1;
            }
            if ("LLAMADO".equals(turno.getEstado())) {
                return 0;
            }
            if (!"ESPERANDO".equals(turno.getEstado())) {
                throw new IllegalStateException("El turno ya no esta esperando.");
            }
            int posicion = turnoDAO.obtenerPosicion(turno.getIdFila(), turno.getIdUsuario());
            if (posicion < 1) {
                throw new IllegalStateException("No se pudo consultar la posicion.");
            }
            return posicion;
        }
    }

    public Turno cancelarMiTurno() {
        synchronized (BLOQUEO_CAMBIOS) {
            Turno turno = consultarMiTurno();
            if (turno == null) {
                throw new IllegalStateException("No se encontro un turno activo para cancelar.");
            }
            if (!"ESPERANDO".equals(turno.getEstado()) && !"LLAMADO".equals(turno.getEstado())) {
                throw new IllegalStateException("El turno ya no se puede cancelar.");
            }
            return guardarEstadoTurno(turno, "CANCELADO");
        }
    }

    public List<Turno> obtenerPendientes(int idFila) {
        exigirEncargado();
        buscarFila(idFila);
        return turnoDAO.obtenerPendientes(idFila);
    }

    public Turno llamarSiguiente(int idFila) {
        synchronized (BLOQUEO_CAMBIOS) {
            exigirEncargado();
            Fila fila = buscarFila(idFila);
            exigirFilaDeHoy(fila);
            exigirFilaAbierta(fila);
            exigirRestauranteActivo(buscarRestaurante(fila.getIdRestaurante()));

            Turno turno = turnoDAO.obtenerSiguiente(idFila);
            if (turno == null) {
                return null;
            }
            if (turno.getIdFila() != idFila || !"ESPERANDO".equals(turno.getEstado())) {
                throw new IllegalStateException("El turno consultado ya no esta disponible.");
            }
            return guardarEstadoTurno(turno, "LLAMADO");
        }
    }

    public Turno marcarAtendido(Turno turno) {
        return finalizarTurno(turno, "ATENDIDO");
    }

    public Turno marcarAusente(Turno turno) {
        return finalizarTurno(turno, "AUSENTE");
    }

    private Turno finalizarTurno(Turno referencia, String estadoFinal) {
        synchronized (BLOQUEO_CAMBIOS) {
            exigirEncargado();
            if (referencia == null || referencia.getIdTurno() <= 0
                    || referencia.getIdUsuario() <= 0 || referencia.getIdFila() <= 0) {
                throw new IllegalArgumentException("Debes seleccionar un turno valido.");
            }

            Turno actual = turnoDAO.buscarTurnoActivo(referencia.getIdUsuario());
            if (actual == null || actual.getIdTurno() != referencia.getIdTurno()
                    || actual.getIdFila() != referencia.getIdFila()
                    || actual.getIdUsuario() != referencia.getIdUsuario()) {
                throw new IllegalStateException("Ese turno ya no esta activo. Actualiza la pantalla.");
            }
            if (!"LLAMADO".equals(actual.getEstado())) {
                throw new IllegalStateException("Primero debes llamar al estudiante.");
            }
            return guardarEstadoTurno(actual, estadoFinal);
        }
    }

    private Usuario exigirSesion() {
        Usuario usuario = authservice.getUsuarioActual();
        if (usuario == null) {
            throw new IllegalStateException("Debes iniciar sesion.");
        }
        return usuario;
    }

    private Usuario exigirEstudiante() {
        Usuario usuario = exigirSesion();
        if (!"ESTUDIANTE".equals(usuario.getRol())) {
            throw new IllegalStateException("Esta operacion corresponde a estudiantes.");
        }
        return usuario;
    }

    private void exigirEncargado() {
        String rol = exigirSesion().getRol();
        if (!"ENCARGADO".equals(rol) && !"ADMIN".equals(rol)) {
            throw new IllegalStateException("Solo un encargado o administrador puede hacer esto.");
        }
    }

    private Restaurante buscarRestaurante(int idRestaurante) {
        validarId(idRestaurante, "restaurante");
        Restaurante restaurante = restauranteDAO.buscarPorId(idRestaurante);
        if (restaurante == null) {
            throw new IllegalStateException("No se pudo encontrar el restaurante.");
        }
        return restaurante;
    }

    private Fila buscarFila(int idFila) {
        validarId(idFila, "fila");
        Fila fila = filaDAO.buscarPorId(idFila);
        if (fila == null) {
            throw new IllegalStateException("No se pudo encontrar la fila.");
        }
        return fila;
    }

    private void validarId(int id, String entidad) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID de " + entidad + " debe ser positivo.");
        }
    }

    private void exigirRestauranteActivo(Restaurante restaurante) {
        if (!restaurante.isActivo()) {
            throw new IllegalStateException("El restaurante esta inactivo.");
        }
    }

    private void exigirFilaDeHoy(Fila fila) {
        if (!LocalDate.now().equals(fila.getFecha())) {
            throw new IllegalStateException("La fila no corresponde al dia actual.");
        }
    }

    private void exigirFilaAbierta(Fila fila) {
        if (!"ABIERTA".equals(fila.getEstado())) {
            throw new IllegalStateException("La fila esta pausada o cerrada.");
        }
    }

    private Fila guardarEstadoFila(Fila fila, String estado) {
        if (!filaDAO.cambiarEstado(fila.getIdFila(), estado)) {
            throw new IllegalStateException("No se pudo actualizar el estado de la fila.");
        }

        return new Fila(fila.getIdFila(), fila.getIdRestaurante(), estado, fila.getFecha());
    }

    private Turno guardarEstadoTurno(Turno turno, String estado) {
        if (!turnoDAO.cambiarEstado(turno.getIdTurno(), estado)) {
            throw new IllegalStateException("No se pudo actualizar el estado del turno.");
        }
        return new Turno(turno.getIdTurno(), turno.getIdFila(), turno.getIdUsuario(),
                turno.getNumeroTurno(), estado, turno.getFechaHora());
    }
}
