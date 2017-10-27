package co.edu.javeriana.tandemsquad.tandem.negocio;

import java.util.GregorianCalendar;

public class Recorrido {

    public enum Estado {
        PUBLICADO, CASUAL, VIAJE;
    }

    private GregorianCalendar horaInicio;
    private GregorianCalendar horaFinal;
    private Estado estado;

    public Recorrido(Estado estado) {
        this.estado = estado;
        horaInicio = new GregorianCalendar();
        horaFinal = new GregorianCalendar();
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public GregorianCalendar getHoraInicio() {
        return horaInicio;
    }

    public GregorianCalendar getHoraFinal() {
        return horaFinal;
    }
}
