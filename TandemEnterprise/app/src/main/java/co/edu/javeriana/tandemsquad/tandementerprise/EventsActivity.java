package co.edu.javeriana.tandemsquad.tandementerprise;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import co.edu.javeriana.tandemsquad.tandementerprise.Adapters.EventsAdapter;
import co.edu.javeriana.tandemsquad.tandementerprise.Firebase.FirebaseAuthManager;
import co.edu.javeriana.tandemsquad.tandementerprise.Firebase.FirebaseDBManager;
import co.edu.javeriana.tandemsquad.tandementerprise.Mundo.Enterprise;
import co.edu.javeriana.tandemsquad.tandementerprise.Mundo.Event;

public class EventsActivity extends AppCompatActivity
{
    private FirebaseAuthManager authManager;
    private FirebaseDBManager db;

    private ListView listView;
    private List< Event > eventsList;
    private EventsAdapter adapter;
    private Button btnAddEvent;

    private Enterprise currentUser;

    private Intent createEventIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        createEventIntent = new Intent(this, CreateEventActivity.class);

        db = new FirebaseDBManager(this );

        authManager = new FirebaseAuthManager( this )
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
                updateAdapter();
            }
        };

        initComponents();
        setupListeners();
    }

    public void initComponents()
    {
        listView = (ListView) findViewById(R.id.eventsListView);
        eventsList = new ArrayList<>();
        adapter = new EventsAdapter( this, eventsList );
        listView.setAdapter(adapter);

        btnAddEvent = (Button) findViewById(R.id.btnAddEvent);
    }

    public void setupListeners()
    {
        btnAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(createEventIntent);
            }
        });
    }

    public void updateAdapter()
    {
        if( currentUser!= null ) {
            eventsList = currentUser.getEvents();

            if (eventsList != null) {
                adapter = new EventsAdapter(this, eventsList);
            } else {
                eventsList = new ArrayList<>();
                adapter = new EventsAdapter(this, eventsList);
            }

            listView.setAdapter(adapter);
        }
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
