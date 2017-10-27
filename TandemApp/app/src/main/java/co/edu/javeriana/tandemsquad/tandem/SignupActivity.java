package co.edu.javeriana.tandemsquad.tandem;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.UploadTask;

import co.edu.javeriana.tandemsquad.tandem.firebase.FireBaseAuthentication;
import co.edu.javeriana.tandemsquad.tandem.firebase.FireBaseStorage;
import co.edu.javeriana.tandemsquad.tandem.permissions.Permissions;
import co.edu.javeriana.tandemsquad.tandem.utilities.ActivityResult;
import co.edu.javeriana.tandemsquad.tandem.utilities.FieldValidator;
import co.edu.javeriana.tandemsquad.tandem.utilities.Utils;
import de.hdodenhof.circleimageview.CircleImageView;

public class SignupActivity extends AppCompatActivity {

    private FireBaseAuthentication fireBaseAuthentication;
    private FireBaseStorage fireBaseStorage;

    private CircleImageView inputPhoto;
    private TextInputLayout layoutName;
    private TextInputLayout layoutUsername;
    private TextInputLayout layoutEmail;
    private TextInputLayout layoutPhone;
    private TextInputLayout layoutPassword;
    private TextInputLayout layoutConfirmation;
    private TextInputEditText inputName;
    private TextInputEditText inputUsername;
    private TextInputEditText inputEmail;
    private TextInputEditText inputPhone;
    private TextInputEditText inputPassword;
    private TextInputEditText inputConfirmation;
    private Button btnSignup;
    private Uri imageUri;
    private Bundle signupBundle;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initComponents();
        setButtonActions();

        inputEmail.setText(getIntent().getExtras().getString("login_email", ""));
        inputPassword.setText(getIntent().getExtras().getString("login_password", ""));

        fireBaseAuthentication = new FireBaseAuthentication(this) {
            @Override
            public void onSignUpSuccess() {
                user = fireBaseAuthentication.getUser();
                if(imageUri != null) {
                    fireBaseStorage.uploadFile(imageUri, user);
                } else {
                    fireBaseAuthentication.updateUserProfile(signupBundle.getString("name"));
                }
            }

            @Override
            public void onUserProfileUpdateSuccess() {
                btnSignup.setEnabled(true);
                goHome();
            }
        };
        fireBaseStorage = new FireBaseStorage(this) {
            @Override
            public void onUploadFileSuccess(Task<UploadTask.TaskSnapshot> task) {
                imageUri = task.getResult().getDownloadUrl();
                fireBaseAuthentication.updateUserProfile(signupBundle.getString("name"), imageUri);
            }

            @Override
            public void onUploadFileFailed(Task<UploadTask.TaskSnapshot> task) {
                super.onUploadFileFailed(task);
                fireBaseAuthentication.updateUserProfile(signupBundle.getString("name"));
            }
        };
    }

    private void initComponents() {
        inputPhoto = (CircleImageView) findViewById(R.id.signup_input_photo);
        layoutName = (TextInputLayout) findViewById(R.id.signup_layout_name);
        layoutUsername = (TextInputLayout) findViewById(R.id.signup_layout_username);
        layoutEmail = (TextInputLayout) findViewById(R.id.signup_layout_email);
        layoutPhone = (TextInputLayout) findViewById(R.id.signup_layout_phone);
        layoutPassword = (TextInputLayout) findViewById(R.id.signup_layout_contrasena);
        layoutConfirmation = (TextInputLayout) findViewById(R.id.signup_layout_confirmacion);
        inputName = (TextInputEditText) findViewById(R.id.signup_input_name);
        inputUsername = (TextInputEditText) findViewById(R.id.signup_input_username);
        inputEmail = (TextInputEditText) findViewById(R.id.signup_input_email);
        inputPhone = (TextInputEditText) findViewById(R.id.signup_input_phone);
        inputPassword = (TextInputEditText) findViewById(R.id.signup_input_contrasena);
        inputConfirmation = (TextInputEditText) findViewById(R.id.signup_input_confirmacion);
        btnSignup = (Button) findViewById(R.id.signup_btn_signup);
        imageUri = null;
        signupBundle = null;
        user = null;
    }

    private void setButtonActions() {
        inputPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPhoto();
            }
        });
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });
    }

    private void selectPhoto() {
        final CharSequence[] items = { "Camara", "Galeria de fotos"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cargar foto desde");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 0:
                        cameraAction();
                        break;
                    case 1:
                        galleryAction();
                        break;
                    default:
                        dialog.dismiss();
                        break;
                }
            }
        });
        AlertDialog fMapTypeDialog = builder.create();
        fMapTypeDialog.setCanceledOnTouchOutside(true);
        fMapTypeDialog.show();
    }

    private void cameraAction() {
        if(Permissions.askPermission(SignupActivity.this, Permissions.CAMERA)) {
            imageUri = ActivityResult.startCameraActivity(SignupActivity.this, imageUri);
        }
    }

    private void galleryAction() {
        if(Permissions.askPermission(SignupActivity.this, Permissions.READ_EXTERNAL_STORAGE)) {
            ActivityResult.startGalleryActivity(SignupActivity.this);
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

    private void signup() {
        boolean validData = true;
        Bundle signupData = getData();
        enableErrorLayouts(false);

        if(!FieldValidator.validateText(signupData.getString("name"))) {
            layoutName.setErrorEnabled(true);
            layoutName.setError(getString(R.string.name_error));
            validData = false;
        }

        if(!FieldValidator.validateUsername(signupData.getString("username"))) {
            layoutUsername.setErrorEnabled(true);
            layoutUsername.setError(getString(R.string.username_error));
            validData = false;
        }

        if(!FieldValidator.validateEmail(signupData.getString("email"))) {
            layoutEmail.setErrorEnabled(true);
            layoutEmail.setError(getString(R.string.email_error));
            validData = false;
        }

        if(!FieldValidator.validatePhone(signupData.getString("phone"))) {
            layoutPhone.setErrorEnabled(true);
            layoutPhone.setError(getString(R.string.phone_error));
            validData = false;
        }

        if(!FieldValidator.validatePassword(signupData.getString("password"))) {
            layoutPassword.setErrorEnabled(true);
            layoutPassword.setError(getString(R.string.password_error));
            validData = false;
        }

        if(!FieldValidator.validatePassword(signupData.getString("confirmation"))) {
            layoutConfirmation.setErrorEnabled(true);
            layoutConfirmation.setError(getString(R.string.confirmation_error));
            validData = false;
        }

        if(!FieldValidator.confirmatePasswords(signupData.getString("password"), signupData.getString("confirmation"))) {
            layoutConfirmation.setErrorEnabled(true);
            layoutConfirmation.setError(getString(R.string.confirmation_error));
            validData = false;
        }

        if(validData) {
            Snackbar.make(this.getCurrentFocus(), getString(R.string.msg_signup), Snackbar.LENGTH_INDEFINITE).show();
            btnSignup.setEnabled(false);
            fireBaseAuthentication.createUserWithEmailAndPassword(
                signupData.getString("email"),
                signupData.getString("password")
            );
        }
    }

    private void goHome() {
        Intent homeIntent = new Intent(this, HomeActivity.class);
        startActivity(homeIntent);
    }

    private Bundle getData() {
        signupBundle = new Bundle();
        signupBundle.putString("name", inputName.getText().toString());
        signupBundle.putString("username", inputUsername.getText().toString());
        signupBundle.putString("email", inputEmail.getText().toString());
        signupBundle.putString("phone", inputPhone.getText().toString());
        signupBundle.putString("password", inputPassword.getText().toString());
        signupBundle.putString("confirmation", inputConfirmation.getText().toString());
        return signupBundle;
    }

    private void enableErrorLayouts(boolean enable) {
        layoutName.setErrorEnabled(enable);
        layoutUsername.setErrorEnabled(enable);
        layoutEmail.setErrorEnabled(enable);
        layoutPhone.setErrorEnabled(enable);
        layoutPassword.setErrorEnabled(enable);
        layoutConfirmation.setErrorEnabled(enable);
    }
}
