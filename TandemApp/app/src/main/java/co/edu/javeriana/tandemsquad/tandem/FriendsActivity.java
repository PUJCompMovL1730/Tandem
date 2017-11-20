package co.edu.javeriana.tandemsquad.tandem;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.GravityCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import co.edu.javeriana.tandemsquad.tandem.adapters.FriendAdapter;
import co.edu.javeriana.tandemsquad.tandem.firebase.FireBaseAuthentication;
import co.edu.javeriana.tandemsquad.tandem.firebase.FireBaseDatabase;
import co.edu.javeriana.tandemsquad.tandem.firebase.FireBaseStorage;
import co.edu.javeriana.tandemsquad.tandem.negocio.Historia;
import co.edu.javeriana.tandemsquad.tandem.negocio.Mensaje;
import co.edu.javeriana.tandemsquad.tandem.negocio.Usuario;
import co.edu.javeriana.tandemsquad.tandem.utilities.Utils;
import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsActivity extends NavigationActivity {

    private FireBaseAuthentication fireBaseAuthentication;
    private FireBaseStorage fireBaseStorage;
    private FireBaseDatabase fireBaseDatabase;

    private ListView friendsListView;
    private List<Usuario> friendsList;
    private FriendAdapter userAdapter;
    private LinearLayout storiesContainer;

    private Usuario currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewStub stub = (ViewStub) findViewById(R.id.layout_stub);
        stub.setLayoutResource(R.layout.activity_friends);
        View contentView = stub.inflate();

        initComponents();
        setButtonActions();

        fireBaseDatabase = new FireBaseDatabase(this);

        fireBaseAuthentication = new FireBaseAuthentication(this) {
            @Override
            public void onSignInSuccess() {
                setToolbarData(fireBaseAuthentication, fireBaseStorage);

                currentUser = fireBaseDatabase.getUser(fireBaseAuthentication.getUser().getUid());
                if (currentUser != null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final List<Historia> stories = fireBaseDatabase.getAllStories();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateStories(stories);
                                }
                            });
                        }
                    }).start();
                    updateFriendsAdapter();
                }
            }
        };

        fireBaseStorage = new FireBaseStorage(this) {
            @Override
            protected void onDownloadFileSuccess(Task<FileDownloadTask.TaskSnapshot> task, File file) {
                Uri uri = Uri.fromFile(file);
                Bitmap image = (Bitmap) Utils.getImageFormUri(FriendsActivity.this, uri);
                viewImage.setImageBitmap(image);
            }
        };
    }

    public void updateFriendsAdapter() {
        friendsList = currentUser.getAmigos();

        if (friendsList != null) {
            userAdapter = new FriendAdapter(this, friendsList);
        } else {
            friendsList = new ArrayList<>();
            userAdapter = new FriendAdapter(this, friendsList);
        }

        friendsListView.setAdapter(userAdapter);
    }

    public int dp2px(int dps) {
        final float scale = getResources().getDisplayMetrics().density;
        int pixels = (int) (dps * scale + 0.5f);
        return pixels;
    }

    public void updateStories(List<Historia> stories) {
        storiesContainer.removeAllViews();
        for (final Historia story : stories) {
            LinearLayout storyContainer = new LinearLayout(this);
            storyContainer.setOrientation(LinearLayout.VERTICAL);

            ImageView image = new CircleImageView(this);

            ViewGroup.MarginLayoutParams margins = new LinearLayout.LayoutParams(dp2px(50), dp2px(50));
            int marginPx = getResources().getDimensionPixelSize(R.dimen.gutter);
            margins.setMargins(marginPx, marginPx, marginPx, 0);
            image.setLayoutParams(margins);
            story.addAsyncImageListener(image);
            //story.getUsuario().addAsyncImageListener(image);
            //image.setImageBitmap(story.getUsuario().getImagen());

            storyContainer.addView(image);

            TextView storyUser = new TextView(this);
            storyUser.setText(story.getUsuario().getNombre());
            storyUser.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);

            storyContainer.addView(storyUser);

            storyContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (story.getImagen() != null && story.getUsuario() != null && story.getUsuario().getImagen() != null) {
                        Intent viewStory = new Intent(getApplicationContext(), StoryActivity.class);

                        ByteArrayOutputStream bytesProfile = new ByteArrayOutputStream();
                        story.getUsuario().getImagen().compress(Bitmap.CompressFormat.JPEG, 85, bytesProfile);

                        ByteArrayOutputStream bytesStory = new ByteArrayOutputStream();
                        story.getImagen().compress(Bitmap.CompressFormat.JPEG, 85, bytesStory);

                        viewStory.putExtra("image", bytesStory.toByteArray());
                        viewStory.putExtra("profileImage", bytesProfile.toByteArray());
                        viewStory.putExtra("username", story.getUsuario().getNombre());
                        startActivity(viewStory);
                    }
                }
            });

            storiesContainer.addView(storyContainer);
        }
    }

    @Override
    protected void initComponents() {
        super.initComponents();
        getSupportActionBar().setTitle(R.string.activity_label_friends);

        storiesContainer = (LinearLayout) findViewById(R.id.stories_container);
        friendsListView = (ListView) findViewById(R.id.friends_list_view);
        friendsList = new ArrayList<>();
        userAdapter = new FriendAdapter(this, friendsList);
        friendsListView.setAdapter(userAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.add_friend_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);
                return true;
            case R.id.menu_settings:
                break;
            case R.id.menu_logout:
                logout();
                break;
            case R.id.menu_add_friend:
                startActivity(new Intent(this, AddFriendsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
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
}
