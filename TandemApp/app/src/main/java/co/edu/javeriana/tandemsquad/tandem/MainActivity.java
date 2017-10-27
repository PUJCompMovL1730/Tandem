package co.edu.javeriana.tandemsquad.tandem;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import co.edu.javeriana.tandemsquad.tandem.firebase.FireBaseAuthentication;
import co.edu.javeriana.tandemsquad.tandem.utilities.FieldValidator;

public class MainActivity extends AppCompatActivity {

    private FireBaseAuthentication fireBaseAuthentication;

    private TextInputLayout layoutEmail;
    private TextInputLayout layoutPassword;
    private TextInputEditText inputEmail;
    private TextInputEditText inputPassword;
    private Button btnLogin;
    private Button btnSignup;
    private ImageButton btnFacebook;
    private ImageButton btnTwitter;
    private ImageButton btnGoogle;
    private Bundle loginBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponents();
        setButtonActions();

        fireBaseAuthentication = new FireBaseAuthentication(this) {
            @Override
            public void onSignInSuccess() {
                goHome();
            }

            @Override
            protected void onSignInFailed(Task<AuthResult> task) {
                signup();
            }
        };
    }

    private void initComponents() {
        layoutEmail = (TextInputLayout) findViewById(R.id.login_layout_email);
        layoutPassword = (TextInputLayout) findViewById(R.id.login_layout_password);
        inputEmail = (TextInputEditText) findViewById(R.id.login_input_email);
        inputPassword = (TextInputEditText) findViewById(R.id.login_input_password);
        btnLogin = (Button) findViewById(R.id.login_btn_login);
        btnSignup = (Button) findViewById(R.id.login_btn_signup);
        btnFacebook = (ImageButton) findViewById(R.id.login_btn_facebook);
        btnTwitter = (ImageButton) findViewById(R.id.login_btn_twitter);
        btnGoogle = (ImageButton) findViewById(R.id.login_btn_google);
        loginBundle = null;
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
        btnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                facebook();
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
}
