package co.edu.javeriana.tandemsquad.tandem.negocio;

import android.graphics.Bitmap;

import com.twitter.sdk.android.core.models.User;

import java.io.Serializable;
import java.util.GregorianCalendar;

public class Historia {

    private String imagenUri;
    private String mensaje;
    private Usuario usuario;
    private Bitmap imagen;
    private GregorianCalendar fecha;

    public Historia(String mensaje, Usuario usuario, GregorianCalendar fecha, String imagenUri) {
        this.mensaje = mensaje;
        this.usuario = usuario;
        this.fecha = fecha;
        this.imagenUri = imagenUri;
        this.imagen = null;
    }

    public String getImagenUri() {
        return imagenUri;
    }

    public void setImagenUri(String imagenUri) {
        this.imagenUri = imagenUri;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Bitmap getImagen() {
        return imagen;
    }

    public void setImagen(Bitmap imagen) {
        this.imagen = imagen;
    }

    public GregorianCalendar getFecha() {
        return fecha;
    }

    public void setFecha(GregorianCalendar fecha) {
        this.fecha = fecha;
    }
}
