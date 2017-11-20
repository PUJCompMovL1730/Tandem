package co.edu.javeriana.tandemsquad.tandementerprise.Mundo;

import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.List;

public class Enterprise
{
    private String id;
    private String name;
    private String email;
    private List<CustomMarker> markers;
    private List<Event> events;

    public Enterprise( String idP, String nameP, String emailP )
    {
        this.id = idP;
        this.name = nameP;
        this.email = emailP;
        this.markers = new ArrayList<>();
        this.events = new ArrayList<>();
    }

    public String getId() {
        return id;
    }
    public void addEvent( Event evt )
    {
        this.events.add(evt);
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<CustomMarker> getMarkers() {
        return markers;
    }

    public void setMarkers(List<CustomMarker> markers) {
        this.markers = markers;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }
}
