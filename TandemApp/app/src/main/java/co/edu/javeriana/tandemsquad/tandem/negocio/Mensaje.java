package co.edu.javeriana.tandemsquad.tandem.negocio;


import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

public class Mensaje {

    private String texto;
    private GregorianCalendar fecha;
    private Usuario emisor;
    private Usuario receptor;

    private boolean isMe;

    public Mensaje(String texto, Usuario emisor, Usuario receptor) {
        this(texto, emisor, receptor, new GregorianCalendar(), true);
    }

    public Mensaje(String texto, Usuario emisor, Usuario receptor, GregorianCalendar fecha, boolean isMe) {
        this.texto = texto;
        this.fecha = fecha;
        this.emisor = emisor;
        this.receptor = receptor;
        this.isMe = isMe;
    }

    public String getCuteDate() {
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
        return formatter.format(fecha.getTime());
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

    public boolean isMe() {
        return isMe;
    }

    public void setMe(boolean me) {
        isMe = me;
    }
}
