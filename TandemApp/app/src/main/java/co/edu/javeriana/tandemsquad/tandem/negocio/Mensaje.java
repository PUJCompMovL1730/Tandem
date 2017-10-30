package co.edu.javeriana.tandemsquad.tandem.negocio;

import java.util.GregorianCalendar;

public class Mensaje {

    private String texto;
    private GregorianCalendar fecha;
    private Usuario emisor;
    private Usuario receptor;

    public Mensaje(String texto, Usuario emisor, Usuario receptor, GregorianCalendar fecha) {
        this.texto = texto;
        this.fecha = fecha;
        this.emisor = emisor;
        this.receptor = receptor;
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

    public Usuario getEmisor() {
        return emisor;
    }

    public void setEmisor(Usuario emisor) {
        this.emisor = emisor;
    }

    public Usuario getReceptor() {
        return receptor;
    }

    public void setReceptor(Usuario receptor) {
        this.receptor = receptor;
    }
}
