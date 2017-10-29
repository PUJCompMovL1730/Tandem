package co.edu.javeriana.tandemsquad.tandem;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;

import java.io.File;

import co.edu.javeriana.tandemsquad.tandem.firebase.FireBaseAuthentication;
import co.edu.javeriana.tandemsquad.tandem.firebase.FireBaseStorage;
import co.edu.javeriana.tandemsquad.tandem.permissions.Permissions;
import co.edu.javeriana.tandemsquad.tandem.utilities.ActivityResult;
import co.edu.javeriana.tandemsquad.tandem.utilities.FieldValidator;
import co.edu.javeriana.tandemsquad.tandem.utilities.Utils;
import de.hdodenhof.circleimageview.CircleImageView;

public class EditActivity extends AppCompatActivity {

    private FireBaseAuthentication fireBaseAuthentication;
    private FireBaseStorage fireBaseStorage;

    private CircleImageView inputPhoto;
    private TextInputLayout layoutName;
    private TextInputLayout layoutEmail;
    private TextInputLayout layoutPassword;
    private TextInputLayout layoutConfirmation;
    private TextInputLayout layoutSave;
    private TextInputEditText inputName;
    private TextInputEditText inputEmail;
    private TextInputEditText inputPassword;
    private TextInputEditText inputConfirmation;
    private TextInputEditText inputSave;
    private Button btnCamera;
    private Button btnGallery;
    private Button btnSave;
    private Uri imageUri;
    private Bundle signupBundle;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        fireBaseAuthentication = new FireBaseAuthentication(this) {
            @Override
            public void onSignInSuccess() {
                setToolbarData(fireBaseAuthentication, fireBaseStorage);
            }
        };

        fireBaseStorage = new FireBaseStorage(this) {
            @Override
            protected void onDownloadFileSuccess(Task<FileDownloadTask.TaskSnapshot> task, File file) {
                Uri uri = Uri.fromFile(file);
                Bitmap image = (Bitmap) Utils.getImageFormUri(EditActivity.this, uri);
                inputPhoto.setImageBitmap(image);
            }
        };
    }

    private void initComponents() {
        inputPhoto = (CircleImageView) findViewById(R.id.edit_image);
        layoutName = (TextInputLayout) findViewById(R.id.edit_layout_name);
        layoutEmail = (TextInputLayout) findViewById(R.id.edit_layout_email);
        layoutPassword = (TextInputLayout) findViewById(R.id.edit_layout_contrasena);
        layoutConfirmation = (TextInputLayout) findViewById(R.id.edit_layout_confirmacion);
        layoutSave = (TextInputLayout) findViewById(R.id.edit_layout_contrasena_save);

        inputName = (TextInputEditText) findViewById(R.id.edit_input_name);
        inputEmail = (TextInputEditText) findViewById(R.id.edit_input_email);
        inputPassword = (TextInputEditText) findViewById(R.id.edit_input_contrasena);
        inputConfirmation = (TextInputEditText) findViewById(R.id.edit_input_confirmacion);
        inputSave = (TextInputEditText) findViewById(R.id.edit_input_contrasena_save);

        btnCamera = (Button) findViewById(R.id.edit_btn_camera);
        btnGallery = (Button) findViewById(R.id.edit_btn_gallery);
        btnSave = (Button) findViewById(R.id.edit_btn_save);
        imageUri = null;
        signupBundle = null;
        user = null;
    }

    private void setButtonActions() {
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraAction();
            }
        });

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galleryAction();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAction();
            }
        });
    }

    private void cameraAction() {
        if(Permissions.askPermissionWithJustification(this, Permissions.CAMERA, "Permitenos acceder a la camara para poder tomar la foto")) {
            imageUri = ActivityResult.startCameraActivity(this, imageUri);
        }
    }

    private void galleryAction() {
        if(Permissions.askPermissionWithJustification(this, Permissions.READ_EXTERNAL_STORAGE, "Permitenos acceder a la galeria para poder escoger una imagen")) {
            imageUri = ActivityResult.startCameraActivity(this, imageUri);
        }
    }

    private void saveAction() {
        boolean validData = true;
        boolean modified = false;
        Bundle signupData = getData();
        enableErrorLayouts(false);

        if(!FieldValidator.validateText(signupData.getString("name")) && signupData.getString("name") != null) {
            layoutName.setErrorEnabled(true);
            layoutName.setError(getString(R.string.name_error));
            modified = true;
            validData = false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(!Permissions.permissionGranted(requestCode, permissions, grantResults)) {
            return;
        }
        switch (requestCode) {
            case Permissions.CAMERA:
                cameraAction();
                break;
            case Permissions.READ_EXTERNAL_STORAGE:
                galleryAction();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK) {
            return;
        }
        switch(requestCode) {
            case ActivityResult.GALLERY_INTENT:
                this.imageUri = data.getData();
                Log.i("Uri gallery", imageUri.toString());
                ActivityResult.startCropImage(this, imageUri);
                break;
            case ActivityResult.CROP_INTENT:
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                inputPhoto.setImageBitmap(photo);
                break;
            case ActivityResult.CAMERA_INTENT:
                Log.i("Uri camera", imageUri.toString());
                //ActivityResult.startCropImage(this, imageUri);
                inputPhoto.setImageBitmap(Utils.getImageFormUri(this, imageUri));
                break;
        }
    }

    private void setToolbarData(FireBaseAuthentication fireBaseAuthentication, FireBaseStorage fireBaseStorage) {
        if(fireBaseAuthentication.isAnUserSignedIn()) {
            FirebaseUser user = fireBaseAuthentication.getUser();
            inputName.setText(user.getDisplayName());
            inputEmail.setText(user.getEmail());
            if(user.getPhotoUrl() != null) {
                fireBaseStorage.downloadFile(user);
            }
        }
    }

    private Bundle getData() {
        signupBundle = new Bundle();
        signupBundle.putString("name", inputName.getText().toString());
        signupBundle.putString("password", inputPassword.getText().toString());
        signupBundle.putString("confirmation", inputConfirmation.getText().toString());
        signupBundle.putString("save", inputSave.getText().toString());
        return signupBundle;
    }

    private void enableErrorLayouts(boolean enable) {
        layoutName.setErrorEnabled(enable);
        layoutEmail.setErrorEnabled(enable);
        layoutPassword.setErrorEnabled(enable);
        layoutConfirmation.setErrorEnabled(enable);
        layoutSave.setErrorEnabled(enable);
    }
}
