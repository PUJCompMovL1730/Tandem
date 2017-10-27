package co.edu.javeriana.tandemsquad.tandem.firebase;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

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
                if(task.isSuccessful()) onUploadFileSuccess(task);
                else onUploadFileFailed(task);
            }
        });
    }

    protected void onUploadFileSuccess(Task<UploadTask.TaskSnapshot> task) {}

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
                    if(task.isSuccessful()) onDownloadFileSuccess(task, localFile);
                    else onDownloadFileFailed(task);
                }
            });
        } catch (IOException e) {
            Snackbar.make(activity.getCurrentFocus(), e.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }

    protected void onDownloadFileSuccess(Task<FileDownloadTask.TaskSnapshot> task, File file) {}

    protected void onDownloadFileFailed(Task<FileDownloadTask.TaskSnapshot> task) {
        Snackbar.make(activity.getCurrentFocus(), task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
    }
}
