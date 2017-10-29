package co.edu.javeriana.tandemsquad.tandem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import co.edu.javeriana.tandemsquad.tandem.firebase.FireBaseAuthentication;
import co.edu.javeriana.tandemsquad.tandem.utilities.ActivityResult;
import co.edu.javeriana.tandemsquad.tandem.utilities.FieldValidator;

public class MainActivity extends AppCompatActivity {

    private static final int FACEBOOK_LOGIN_REQUEST_CODE = 64206;

    private FireBaseAuthentication fireBaseAuthentication;

    private TextInputLayout layoutEmail;
    private TextInputLayout layoutPassword;
    private TextInputEditText inputEmail;
    private TextInputEditText inputPassword;
    private Button btnLogin;
    private Button btnSignup;
    private Bundle loginBundle;

    private TwitterLoginButton btnTwitter;
    private SignInButton btnGoogle;
    private LoginButton facebookButton;

    private CallbackManager facebookCallBackManager;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fireBaseAuthentication = new FireBaseAuthentication(this) {
            @Override
            public void onSignInSuccess() {

                dialog.dismiss();
                goHome();
            }
        };

        initComponents();
        setButtonActions();
    }

    private void initComponents() {
        layoutEmail = (TextInputLayout) findViewById(R.id.login_layout_email);
        layoutPassword = (TextInputLayout) findViewById(R.id.login_layout_password);
        inputEmail = (TextInputEditText) findViewById(R.id.login_input_email);
        inputPassword = (TextInputEditText) findViewById(R.id.login_input_password);
        btnLogin = (Button) findViewById(R.id.login_btn_login);
        btnSignup = (Button) findViewById(R.id.login_btn_signup);
        btnTwitter = (TwitterLoginButton) findViewById(R.id.login_btn_twitter);
        btnGoogle = (SignInButton) findViewById(R.id.login_btn_google);
        facebookButton = (LoginButton) findViewById(R.id.login_btn_facebook);
        facebookCallBackManager = CallbackManager.Factory.create();
        loginBundle = null;
        dialog = null;
    }

    private void setButtonActions() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });
        btnTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                twitter();
            }
        });
        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                google();
            }
        });

        fireBaseAuthentication.setUpFacebookAuthentication(facebookButton, facebookCallBackManager);
    }

    private void goHome() {
        Intent homeIntent = new Intent(this, HomeActivity.class);
        startActivity(homeIntent);
    }

    private void login() {
        boolean validData = true;
        Bundle loginData = getData();
        enableErrorLayouts(false);

        if(!FieldValidator.validateEmail(loginData.getString("email"))) {
            layoutEmail.setErrorEnabled(true);
            layoutEmail.setError(getString(R.string.email_error));
            validData = false;
        }

        if(!FieldValidator.validatePassword(loginData.getString("password"))) {
            layoutPassword.setErrorEnabled(true);
            layoutPassword.setError(getString(R.string.password_error));
            validData = false;
        }

        if(validData) {
            dialog = ProgressDialog.show(this, "Ingresando a Tandem", "Espera un momento porfavor...", false, false);
            fireBaseAuthentication.signInWithEmailAndPassword(
                loginData.getString("email"),
                loginData.getString("password")
            );
        }
    }

    private Bundle getData() {
        loginBundle = new Bundle();
        loginBundle.putString("email", inputEmail.getText().toString());
        loginBundle.putString("password", inputPassword.getText().toString());
        return loginBundle;
    }

    private void enableErrorLayouts(boolean enable) {
        layoutEmail.setErrorEnabled(enable);
        layoutPassword.setErrorEnabled(enable);
    }

    private void signup() {
        Intent signupIntent = new Intent(this, SignupActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("login_email", inputEmail.getText().toString());
        bundle.putString("login_password", inputPassword.getText().toString());
        signupIntent.putExtras(bundle);
        startActivity(signupIntent);
    }

    private void facebook() {
    }

    private void twitter() {
    }

    private void google() {
        fireBaseAuthentication.signInWithGoogle();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case ActivityResult.GOOGLE_SIGN_IN:
                fireBaseAuthentication.onGoogleSignInSucess(data);
                break;
            case FACEBOOK_LOGIN_REQUEST_CODE:
                facebookCallBackManager.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        fireBaseAuthentication.start();
        if(fireBaseAuthentication.isAnUserSignedIn()){
            goHome();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        fireBaseAuthentication.stop();
    }
}
