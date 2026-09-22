package edu.uees.refactor.service;

import edu.uees.refactor.domain.Reserva;
import edu.uees.refactor.infraestructura.NotificadorReservas;
import edu.uees.refactor.infraestructura.RepositorioReservas;

/**
 * Servicio de reservas de tutorias.
 *
 * Refactorizacion 1 (Ae5): las reglas que antes eran literales y
 * condiciones anonimas dentro de procesar() tienen ahora nombre propio.
 * Refactorizacion 2 (Ae5): la persistencia y la notificacion viven en
 * colaboradores propios; el servicio solo coordina.
 * Refactorizacion 3 (Ae5): la regla del periodo vive en PeriodoReserva.
 * Refactorizacion 4 (Ae5): la regla del correo vive en Correo.
 * Refactorizacion 5 (Ae5): la tarifa y el descuento viven en PoliticaPrecios.
 */
public class ServicioReservas {

    static final int HORAS_MINIMAS_ANTICIPACION = 2;

    private final PoliticaPrecios politicaPrecios;
    private final RepositorioReservas repositorio;
    private final NotificadorReservas notificador;

    public ServicioReservas() {
        this(new PoliticaPrecios(),
                new RepositorioReservas(),
                new NotificadorReservas());
    }

    public ServicioReservas(
            PoliticaPrecios politicaPrecios,
            RepositorioReservas repositorio,
            NotificadorReservas notificador) {
        this.politicaPrecios = politicaPrecios;
        this.repositorio = repositorio;
        this.notificador = notificador;
    }

    public double procesar(
            Reserva reserva,
            int horasAnticipacion) {

        if (!esProcesable(reserva, horasAnticipacion)) {
            return 0;
        }

        double total = politicaPrecios.calcularTotal(reserva);

        repositorio.guardar(reserva);
        notificador.enviarConfirmacion(reserva);
        reserva.confirmar();

        return total;
    }

    private boolean esProcesable(Reserva reserva, int horasAnticipacion) {
        if (reserva == null) {
            return false;
        }
        if (!reserva.tieneCorreoValido()) {
            return false;
        }
        if (!reserva.tienePeriodoValido()) {
            return false;
        }
        return cumpleAnticipacionMinima(horasAnticipacion);
    }

    private boolean cumpleAnticipacionMinima(int horasAnticipacion) {
        return horasAnticipacion >= HORAS_MINIMAS_ANTICIPACION;
    }
}
