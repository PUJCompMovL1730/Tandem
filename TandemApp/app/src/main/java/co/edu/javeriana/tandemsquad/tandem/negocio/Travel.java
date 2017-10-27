package co.edu.javeriana.tandemsquad.tandem.negocio;

import com.google.android.gms.maps.model.LatLng;

public class Travel {

    private LatLng origin;
    private LatLng destiny;

    public Travel(LatLng origin, LatLng destiny) {
        this.origin = origin;
        this.destiny = destiny;
    }

    public Travel(double latitudeOrigin, double longitudeOrigin, double latitudeDestiny, double longitudeDestiny) {
        this.origin = new LatLng(latitudeOrigin, longitudeOrigin);
        this.destiny = new LatLng(latitudeDestiny, longitudeDestiny);
    }

    public LatLng getOrigin() {
        return origin;
    }

    public LatLng getDestiny() {
        return destiny;
    }

    public String getOriginName() {
        return origin.latitude + " , " + origin.longitude;
    }

    public String getDestinyName() {
        return destiny.latitude + " , " + destiny.longitude;
    }

    public double getDistance() {
        double latDistance = Math.toRadians(origin.latitude - destiny.latitude);
        double lngDistance = Math.toRadians(origin.longitude - destiny.longitude);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(origin.latitude))  * Math.cos(Math.toRadians(destiny.latitude))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a),  Math.sqrt(1 - a));
        double result = 6371 * c;
        return Math.round(result*100.0)/100.0;
    }
}
