package co.edu.javeriana.tandemsquad.tandementerprise.Mundo;

import com.google.android.gms.maps.model.LatLng;

public class CustomMarker
{
    private String name;
    private LatLng position;
    private String description;

    public CustomMarker( String nameP, LatLng positionP, String descriptionP )
    {
        this.name = nameP;
        this.position = positionP;
        this.description = descriptionP;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
