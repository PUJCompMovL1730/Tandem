package co.edu.javeriana.tandemsquad.tandem;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.ListView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;

import java.io.File;
import java.util.List;

import co.edu.javeriana.tandemsquad.tandem.adapters.TravelAdapter;
import co.edu.javeriana.tandemsquad.tandem.firebase.FireBaseAuthentication;
import co.edu.javeriana.tandemsquad.tandem.firebase.FireBaseDatabase;
import co.edu.javeriana.tandemsquad.tandem.firebase.FireBaseStorage;
import co.edu.javeriana.tandemsquad.tandem.negocio.Recorrido;
import co.edu.javeriana.tandemsquad.tandem.negocio.Usuario;
import co.edu.javeriana.tandemsquad.tandem.utilities.Utils;

public class TravelsActivity extends NavigationActivity {

    private FireBaseDatabase fireBaseDatabase;
    private FireBaseAuthentication fireBaseAuthentication;
    private Usuario currentUser;
    private String currentUserId;
    private FireBaseStorage fireBaseStorage;

    private ListView travels;
    private List<Recorrido> listTravels;
    private TravelAdapter travelAdapter;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewStub stub = (ViewStub) findViewById(R.id.layout_stub);
        stub.setLayoutResource(R.layout.activity_travels);
        View contentView = stub.inflate();

        fireBaseDatabase = new FireBaseDatabase(this);

        fireBaseAuthentication = new FireBaseAuthentication(this) {
            @Override
            public void onSignInSuccess() {
                setToolbarData(fireBaseAuthentication, fireBaseStorage);
            }
        };
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        currentUser = fireBaseDatabase.getUser(currentUserId);

        initComponents();
        setButtonActions();

        fireBaseStorage = new FireBaseStorage(this) {
            @Override
            protected void onDownloadFileSuccess(Task<FileDownloadTask.TaskSnapshot> task, File file) {
                Uri uri = Uri.fromFile(file);
                Bitmap image = (Bitmap) Utils.getImageFormUri(TravelsActivity.this, uri);
                viewImage.setImageBitmap(image);
            }
        };

        /**
        Marcador marcador_inicio = new Marcador(new LatLng(4.711000, -74.072094), Marcador.Tipo.INICIO, "Bogota");
        Marcador marcador_fin = new Marcador(new LatLng(4.599730, -74.161994), Marcador.Tipo.INICIO, "Otro lugar");
        travelAdapter.add(new Recorrido(marcador_inicio, marcador_fin, Recorrido.Estado.CASUAL));
         */
    }

    @Override
    protected void initComponents() {
        super.initComponents();

        getSupportActionBar().setTitle(R.string.activity_label_travels);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Historial"));
        tabLayout.addTab(tabLayout.newTab().setText("Tus recorridos"));
        tabLayout.addTab(tabLayout.newTab().setText("Global"));

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        pagerAdapter = new TabPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(pagerAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

        /**
        travels = (ListView) findViewById(R.id.travels_list_view);
        listTravels = new ArrayList<>();
        travelAdapter = new TravelAdapter(this, listTravels);
        travels.setAdapter(travelAdapter);
         */
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
        Intent mainIntent = new Intent(this, LoginActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
    }

    public class TabPagerAdapter extends FragmentPagerAdapter {
        public TabPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    //Historial de recorridos del usuario (recorridos finalizados)
                    TravelsHistoryFragment historyFragment = TravelsHistoryFragment.newInstance();
                    historyFragment.setCurrentUserId(currentUserId);
                    historyFragment.setFireBaseDatabase(fireBaseDatabase);
                    return historyFragment;
                case 1:
                    //Recorridos que ha publicado el usuario y recorridos a los que sea ha unido
                    TravelsOwnFragment ownFragment = TravelsOwnFragment.newInstance();
                    ownFragment.setCurrentUserId(currentUserId);
                    ownFragment.setFireBaseDatabase(fireBaseDatabase);
                    return ownFragment;
                case 2:
                    //Recorridos publicados por otros usuarios
                    TravelsGlobalFragment globalFragment = TravelsGlobalFragment.newInstance();
                    globalFragment.setCurrentUser(currentUser);
                    globalFragment.setCurrentUserId(currentUserId);
                    globalFragment.setFireBaseDatabase(fireBaseDatabase);
                    return globalFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
