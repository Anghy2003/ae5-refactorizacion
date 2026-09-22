package edu.uees.refactor.infraestructura;

import edu.uees.refactor.domain.Reserva;

/**
 * Notificacion al estudiante.
 *
 * Extraida de ServicioReservas en la Refactorizacion 2 (Ae5). Es la unica
 * clase que cambia si la notificacion pasa de consola a correo real o SMS.
 */
public class NotificadorReservas {

    public void enviarConfirmacion(Reserva reserva) {
        System.out.println(
                "Correo enviado a " + reserva.getCorreo()
        );
    }
}
