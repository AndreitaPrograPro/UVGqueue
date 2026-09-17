import dao.RestauranteDAO;
import dao.UsuarioDAO;
import dao.FilaDAO;
import dao.TurnoDAO;

import model.Restaurante;
import model.Usuario;
import model.Fila;
import model.Turno;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        System.out.println("=== PRUEBA DE DAO UVGQUEUE ===");


        // ---------------------------------
        // 1. PROBAR RESTAURANTE DAO
        // ---------------------------------

        System.out.println("\n--- RESTAURANTES ---");

        RestauranteDAO restauranteDAO = new RestauranteDAO();

        List<Restaurante> restaurantes =
                restauranteDAO.obtenerTodos();

        for (Restaurante restaurante : restaurantes) {

            System.out.println(
                    restaurante.getIdRestaurante()
                    + " | "
                    + restaurante.getNombre()
                    + " | "
                    + restaurante.getUbicacion()
            );
        }


        // ---------------------------------
        // 2. PROBAR USUARIO DAO
        // ---------------------------------

        System.out.println("\n--- USUARIO ---");

        UsuarioDAO usuarioDAO = new UsuarioDAO();

        Usuario usuario =
                usuarioDAO.buscarPorCorreo("prueba@uvg.edu.gt");

        if (usuario != null) {

            System.out.println(
                    "Usuario encontrado: "
                    + usuario.getNombre()
                    + " | "
                    + usuario.getRol()
            );

        } else {

            System.out.println("Usuario de prueba no encontrado.");
        }


        // ---------------------------------
        // 3. PROBAR FILA DAO
        // ---------------------------------

        System.out.println("\n--- FILA ---");

        FilaDAO filaDAO = new FilaDAO();

        Fila fila =
                filaDAO.buscarPorRestaurante(1);

        if (fila != null) {

            System.out.println(
                    "Fila encontrada"
                    + " | ID: " + fila.getIdFila()
                    + " | Restaurante: " + fila.getIdRestaurante()
                    + " | Estado: " + fila.getEstado()
            );

        } else {

            System.out.println(
                    "No hay fila para el restaurante 1."
            );
        }


        // ---------------------------------
        // 4. PROBAR TURNO DAO
        // ---------------------------------

        System.out.println("\n--- TURNOS ---");

        if (fila != null) {

            TurnoDAO turnoDAO = new TurnoDAO();

            List<Turno> pendientes =
                    turnoDAO.obtenerPendientes(fila.getIdFila());

            System.out.println(
                    "Turnos esperando: " + pendientes.size()
            );

            for (Turno turno : pendientes) {

                System.out.println(
                        "Turno #" + turno.getNumeroTurno()
                        + " | Usuario: " + turno.getIdUsuario()
                        + " | Estado: " + turno.getEstado()
                );
            }


            // Ver quién sigue
            Turno siguiente =
                    turnoDAO.obtenerSiguiente(fila.getIdFila());

            if (siguiente != null) {

                System.out.println(
                        "Siguiente turno: #"
                        + siguiente.getNumeroTurno()
                );

            } else {

                System.out.println(
                        "No hay personas esperando."
                );
            }
        }


        System.out.println("\n=== FIN DE PRUEBA ===");
    }
}