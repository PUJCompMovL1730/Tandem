package co.edu.javeriana.tandemsquad.tandem;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;

import java.io.File;
import java.util.List;

import co.edu.javeriana.tandemsquad.tandem.firebase.FireBaseAuthentication;
import co.edu.javeriana.tandemsquad.tandem.firebase.FireBaseDatabase;
import co.edu.javeriana.tandemsquad.tandem.firebase.FireBaseStorage;
import co.edu.javeriana.tandemsquad.tandem.negocio.Historia;
import co.edu.javeriana.tandemsquad.tandem.negocio.Mensaje;
import co.edu.javeriana.tandemsquad.tandem.utilities.Utils;
import de.hdodenhof.circleimageview.CircleImageView;

public class StoryActivity extends AppCompatActivity {
    ProgressBar storyProgressBar;
    CircleImageView storyProfileImage;
    ImageView storyImage;
    TextView storyText, storyUser, storyDate;
    int progressStatusCounter = 0;
    Handler progressHandler = new Handler();

    private FireBaseAuthentication fireBaseAuthentication;
    private FireBaseStorage fireBaseStorage;
    private FireBaseDatabase fireBaseDatabase;
    private ProgressDialog dialog;

    private Historia currentStory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);
        storyProgressBar = (ProgressBar) findViewById(R.id.story_progress_bar);
        storyText = (TextView) findViewById(R.id.story_text);
        storyDate = (TextView) findViewById(R.id.story_date);
        storyUser = (TextView) findViewById(R.id.story_user);
        storyProfileImage = (CircleImageView) findViewById(R.id.story_profile_image);
        storyImage = (ImageView) findViewById(R.id.story_image);

        storyText.setText((String) getIntent().getStringExtra("message"));
        storyDate.setText((String) getIntent().getStringExtra("date"));

        fireBaseDatabase = new FireBaseDatabase(this);
        if (dialog == null || !dialog.isShowing()) {
            dialog = ProgressDialog.show(StoryActivity.this, "Cargando historia", "Espera un momento por favor...", false, false);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    currentStory = fireBaseDatabase.getStory((String) getIntent().getStringExtra("userId"), new FireBaseDatabase.AsyncEventListener<Boolean>() {
                        @Override
                        public void onActionPerformed(Boolean item) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    storyUser.setText(currentStory.getUsuario().getNombre());
                                    storyImage.setImageBitmap(currentStory.getImagen());
                                    currentStory.getUsuario().addAsyncImageListener(storyProfileImage);
                                    if (dialog != null) {
                                        dialog.dismiss();
                                    }
                                    runClock();
                                }
                            });
                        }
                    });
                }
            }).start();
        }

        fireBaseStorage = new FireBaseStorage(this) {
        };
    }
    public void runClock() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (progressStatusCounter < 100) {
                    progressStatusCounter += 2;
                    progressHandler.post(new Runnable() {
                        public void run() {
                            storyProgressBar.setProgress(progressStatusCounter);
                        }
                    });
                    try {
                        Thread.sleep(75);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                finish();
            }
        }).start();
    }
}
