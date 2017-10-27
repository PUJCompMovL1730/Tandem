package co.edu.javeriana.tandemsquad.tandem.negocio;

import android.net.Uri;

import java.util.GregorianCalendar;

public class Historia {

    private Uri foto;
    private GregorianCalendar fecha;

    public Historia(Uri foto) {
        this.foto = foto;
        fecha = new GregorianCalendar();
    }

    public Uri getFoto() {
        return foto;
    }

    public void setFoto(Uri foto) {
        this.foto = foto;
    }

    public GregorianCalendar getFecha() {
        return fecha;
    }

    public void setFecha(GregorianCalendar fecha) {
        this.fecha = fecha;
    }
}
