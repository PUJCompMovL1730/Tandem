package co.edu.javeriana.tandemsquad.tandem;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.TextView;

import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;

import java.io.File;

import co.edu.javeriana.tandemsquad.tandem.firebase.FireBaseAuthentication;
import co.edu.javeriana.tandemsquad.tandem.firebase.FireBaseStorage;
import co.edu.javeriana.tandemsquad.tandem.utilities.Utils;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends NavigationActivity {


    private FireBaseAuthentication fireBaseAuthentication;
    private FireBaseStorage fireBaseStorage;

    private TextView name;
    private TextView username;
    private CircleImageView imageProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MapsInitializer.initialize(getApplicationContext());

        ViewStub stub = (ViewStub) findViewById(R.id.layout_stub);
        stub.setLayoutResource(R.layout.activity_profile);
        View contentView = stub.inflate();

        initComponents();
        setButtonActions();

        fireBaseAuthentication = new FireBaseAuthentication(this) {
            @Override
            public void onSignInSuccess() {
                setToolbarData(fireBaseAuthentication, fireBaseStorage);
                setProfileData();
            }
        };

        fireBaseStorage = new FireBaseStorage(this) {
            @Override
            protected void onDownloadFileSuccess(Task<FileDownloadTask.TaskSnapshot> task, File file) {
                Uri uri = Uri.fromFile(file);
                Bitmap image = (Bitmap) Utils.getImageFormUri(ProfileActivity.this, uri);
                imageProfile.setImageBitmap(image);
                viewImage.setImageBitmap(image);

            }
        };
    }

    private void setProfileData() {
        if(fireBaseAuthentication.isAnUserSignedIn()) {
            FirebaseUser user = fireBaseAuthentication.getUser();
            name.setText(user.getDisplayName());
            username.setText(user.getEmail());
            if(user.getPhotoUrl() != null) {
                fireBaseStorage.downloadFile(user);
            }
        }
    }

    @Override
    protected void initComponents() {
        super.initComponents();

        name = (TextView) findViewById(R.id.profile_name);
        username = (TextView) findViewById(R.id.profile_email);
        imageProfile = (CircleImageView) findViewById(R.id.profile_image);
    }

    @Override
    protected void onStart() {
        super.onStart();
        fireBaseAuthentication.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        fireBaseAuthentication.stop();
    }

    @Override
    protected void logout() {
        fireBaseAuthentication.signOut();
        Intent mainIntent = new Intent(this, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
    }
}
