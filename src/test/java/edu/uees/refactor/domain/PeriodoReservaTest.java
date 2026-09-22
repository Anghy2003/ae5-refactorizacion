package edu.uees.refactor.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PeriodoReservaTest {

    private static final LocalDateTime INICIO =
            LocalDateTime.of(2026, 9, 22, 10, 0);

    @Test
    void finPosteriorAlInicioEsValido() {
        // Arrange
        PeriodoReserva periodo = new PeriodoReserva(INICIO, INICIO.plusHours(1));

        // Act
        boolean valido = periodo.esValido();

        // Assert
        assertTrue(valido);
    }

    @Test
    void finIgualAlInicioNoEsValido() {
        // Arrange
        PeriodoReserva periodo = new PeriodoReserva(INICIO, INICIO);

        // Act
        boolean valido = periodo.esValido();

        // Assert
        assertFalse(valido);
    }

    @Test
    void finAnteriorAlInicioNoEsValido() {
        // Arrange
        PeriodoReserva periodo = new PeriodoReserva(INICIO, INICIO.minusMinutes(1));

        // Act
        boolean valido = periodo.esValido();

        // Assert
        assertFalse(valido);
    }

    @Test
    void periodoConFechaNulaNoEsValido() {
        // Arrange
        PeriodoReserva sinInicio = new PeriodoReserva(null, INICIO);
        PeriodoReserva sinFin = new PeriodoReserva(INICIO, null);

        // Act & Assert
        assertFalse(sinInicio.esValido());
        assertFalse(sinFin.esValido());
    }
}
