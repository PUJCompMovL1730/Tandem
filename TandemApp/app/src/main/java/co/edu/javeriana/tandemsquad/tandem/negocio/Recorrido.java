package co.edu.javeriana.tandemsquad.tandem.negocio;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class Recorrido {

    public enum Estado {
        PUBLICADO, CASUAL, VIAJE, TERMINADO;
    }

    private Marcador inicio;
    private Marcador fin;
    private GregorianCalendar horaInicio;
    private GregorianCalendar horaFinal;
    private List<Usuario> participantes;
    private Estado estado;
    private Clima clima;

    public Recorrido(Marcador inicio, Marcador fin, Estado estado) {
        this.inicio = inicio;
        this.fin = fin;
        this.horaInicio = new GregorianCalendar();
        this.horaFinal = new GregorianCalendar();
        this.estado = estado;
        participantes = new ArrayList<>();
    }

    public void agregarParticipante( Usuario usuario )
    {
        participantes.add(usuario);
    }

    public List<Usuario> getParticipantes()
    {
        return participantes;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public GregorianCalendar getHoraInicio() {
        return horaInicio;
    }

    public GregorianCalendar getHoraFinal() {
        return horaFinal;
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

    public void setHoraInicio(GregorianCalendar horaInicio) {
        this.horaInicio = horaInicio;
    }

    public void setHoraFinal(GregorianCalendar horaFinal) {
        this.horaFinal = horaFinal;
    }
}
