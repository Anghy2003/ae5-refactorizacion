package edu.uees.refactor.infraestructura;

import edu.uees.refactor.domain.Reserva;

/**
 * Persistencia de reservas.
 *
 * Extraida de ServicioReservas en la Refactorizacion 2 (Ae5). Hoy sigue
 * siendo una simulacion por consola, pero ya es la unica clase que cambia
 * si manana se guarda en una base de datos.
 */
public class RepositorioReservas {

    public void guardar(Reserva reserva) {
        System.out.println(
                "Guardando reserva " + reserva.getId()
        );
    }
}
