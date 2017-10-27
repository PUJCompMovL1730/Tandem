package co.edu.javeriana.tandemsquad.tandem.negocio;

import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class Usuario {

    private String nombre;
    private String correo;
    private Uri foto;
    private Uri portada;
    private String telefono;
    private List<String> amigos;
    private List<Long> historias;
    private List<Long> historial;

    public Usuario(String nombre, String correo) {
        this.nombre = nombre;
        this.correo = correo;
        amigos = new ArrayList<>();
        historias = new ArrayList<>();
        historial = new ArrayList<>();
    }

    public Usuario(String nombre, String correo, Uri foto) {
        this.nombre = nombre;
        this.correo = correo;
        this.foto = foto;
        amigos = new ArrayList<>();
        historias = new ArrayList<>();
        historial = new ArrayList<>();
    }

    public Usuario(String nombre, String correo, Uri foto, Uri portada) {
        this.nombre = nombre;
        this.correo = correo;
        this.foto = foto;
        this.portada = portada;
        amigos = new ArrayList<>();
        historias = new ArrayList<>();
        historial = new ArrayList<>();
    }

    public Usuario(String nombre, String correo, Uri foto, Uri portada, String telefono) {
        this.nombre = nombre;
        this.correo = correo;
        this.foto = foto;
        this.portada = portada;
        this.telefono = telefono;
        amigos = new ArrayList<>();
        historias = new ArrayList<>();
        historial = new ArrayList<>();
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

    public List<String> getAmigos() {
        return amigos;
    }

    public List<Long> getHistorias() {
        return historias;
    }

    public List<Long> getHistorial() {
        return historial;
    }
}
