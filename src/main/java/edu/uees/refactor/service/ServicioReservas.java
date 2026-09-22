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
 */
public class ServicioReservas {

    static final int HORAS_MINIMAS_ANTICIPACION = 2;
    static final double TARIFA_BASE = 40;
    static final double FACTOR_DESCUENTO_VIP = 0.85;

    private final RepositorioReservas repositorio;
    private final NotificadorReservas notificador;

    public ServicioReservas() {
        this(new RepositorioReservas(), new NotificadorReservas());
    }

    public ServicioReservas(
            RepositorioReservas repositorio,
            NotificadorReservas notificador) {
        this.repositorio = repositorio;
        this.notificador = notificador;
    }

    public double procesar(
            Reserva reserva,
            int horasAnticipacion) {

        if (!esProcesable(reserva, horasAnticipacion)) {
            return 0;
        }

        double total = calcularTotal(reserva);

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

    private double calcularTotal(Reserva reserva) {
        double total = TARIFA_BASE;
        if ("VIP".equals(reserva.getTipo())) {
            total = total * FACTOR_DESCUENTO_VIP;
        }
        return total;
    }
}
