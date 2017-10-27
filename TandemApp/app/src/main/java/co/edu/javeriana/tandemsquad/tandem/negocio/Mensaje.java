package co.edu.javeriana.tandemsquad.tandem.negocio;

import java.util.GregorianCalendar;

public class Mensaje {

    private String texto;
    private GregorianCalendar fecha;

    public Mensaje(String texto) {
        this.texto = texto;
        fecha = new GregorianCalendar();
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public GregorianCalendar getFecha() {
        return fecha;
    }

    public void setFecha(GregorianCalendar fecha) {
        this.fecha = fecha;
    }
}
