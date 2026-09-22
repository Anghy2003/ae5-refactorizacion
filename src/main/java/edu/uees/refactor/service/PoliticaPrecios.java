package edu.uees.refactor.service;

import edu.uees.refactor.domain.Reserva;

/**
 * Politica de precios de las tutorias.
 *
 * Extraida de ServicioReservas en la Refactorizacion 5 (Ae5): la tarifa
 * base y el descuento VIP son la razon de cambio de Finanzas, y ahora
 * tienen una clase propia. El calculo es identico al heredado:
 * TARIFA_BASE * FACTOR_DESCUENTO_VIP para VIP, TARIFA_BASE para el resto.
 */
public class PoliticaPrecios {

    static final double TARIFA_BASE = 40;
    static final double FACTOR_DESCUENTO_VIP = 0.85;

    public double calcularTotal(Reserva reserva) {
        double total = TARIFA_BASE;
        if (reserva.esVip()) {
            total = total * FACTOR_DESCUENTO_VIP;
        }
        return total;
    }
}
