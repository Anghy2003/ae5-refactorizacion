package edu.uees.refactor.domain;

import java.time.LocalDateTime;

/**
 * Periodo de una tutoria: inicio y fin como un solo concepto.
 *
 * Introducido en la Refactorizacion 3 (Ae5) para agrupar el Data Clump
 * inicio + fin y llevar la regla "fin posterior a inicio" junto al dato.
 *
 * Decision de alcance: el constructor NO lanza excepcion ante un periodo
 * invalido. El contrato heredado exige que ServicioReservas responda 0 en
 * ese caso (LB-04) y cambiarlo seria un cambio funcional, no una
 * refactorizacion. La invariante se expone con esValido().
 */
public record PeriodoReserva(LocalDateTime inicio, LocalDateTime fin) {

    public boolean esValido() {
        return inicio != null
                && fin != null
                && fin.isAfter(inicio);
    }
}
