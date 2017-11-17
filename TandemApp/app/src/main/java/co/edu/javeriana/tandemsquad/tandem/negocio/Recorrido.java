package co.edu.javeriana.tandemsquad.tandem.negocio;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class Recorrido {

    public enum Estado {
        PLANEADO, EN_CURSO, FINALIZADO;
    }

    public enum Privacidad {
        PUBLICO, PRIVADO;
    }

    public enum Tipo {
        VIAJE, FRECUENTE, INSTANTANEO;
    }

    private Marcador inicio;
    private Marcador fin;
    private String fecha;
    private String hora;
    private List<Usuario> participantes;
    private Estado estado;
    private Privacidad privacidad;
    private Tipo tipo;
    private Clima clima;

    public Recorrido(Marcador inicio, Marcador fin) {
        this.inicio = inicio;
        this.fin = fin;
        participantes = new ArrayList<>();
    }

    public Recorrido(Marcador inicio, Marcador fin, String fecha, String hora, Estado estado, Privacidad privacidad, Tipo tipo) {
        this.inicio = inicio;
        this.fin = fin;
        this.fecha = fecha;
        this.hora = hora;
        this.estado = estado;
        this.privacidad = privacidad;
        this.tipo = tipo;
    }

    public void agregarParticipante(Usuario usuario) {
        participantes.add(usuario);
    }

    public List<Usuario> getParticipantes() {
        return participantes;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public String getFecha() {
        return fecha;
    }

    public Marcador getInicio() {
        return inicio;
    }

    public void setInicio(Marcador inicio) {
        this.inicio = inicio;
    }

    public Marcador getFin() {
        return fin;
    }

    public void setFin(Marcador fin) {
        this.fin = fin;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public Privacidad getPrivacidad() { return privacidad; }

    public void setPrivacidad(Privacidad privacidad) { this.privacidad = privacidad; }

    public Tipo getTipo() { return tipo; }

    public void setTipo(Tipo tipo) { this.tipo = tipo; }

    public String getHora() { return hora; }

    public void setHora(String hora) { this.hora = hora; }
}
