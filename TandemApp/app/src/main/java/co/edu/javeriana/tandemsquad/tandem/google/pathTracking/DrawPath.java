package co.edu.javeriana.tandemsquad.tandem.google.pathTracking;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import co.edu.javeriana.tandemsquad.tandem.utilities.Utils;

public class DrawPath extends AsyncTask<Void, Void, Void> {

    private GoogleMap googleMap;
    private LatLng origin;
    private LatLng destiny;
    private String url;

    public DrawPath(GoogleMap googleMap, LatLng origin, LatLng destiny) {
        this.googleMap = googleMap;
        this.origin = origin;
        this.destiny = destiny;
    }

    @Override
    protected void onPreExecute() {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + destiny.latitude + "," + destiny.longitude;
        String sensor = "sensor=false";
        String parameters = str_origin + "&" + str_dest + "&" + sensor;
        String output = "json";
        url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        Log.i("Call Rest service", url);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        DownloadTask downloadTask = new DownloadTask(googleMap);
        downloadTask.execute(url);
        return null;
    }
}
