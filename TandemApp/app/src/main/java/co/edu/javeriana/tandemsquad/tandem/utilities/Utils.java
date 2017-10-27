package co.edu.javeriana.tandemsquad.tandem.utilities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
    public static Bitmap getImageFormUri(Activity activity, Uri uri) {
        InputStream imageStream = null;
        try {
            imageStream = activity.getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return BitmapFactory.decodeStream(imageStream);
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
}
