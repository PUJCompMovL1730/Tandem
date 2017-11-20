package co.edu.javeriana.tandemsquad.tandementerprise;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import co.edu.javeriana.tandemsquad.tandementerprise.Firebase.FirebaseAuthManager;
import co.edu.javeriana.tandemsquad.tandementerprise.Firebase.FirebaseDBManager;
import co.edu.javeriana.tandemsquad.tandementerprise.Mundo.Enterprise;
import co.edu.javeriana.tandemsquad.tandementerprise.Utils.FieldValidator;

public class LoginActivity extends AppCompatActivity
{
    private EditText edtEmail;
    private EditText edtPassword;
    private Button btnLogin;
    private Intent homeIntent;

    private FirebaseAuthManager authManager;
    private FirebaseDBManager db;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        homeIntent = new Intent(this, MapsActivity.class);

        db = new FirebaseDBManager( this );

        authManager = new FirebaseAuthManager( this )
        {
            @Override
            public void onSignInSuccess()
            {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if( dialog != null && isEnterprise( ) )
                        {
                            dialog.dismiss();
                            startActivity(homeIntent);
                        }
                        else
                        {
                            if( dialog != null )
                            {
                                dialog.dismiss();
                                authManager.signOut();
                                Toast.makeText(getApplicationContext(), "La cuenta no es una Empresa", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }).start();
            }

            @Override
            protected void onSignInFailed( Task<AuthResult> task )
            {
                if( dialog != null )
                {
                    dialog.dismiss();
                }
            }
        };

        initComponents();
        setupListeners();
    }

    public void initComponents()
    {
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);

        dialog = null;
    }

    public void setupListeners()
    {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                login();
            }
        });
    }

    public boolean isEnterprise( )
    {
        Enterprise ent = db.getEnterprise( authManager.getUser().getUid() );
        return ent != null;
    }

    public void login()
    {
        String email = edtEmail.getText().toString();
        String pass = edtPassword.getText().toString();
        boolean validData = true;

        if( !FieldValidator.validateEmail( email ) )
        {
            validData = false;
        }

        if( !FieldValidator.validatePassword( pass ) )
        {
            validData = false;
        }

        if( validData )
        {
            dialog = ProgressDialog.show( this, "Ingresando a Tandem", "Espera un momento por favor...", false, false);
            authManager.signInWithEmailAndPassword( email, pass );
        }
    }
}
