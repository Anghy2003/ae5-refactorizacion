package edu.uees.refactor.domain;

/**
 * Correo electronico del estudiante.
 *
 * Introducido en la Refactorizacion 4 (Ae5): la regla de validez deja de
 * estar escrita dentro de ServicioReservas y vive junto al dato, en un
 * solo lugar reutilizable.
 *
 * La regla se conserva exactamente como era en el codigo heredado
 * (no nulo y contiene "@"), incluido lo que hoy acepta, por ejemplo "a@".
 * Endurecerla seria un cambio funcional y se decide aparte.
 */
public record Correo(String valor) {

    public boolean esValido() {
        return valor != null && valor.contains("@");
    }

    @Override
    public String toString() {
        return valor;
    }
}
