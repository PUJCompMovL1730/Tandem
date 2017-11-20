package co.edu.javeriana.tandemsquad.tandementerprise.Mundo;

import com.google.android.gms.maps.model.LatLng;

public class Event
{
    private String initialDate;
    private String finalDate;
    private LatLng initialPosition;
    private LatLng finalPosition;
    private String description;

    public Event( String initialDateP, String finalDateP, LatLng initialPositionP, LatLng finalPositionP, String descriptionP )
    {
        this.initialDate = initialDateP;
        this.finalDate = finalDateP;
        this.initialPosition = initialPositionP;
        this.finalPosition = finalPositionP;
        this.description = descriptionP;
    }

    public String getInitialDate() {
        return initialDate;
    }

    public void setInitialDate(String initialDate) {
        this.initialDate = initialDate;
    }

    public String getFinalDate() {
        return finalDate;
    }

    public void setFinalDate(String finalDate) {
        this.finalDate = finalDate;
    }

    public LatLng getInitialPosition() {
        return initialPosition;
    }

    public void setInitialPosition(LatLng initialPosition) {
        this.initialPosition = initialPosition;
    }

    public LatLng getFinalPosition() {
        return finalPosition;
    }

    public void setFinalPosition(LatLng finalPosition) {
        this.finalPosition = finalPosition;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
