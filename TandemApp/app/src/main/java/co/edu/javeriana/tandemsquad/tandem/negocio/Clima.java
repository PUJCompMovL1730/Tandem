package co.edu.javeriana.tandemsquad.tandem.negocio;

public class Clima {

    public enum Estado {
        SOLEADO, NUBLADO, LLOVISNA, LLUVIA;
    }

    private double temperatura;
    private Estado estado;

    public Clima(double temperatura, Estado estado) {
        this.temperatura = temperatura;
        this.estado = estado;
    }

    public double getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(double temperatura) {
        this.temperatura = temperatura;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }
}
