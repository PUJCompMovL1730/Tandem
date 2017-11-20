package co.edu.javeriana.tandemsquad.tandem;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class StoryActivity extends AppCompatActivity {
    ProgressBar storyProgressBar;
    CircleImageView storyProfileImage;
    ImageView storyImage;
    TextView storyText;
    int progressStatusCounter = 0;
    Handler progressHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);
        storyProgressBar = (ProgressBar) findViewById(R.id.story_progress_bar);
        storyText = (TextView) findViewById(R.id.story_text);
        storyProfileImage = (CircleImageView) findViewById(R.id.story_profile_image);
        storyImage = (ImageView) findViewById(R.id.story_image);

        storyText.setText((String)getIntent().getStringExtra("username"));

        byte[] profileImageBytes = getIntent().getByteArrayExtra("profileImage");
        Bitmap profileImageData = BitmapFactory.decodeByteArray(profileImageBytes, 0, profileImageBytes.length);
        storyProfileImage.setImageBitmap(profileImageData);

        byte[] storyImageBytes = getIntent().getByteArrayExtra("image");
        Bitmap storyImageData = BitmapFactory.decodeByteArray(storyImageBytes, 0, storyImageBytes.length);
        storyImage.setImageBitmap(storyImageData);


        new Thread(new Runnable() {
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
