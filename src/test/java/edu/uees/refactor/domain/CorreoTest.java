package edu.uees.refactor.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CorreoTest {

    @Test
    void correoConArrobaEsValido() {
        // Arrange
        Correo correo = new Correo("ana@uees.edu.ec");

        // Act
        boolean valido = correo.esValido();

        // Assert
        assertTrue(valido);
    }

    @Test
    void correoSinArrobaNoEsValido() {
        // Arrange
        Correo correo = new Correo("incorrecto");

        // Act
        boolean valido = correo.esValido();

        // Assert
        assertFalse(valido);
    }

    @Test
    void correoNuloNoEsValido() {
        // Arrange
        Correo correo = new Correo(null);

        // Act
        boolean valido = correo.esValido();

        // Assert
        assertFalse(valido);
    }

    @Test
    void correoConSoloArrobaSigueSiendoValidoComoEnElCodigoHeredado() {
        // Arrange: documenta que la regla NO se endurecio al moverla
        Correo correo = new Correo("a@");

        // Act
        boolean valido = correo.esValido();

        // Assert
        assertTrue(valido);
    }

    @Test
    void seImprimeComoSuValorParaConservarElMensajeHeredado() {
        // Arrange
        Correo correo = new Correo("ana@uees.edu.ec");

        // Act
        String texto = "Correo enviado a " + correo;

        // Assert
        assertEquals("Correo enviado a ana@uees.edu.ec", texto);
    }
}
