package co.edu.javeriana.tandemsquad.tandem.negocio;

import android.app.Activity;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.twitter.sdk.android.core.models.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class Historia {
    private String imagenUri;
    private String mensaje;
    private Usuario usuario;
    private Bitmap imagen;
    private GregorianCalendar fecha;
    private List<ImageView> asyncImageDeliver;

    public Historia(String mensaje, Usuario usuario, GregorianCalendar fecha, String imagenUri) {
        this.mensaje = mensaje;
        this.usuario = usuario;
        this.fecha = fecha;
        this.imagenUri = imagenUri;
        this.imagen = null;
        this.asyncImageDeliver = new ArrayList<>();
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
        this.deliverImages();
    }

    public GregorianCalendar getFecha() {
        return fecha;
    }

    public void setFecha(GregorianCalendar fecha) {
        this.fecha = fecha;
    }

    public void addAsyncImageListener(final ImageView image) {
        if (getImagen() == null)
            this.asyncImageDeliver.add(image);
        else
        {
            Activity host = (Activity) image.getContext();
            if (host != null) {
                host.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        image.setImageBitmap(getImagen());
                    }
                });
            }
        }
    }

    public void deliverImages() {
        for (final ImageView view : this.asyncImageDeliver) {
            Activity host = (Activity) view.getContext();
            if (host != null) {
                host.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        view.setImageBitmap(getImagen());
                    }
                });
            }
        }
    }
}
