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
        if (!tieneCorreoValido(reserva)) {
            return false;
        }
        if (!tienePeriodoValido(reserva)) {
            return false;
        }
        return cumpleAnticipacionMinima(horasAnticipacion);
    }

    private boolean tieneCorreoValido(Reserva reserva) {
        return reserva.getCorreo() != null
                && reserva.getCorreo().contains("@");
    }

    private boolean tienePeriodoValido(Reserva reserva) {
        return reserva.getInicio() != null
                && reserva.getFin() != null
                && reserva.getFin().isAfter(reserva.getInicio());
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
