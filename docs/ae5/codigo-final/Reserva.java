package edu.uees.refactor.domain;

public class Reserva {

    private final String id;
    private final Correo correo;
    private final PeriodoReserva periodo;
    private final String tipo;
    private EstadoReserva estado = EstadoReserva.PENDIENTE;

    public Reserva(
            String id,
            Correo correo,
            PeriodoReserva periodo,
            String tipo) {

        this.id = id;
        this.correo = correo;
        this.periodo = periodo;
        this.tipo = tipo;
    }

    public void confirmar() {
        estado = EstadoReserva.CONFIRMADA;
    }

    public boolean tieneCorreoValido() {
        return correo != null && correo.esValido();
    }

    public boolean tienePeriodoValido() {
        return periodo != null && periodo.esValido();
    }

    /** Comparacion exacta, como en el codigo heredado: "vip" no es VIP (LB-08). */
    public boolean esVip() {
        return "VIP".equals(tipo);
    }

    public String getId() {
        return id;
    }

    public Correo getCorreo() {
        return correo;
    }

    public PeriodoReserva getPeriodo() {
        return periodo;
    }

    public String getTipo() {
        return tipo;
    }

    public EstadoReserva getEstado() {
        return estado;
    }
}
