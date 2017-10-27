package co.edu.javeriana.tandemsquad.tandem.negocio;

import com.google.android.gms.maps.model.LatLng;

public class Marcador {

    public enum Tipo {
        ACCIDENTE, EMBOTELLAMIENTO, PELIGRO, HUECO, MARCHA, GENERICO;
    }

    private LatLng posicion;
    private Tipo tipo;
    private String descripcion;

    public Marcador(LatLng posicion, Tipo tipo, String descripcion) {
        this.posicion = posicion;
        this.tipo = tipo;
        this.descripcion = descripcion;
    }

    public Marcador(LatLng posicion, Tipo tipo) {
        this.posicion = posicion;
        this.tipo = tipo;
        this.descripcion = null;
    }

    public LatLng getPosicion() {
        return posicion;
    }

    public void setPosicion(LatLng posicion) {
        this.posicion = posicion;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
