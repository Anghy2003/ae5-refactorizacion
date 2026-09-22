package edu.uees.refactor.service;

import edu.uees.refactor.domain.Correo;
import edu.uees.refactor.domain.PeriodoReserva;
import edu.uees.refactor.domain.Reserva;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PoliticaPreciosTest {

    private static final LocalDateTime INICIO =
            LocalDateTime.of(2026, 9, 22, 10, 0);

    private final PoliticaPrecios politica = new PoliticaPrecios();

    @Test
    void reservaNormalPagaLaTarifaBase() {
        // Arrange
        Reserva reserva = reservaDeTipo("NORMAL");

        // Act
        double total = politica.calcularTotal(reserva);

        // Assert
        assertEquals(40.0, total, 0.0001);
    }

    @Test
    void reservaVipPagaConQuincePorCientoDeDescuento() {
        // Arrange
        Reserva reserva = reservaDeTipo("VIP");

        // Act
        double total = politica.calcularTotal(reserva);

        // Assert
        assertEquals(34.0, total, 0.0001);
    }

    @Test
    void tipoVipEnMinusculaSigueSinDescuentoComoEnElCodigoHeredado() {
        // Arrange
        Reserva reserva = reservaDeTipo("vip");

        // Act
        double total = politica.calcularTotal(reserva);

        // Assert
        assertEquals(40.0, total, 0.0001);
    }

    private static Reserva reservaDeTipo(String tipo) {
        return new Reserva("R-001", new Correo("ana@uees.edu.ec"),
                new PeriodoReserva(INICIO, INICIO.plusHours(1)), tipo);
    }
}
