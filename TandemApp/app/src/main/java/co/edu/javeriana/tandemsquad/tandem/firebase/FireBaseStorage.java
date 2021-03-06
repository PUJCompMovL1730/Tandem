package co.edu.javeriana.tandemsquad.tandem.firebase;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import co.edu.javeriana.tandemsquad.tandem.negocio.Historia;
import co.edu.javeriana.tandemsquad.tandem.negocio.Usuario;

public class FireBaseStorage {

    private Activity activity;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    public FireBaseStorage(Activity activity) {
        this.activity = activity;
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
    }

    /***********************************
     * UPLOAD THE FILE TO FIREBASE
     * **********************************/

    public final void uploadFile(Uri uri, FirebaseUser user) {
        StorageReference imagesStorage = storageReference.child("images/" + user.getUid() + "/profile.png");
        imagesStorage.putFile(uri).addOnCompleteListener(activity, new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) onUploadFileSuccess(task);
                else onUploadFileFailed(task);
            }
        });
    }

    public final void uploadStory(String userId, Bitmap photo, final FireBaseDatabase.AsyncEventListener<Boolean> handler) {

        ByteArrayOutputStream bytesStory = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 50, bytesStory);

        StorageReference imagesStorage = storageReference.child("stories/" + userId + ".jpg");
        imagesStorage.putBytes(bytesStory.toByteArray()).addOnCompleteListener(activity, new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful())
                    handler.onActionPerformed(true);
                else
                    handler.onActionPerformed(false);
            }
        });
    }

    protected void onUploadFileSuccess(Task<UploadTask.TaskSnapshot> task) {
    }

    protected void onUploadFileFailed(Task<UploadTask.TaskSnapshot> task) {
        Snackbar.make(activity.getCurrentFocus(), task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
    }

    /***********************************
     * DOWNLOAD THE FILE FROM FIREBASE
     * **********************************/


    public final void downloadFile(FirebaseUser user) {
        try {
            final File localFile = File.createTempFile("images", "png");
            StorageReference imagesStorage = storageReference.child("images/" + user.getUid() + "/profile.png");
            imagesStorage.getFile(localFile).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) onDownloadFileSuccess(task, localFile);
                    else onDownloadFileFailed(task);
                }
            });
        } catch (IOException e) {
            Snackbar.make(activity.getCurrentFocus(), e.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }

    public final void downloadStoryImage(final Historia historia, final FireBaseDatabase.AsyncEventListener<Boolean> onImageReceived) {
        try {
            final File localFile = File.createTempFile("stories", "png");
            StorageReference imagesStorage = storageReference.child("stories/" + historia.getImagenUri() + ".jpg");
            imagesStorage.getFile(localFile).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 6;

                        Bitmap bitmap = BitmapFactory.decodeFile(localFile.getPath(), options);
                        historia.setImagen(bitmap);
                        onImageReceived.onActionPerformed(true);
                    } else onDownloadFileFailed(task);
                }
            });
        } catch (IOException e) {
            //Snackbar.make(activity.getCurrentFocus(), e.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }

    public final void downloadUserImage(final Usuario usuario) {
        try {
            final File localFile = File.createTempFile("images", "png");
            StorageReference imagesStorage = storageReference.child("images/" + usuario.getId() + "/profile.png");
            imagesStorage.getFile(localFile).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 6;

                        Bitmap bitmap = BitmapFactory.decodeFile(localFile.getPath(), options);
                        usuario.setImagen(bitmap);
                    } else onDownloadFileFailed(task);
                }
            });
        } catch (IOException e) {
            //Snackbar.make(activity.getCurrentFocus(), e.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }

    protected void onDownloadFileSuccess(Task<FileDownloadTask.TaskSnapshot> task, File file) {
    }

    protected void onDownloadFileFailed(Task<FileDownloadTask.TaskSnapshot> task) {
        //Snackbar.make(activity.getCurrentFocus(), task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
    }
}
