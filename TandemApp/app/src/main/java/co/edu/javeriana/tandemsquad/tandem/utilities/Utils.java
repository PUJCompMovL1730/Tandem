package co.edu.javeriana.tandemsquad.tandem.utilities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import co.edu.javeriana.tandemsquad.tandem.google.pathTracking.DownloadTask;

public class Utils {
    public static Bitmap getImageFormUri(Activity activity, Uri uri) {
        InputStream imageStream = null;
        try {
            imageStream = activity.getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 6;
        return BitmapFactory.decodeStream(imageStream, null, options);
    }

    public static File createImageFile(Activity activity) {
        try {
            String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            String imageFileName = "tandem_" + timeStamp;
            File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );

            return image;
        } catch (IOException e) {}
        return null;
    }

    public static double distance(double lat1, double long1, double lat2, double long2) {
        double latDistance = Math.toRadians(lat1 - lat2);
        double lngDistance = Math.toRadians(long1 - long2);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1))  * Math.cos(Math.toRadians(lat2))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a),  Math.sqrt(1 - a));
        double result = 6371 * c;
        return Math.round(result*100.0)/100.0;
    }
}
