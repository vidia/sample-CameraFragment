package com.davidtschida.cameralibraryexample;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.github.florent37.camerafragment.CameraFragment;
import com.github.florent37.camerafragment.configuration.Configuration;
import com.github.florent37.camerafragment.listeners.CameraFragmentResultListener;

import java.io.File;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class MainActivity extends AppCompatActivity implements CameraFragmentResultListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //you can configure the fragment by the configuration builder
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            Toast.makeText(this, "Grant the permission you idiot.", Toast.LENGTH_LONG).show();
            return;
        }
        final CameraFragment cameraFragment =
                CameraFragment.newInstance(new Configuration.Builder().build());

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, cameraFragment, "TheCameraThing")
                .commit();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                cameraFragment.takePhotoOrCaptureVideo(MainActivity.this, "/storage/self/primary", "thePicture001");
            }
        }, 5000);
    }

    @Override
    public void onVideoRecorded(String filePath) {
        Toast.makeText(this, "Video", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onPhotoTaken(byte[] bytes, String filePath) {
        Toast.makeText(this, "Photo: " + bytes.length + filePath, Toast.LENGTH_SHORT).show();

        new AsyncTask<String, Void, Void>() {

            @Override
            protected Void doInBackground(String... params) {
                ConfigurationBuilder twitterConfigBuilder = new ConfigurationBuilder();
                twitterConfigBuilder.setDebugEnabled(true);
                twitterConfigBuilder.setOAuthConsumerKey("conkey");
                twitterConfigBuilder.setOAuthConsumerSecret("consecret");
                twitterConfigBuilder.setOAuthAccessToken("accesstoken");
                twitterConfigBuilder.setOAuthAccessTokenSecret("tokensecret");

                Twitter twitter = new TwitterFactory(twitterConfigBuilder.build()).getInstance();
                File file = new File(params[0]);

                StatusUpdate status = new StatusUpdate("This is a sample image. Thanks for watching!");
                status.setMedia(file); // set the image to be uploaded here.
                try {
                    twitter.updateStatus(status);
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute(filePath);


    }
}
