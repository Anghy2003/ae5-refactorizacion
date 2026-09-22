package edu.uees.refactor.infraestructura;

import edu.uees.refactor.domain.PeriodoReserva;
import edu.uees.refactor.domain.Reserva;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NotificadorReservasTest {

    private static final LocalDateTime INICIO =
            LocalDateTime.of(2026, 9, 22, 10, 0);

    private ByteArrayOutputStream consola;
    private PrintStream salidaOriginal;

    @BeforeEach
    void capturarConsola() {
        salidaOriginal = System.out;
        consola = new ByteArrayOutputStream();
        System.setOut(new PrintStream(consola, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void restaurarConsola() {
        System.setOut(salidaOriginal);
    }

    @Test
    void enviarConfirmacionEmiteElMensajeHeredadoConElCorreo() {
        // Arrange
        Reserva reserva = new Reserva("R-001", "ana@uees.edu.ec",
                new PeriodoReserva(INICIO, INICIO.plusHours(1)), "NORMAL");
        NotificadorReservas notificador = new NotificadorReservas();

        // Act
        notificador.enviarConfirmacion(reserva);

        // Assert
        assertEquals("Correo enviado a ana@uees.edu.ec",
                consola.toString(StandardCharsets.UTF_8).trim());
    }
}
