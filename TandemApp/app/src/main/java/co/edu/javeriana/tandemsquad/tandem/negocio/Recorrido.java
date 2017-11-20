package co.edu.javeriana.tandemsquad.tandem.negocio;

import com.google.firebase.database.Exclude;

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

    private String key;
    private Marcador inicio;
    private Marcador fin;
    private String fecha;
    private String hora;
    @Exclude
    private List<Usuario> participantes;
    private Estado estado;
    private Privacidad privacidad;
    private String endName;
    private Tipo tipo;
    private Clima clima;

    public Recorrido() {
    }

    public Recorrido(Marcador inicio, Marcador fin) {
        this.inicio = inicio;
        this.fin = fin;
        participantes = new ArrayList<>();
    }

    public Recorrido(String key, Marcador inicio, Marcador fin, String fecha, String hora, Estado estado, Privacidad privacidad, Tipo tipo, String nombreFin) {
        this.key = key;
        this.inicio = inicio;
        this.fin = fin;
        this.fecha = fecha;
        this.hora = hora;
        this.estado = estado;
        this.privacidad = privacidad;
        this.tipo = tipo;
        this.endName = nombreFin;
        participantes = new ArrayList<>();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void agregarParticipante(Usuario usuario) {
        participantes.add(usuario);
    }

    @Exclude
    public List<Usuario> getParticipantes() {
        return participantes;
    }

    public String getEstado() {
        return estado.name();
    }

    public void setEstado(String estadoString) {
        estado = Estado.valueOf(estadoString);
    }

    @Exclude
    public Estado getEstadoVal() {
        return estado;
    }

    @Exclude
    public void setEstadoVal(Estado estado) {
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

    public String getPrivacidad() {
        return privacidad.name();
    }

    public void setPrivacidad(String privacidadString) {
        privacidad = Privacidad.valueOf(privacidadString);
    }

    @Exclude
    public Privacidad getPrivacidadVal() { return privacidad; }

    @Exclude
    public void setPrivacidadVal(Privacidad privacidad) { this.privacidad = privacidad; }

    public String getTipo() {
        return tipo.name();
    }

    public void setTipo(String tipoString) {
        tipo = Tipo.valueOf(tipoString);
    }

    @Exclude
    public Tipo getTipoVal() { return tipo; }

    @Exclude
    public void setTipoVal(Tipo tipo) { this.tipo = tipo; }

    public String getHora() { return hora; }

    public void setHora(String hora) { this.hora = hora; }

    public String getEndName() {
        return endName;
    }

    public void setEndName(String endName) {
        this.endName = endName;
    }
}
