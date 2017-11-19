package co.edu.javeriana.tandemsquad.tandem.negocio;

import android.graphics.Bitmap;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class Usuario {

    private String id;
    private String nombre;
    private String correo;
    private Uri foto;
    private Uri portada;
    private String telefono;
    private List<Usuario> amigos;
    private List<Historia> historias;
    private List<Recorrido> historial;
    private Bitmap imagen;

    public Usuario(String id, String nombre, String correo) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        amigos = new ArrayList<>();
        historias = new ArrayList<>();
        historial = new ArrayList<>();
    }

    public Usuario(String id, String nombre, String correo, Uri foto) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.foto = foto;
        amigos = new ArrayList<>();
        historias = new ArrayList<>();
        historial = new ArrayList<>();
    }

    public Usuario(String id, String nombre, String correo, Uri foto, Uri portada) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.foto = foto;
        this.portada = portada;
        amigos = new ArrayList<>();
        historias = new ArrayList<>();
        historial = new ArrayList<>();
    }

    public Usuario(String id, String nombre, String correo, Uri foto, Uri portada, String telefono) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.foto = foto;
        this.portada = portada;
        this.telefono = telefono;
        amigos = new ArrayList<>();
        historias = new ArrayList<>();
        historial = new ArrayList<>();
    }

    public void agregarAmigo( Usuario amigo ) { this.amigos.add(amigo); }

    public void agregarRecorrido( Recorrido r )
    {
        this.historial.add(r);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public Uri getFoto() {
        return foto;
    }

    public void setFoto(Uri foto) {
        this.foto = foto;
    }

    public Uri getPortada() {
        return portada;
    }

    public void setPortada(Uri portada) {
        this.portada = portada;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public List<Usuario> getAmigos() {
        return amigos;
    }

    public void setAmigos(List<Usuario> amigos) {
        this.amigos = amigos;
    }

    public List<Historia> getHistorias() {
        return historias;
    }

    public List<Recorrido> getHistorial() {
        return historial;
    }

    public Bitmap getImagen() {
        return imagen;
    }

    public void setImagen(Bitmap imagen) {
        this.imagen = imagen;
    }
}
