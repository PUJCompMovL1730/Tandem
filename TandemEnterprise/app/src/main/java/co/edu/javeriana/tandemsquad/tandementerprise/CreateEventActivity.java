package co.edu.javeriana.tandemsquad.tandementerprise;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.FirebaseDatabase;

import co.edu.javeriana.tandemsquad.tandementerprise.Firebase.FirebaseAuthManager;
import co.edu.javeriana.tandemsquad.tandementerprise.Firebase.FirebaseDBManager;
import co.edu.javeriana.tandemsquad.tandementerprise.Mundo.Enterprise;
import co.edu.javeriana.tandemsquad.tandementerprise.Mundo.Event;

public class CreateEventActivity extends AppCompatActivity
{
    private EditText latStart;
    private EditText lonStart;
    private EditText latEnd;
    private EditText lonEnd;
    private EditText hourStart;
    private EditText hourEnd;
    private EditText description;

    private Button btnCreate;

    private FirebaseAuthManager authManager;
    private FirebaseDBManager db;

    private Enterprise currentUser;

    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        activity = this;

        db = new FirebaseDBManager(this);

        authManager = new FirebaseAuthManager(this)
        {
            @Override
            public void onSignInSuccess()
            {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        currentUser = db.getEnterprise( authManager.getUser().getUid() );
                    }
                }).start();
            }
        };

        initComponents();
        setupListeners();
    }

    public void initComponents()
    {
        latStart = (EditText) findViewById(R.id.edtLatStart);
        lonStart = (EditText) findViewById(R.id.edtLonStart);
        latEnd = (EditText) findViewById(R.id.edtLatEnd);
        lonEnd = (EditText) findViewById(R.id.edtLonEnd);
        hourStart = (EditText) findViewById(R.id.edtHourStart);
        hourEnd = (EditText) findViewById(R.id.edtHourEnd);
        description = (EditText) findViewById(R.id.edtDescripcion);

        btnCreate = (Button) findViewById(R.id.btnCreateEvent);
    }

    public void setupListeners()
    {
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                LatLng start = new LatLng(Double.parseDouble(latStart.getText().toString()), Double.parseDouble(lonStart.getText().toString()));
                LatLng end = new LatLng(Double.parseDouble(latEnd.getText().toString()), Double.parseDouble(lonEnd.getText().toString()));
                Event evt = new Event(hourStart.getText().toString(), hourEnd.getText().toString(), start, end, description.getText().toString());

                currentUser.addEvent(evt);

                activity.finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        authManager.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        authManager.stop();
    }
}
