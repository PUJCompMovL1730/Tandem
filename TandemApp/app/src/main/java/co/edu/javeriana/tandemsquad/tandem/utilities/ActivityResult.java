package co.edu.javeriana.tandemsquad.tandem.utilities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;

public class ActivityResult {
    public static final int GALLERY_INTENT = 0;
    public static final int CAMERA_INTENT = 1;
    public static final int CROP_INTENT = 2;
    public static final int GOOGLE_SIGN_IN = 3;
    public static final int REQUEST_CHECK_SETTINGS = 4;

    public static void startGalleryActivity(Activity activity) {
        Intent galleryintent = new Intent(Intent.ACTION_PICK, null);
        galleryintent.setType("image/*");
        activity.startActivityForResult(Intent.createChooser(galleryintent, "Escoje una foto"), ActivityResult.GALLERY_INTENT);
    }

    public static Uri startCameraActivity(Activity activity, Uri imageUri) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = Utils.createImageFile(activity);

        if (photoFile != null) {
            imageUri = FileProvider.getUriForFile(activity, "co.edu.javeriana.tandemsquad.tandem.fileprovider", photoFile);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            cameraIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            activity.startActivityForResult(cameraIntent, CAMERA_INTENT);
        }
        return imageUri;
    }

    public static void startCropImage(Activity activity, Uri uri) {
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        cropIntent.setDataAndType(uri, "image/*");
        cropIntent.putExtra("crop", "true");
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        cropIntent.putExtra("outputX", 250);
        cropIntent.putExtra("outputY", 250);
        cropIntent.putExtra("return-data", true);
        activity.startActivityForResult(cropIntent, ActivityResult.CROP_INTENT);
    }

    public static void startGoogleLogin(Activity activity, GoogleApiClient googleApiClient){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        activity.startActivityForResult(signInIntent, ActivityResult.GOOGLE_SIGN_IN);
    }
}
